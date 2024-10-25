package i.mrhua269.moliatopiabot.extra.storage.cesium.api.database;

public interface IKVTransaction<K, V> {
    void add(final K key, final V value);
}
