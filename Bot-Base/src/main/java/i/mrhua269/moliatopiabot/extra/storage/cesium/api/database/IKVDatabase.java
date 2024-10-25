package i.mrhua269.moliatopiabot.extra.storage.cesium.api.database;

import i.mrhua269.moliatopiabot.extra.storage.cesium.api.io.ISerializer;

public interface IKVDatabase<K, V> {
    V getValue(final K key);

    <S> void scan(final K key, final S scanner);

    ISerializer<K> getKeySerializer();

    ISerializer<V> getValueSerializer();

    ICloseableIterator<K> getIterator();
}
