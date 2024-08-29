package i.mrhua269.moliatopiabot.extra.cesiumstorage.common.lmdb;

import i.mrhua269.moliatopiabot.extra.cesiumstorage.api.database.ICloseableIterator;
import i.mrhua269.moliatopiabot.extra.cesiumstorage.api.io.ISerializer;
import org.lmdbjava.Cursor;

import java.io.IOException;

public class CursorIterator<K> implements ICloseableIterator<K> {
    private final Cursor<byte[]> cursor;
    private final ISerializer<K> serializer;

    private boolean hasNext;

    public CursorIterator(final Cursor<byte[]> cursor, final ISerializer<K> serializer) {
        this.cursor = cursor;
        this.serializer = serializer;

        this.hasNext = this.cursor.first();
    }

    @Override
    public boolean hasNext() {
        return this.hasNext;
    }

    @Override
    public K next() {
        try {
            final K key = this.serializer.deserialize(this.cursor.key());

            this.hasNext = this.cursor.next();

            return key;
        } catch (final IOException e) {
            throw new RuntimeException("Could not deserialize key", e);
        }
    }

    @Override
    public void close() throws Exception {
        this.cursor.close();
    }
}
