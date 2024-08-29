package i.mrhua269.moliatopiabot.extra.cesiumstorage.api.database;

import i.mrhua269.moliatopiabot.extra.cesiumstorage.api.io.ISerializer;

public interface IKVDatabase<K, V> {
    V getValue(final K key);

    <S> void scan(final K key, final S scanner);

    ISerializer<K> getKeySerializer();

    ISerializer<V> getValueSerializer();

    ICloseableIterator<K> getIterator();
}
