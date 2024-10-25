package i.mrhua269.moliatopiabot.extra.storage.cesium.common.lmdb;

import i.mrhua269.moliatopiabot.extra.storage.cesium.api.database.DatabaseSpec;
import i.mrhua269.moliatopiabot.extra.storage.cesium.api.database.IDBInstance;
import i.mrhua269.moliatopiabot.extra.storage.cesium.api.database.IKVDatabase;
import i.mrhua269.moliatopiabot.extra.storage.cesium.api.database.IKVTransaction;
import i.mrhua269.moliatopiabot.base.manager.ConfigManager;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.lmdbjava.ByteArrayProxy;
import org.lmdbjava.CopyFlags;
import org.lmdbjava.Env;
import org.lmdbjava.EnvFlags;
import org.lmdbjava.EnvInfo;
import org.lmdbjava.LmdbException;
import org.lmdbjava.Stat;
import org.lmdbjava.Txn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LMDBInstance implements IDBInstance {
    protected final Env<byte[]> env;

    protected final Reference2ObjectMap<DatabaseSpec<?, ?>, KVDatabase<?, ?>> databases = new Reference2ObjectOpenHashMap<>();
    protected final Reference2ObjectMap<DatabaseSpec<?, ?>, KVTransaction<?, ?>> transactions = new Reference2ObjectOpenHashMap<>();

    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    protected final int MAX_COMMIT_TRIES = 3;
    protected final int resizeStep;

    protected volatile boolean isDirty = false;

    public LMDBInstance(Path dir, String name, DatabaseSpec<?, ?>[] databases) {
        if (!Files.isDirectory(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException ioException) {
                throw new RuntimeException("Failed to create directory.", ioException);
            }
        }

        this.env = Env.create(ByteArrayProxy.PROXY_BA)
                .setMaxDbs(databases.length)
                .open(dir.resolve(name + (".db")).toFile(), EnvFlags.MDB_NOLOCK, EnvFlags.MDB_NOSUBDIR);

        this.resizeStep = Arrays.stream(databases).mapToInt(DatabaseSpec::getInitialSize).sum();

        EnvInfo info = this.env.info();
        if (info.mapSize < this.resizeStep) {
            this.env.setMapSize(this.resizeStep);
        }

        for (DatabaseSpec<?, ?> spec : databases) {
            KVDatabase<?, ?> database = new KVDatabase<>(this, spec);

            this.databases.put(spec, database);
            this.transactions.put(spec, new KVTransaction<>(database));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> IKVDatabase<K, V> getDatabase(DatabaseSpec<K, V> spec) {
        KVDatabase<?, ?> database = this.databases.get(spec);

        if (database == null) {
            throw new NullPointerException("No database is registered for spec " + spec);
        }

        return (IKVDatabase<K, V>) database;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> IKVTransaction<K, V> getTransaction(DatabaseSpec<K, V> spec) {
        KVTransaction<?, ?> transaction = this.transactions.get(spec);

        if (transaction == null) {
            throw new NullPointerException("No transaction is registered for spec " + spec);
        }

        return (IKVTransaction<K, V>) transaction;
    }

    @Override
    public void flushChanges() {
        if (!this.isDirty) {
            return;
        }

        this.lock.writeLock()
                .lock();

        try {
            this.commitTransaction();
            this.isDirty = false;
        } finally {
            this.lock.writeLock()
                    .unlock();
        }
    }

    private void commitTransaction() {
        this.snapshotCreate();

        for (int tries = 0; tries < MAX_COMMIT_TRIES; tries++) {
            try (final Txn<?> txn = this.prepareTransaction()) {
                txn.commit();

                break;
            } catch (final LmdbException l) {
                if (l instanceof Env.MapFullException) {
                    this.growMap();

                    tries--;
                    continue;
                }

                LogManager.getLogger(LMDBInstance.class).info("Commit of transaction failed; trying again ({}/{}): {}", tries, this.MAX_COMMIT_TRIES, l.getMessage());
            }

            if (tries == (MAX_COMMIT_TRIES - 1)) {
                throw new RuntimeException("Could not commit transactions!");
            }
        }

        this.snapshotClear();
    }

    private Txn<?> prepareTransaction() throws LmdbException {
        final Iterator<KVTransaction<?, ?>> it = this.transactions.values()
                .iterator();

        final Txn<byte[]> txn = this.env.txnWrite();

        try {
            while (it.hasNext()) {
                KVTransaction<?, ?> transaction = it.next();
                transaction.addChanges(txn);
            }
        } catch (LmdbException l) {
            txn.abort();
            throw l;
        }

        return txn;
    }

    private void snapshotCreate() {
        for (final KVTransaction<?, ?> txn : this.transactions.values()) {
            txn.createSnapshot();
        }
    }

    private void snapshotClear() {
        for (final KVTransaction<?, ?> txn : this.transactions.values()) {
            txn.clearSnapshot();
        }
    }

    private void growMap() {
        EnvInfo info = this.env.info();

        long oldSize = info.mapSize;
        long newSize = oldSize + (long) this.resizeStep;

        this.env.setMapSize(newSize);

        if (ConfigManager.INSTANCE.getReadConfig().getAiLogDatabaseGrow()) {
            LogManager.getLogger(LMDBInstance.class).info("Grew map size from {} to {} MB", (oldSize / 1024 / 1024), (newSize / 1024 / 1024));
        }
    }

    public void createCopy(final Path path) {
        this.lock.writeLock()
                .lock();

        try {
            this.env.copy(path.toFile(), CopyFlags.MDB_CP_COMPACT);
        } finally {
            this.lock.writeLock()
                    .unlock();
        }
    }

    @Override
    public List<Stat> getStats() {
        this.lock.readLock()
                .lock();

        try {
            return this.databases.values().stream()
                    .map(KVDatabase::getStats)
                    .toList();
        } finally {
            this.lock.readLock()
                    .unlock();
        }

    }

    @Override
    public ReentrantReadWriteLock getLock() {
        return this.lock;
    }

    @Override
    public boolean closed() {
        return this.env.isClosed();
    }

    @Override
    public void close() {
        this.flushChanges();

        for (KVDatabase<?, ?> database : this.databases.values()) {
            database.close();
        }

        this.env.close();
    }
}
