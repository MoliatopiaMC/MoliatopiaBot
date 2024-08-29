package i.mrhua269.moliatopiabot.extra.cesiumstorage.common;

import i.mrhua269.moliatopiabot.extra.cesiumstorage.api.io.ISerializer;
import i.mrhua269.moliatopiabot.extra.cesiumstorage.common.serializer.MemoryInstanceArraySerializer;
import i.mrhua269.moliatopiabot.extra.cesiumstorage.common.serializer.StringSerializer;
import i.mrhua269.moliatopiabot.extra.cesiumstorage.common.serializer.UUIDSerializer;
import i.mrhua269.moliatopiabot.extra.OpenAIAPIRequester;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.UUID;

public class DefaultSerializers {
    private static final Reference2ReferenceMap<Class<?>, ISerializer<?>> serializers = new Reference2ReferenceOpenHashMap<>();

    static {
        serializers.put(UUID.class, new UUIDSerializer());
        serializers.put(String.class, new StringSerializer());
        serializers.put(OpenAIAPIRequester.MemoryInstance[].class, new MemoryInstanceArraySerializer());
    }

    @SuppressWarnings("unchecked")
    public static <K> ISerializer<K> getSerializer(Class<K> clazz) {
        ISerializer<?> serializer = DefaultSerializers.serializers.get(clazz);

        if (serializer == null) {
            throw new NullPointerException("No serializer exists for type: " + clazz.getName());
        }

        return (ISerializer<K>) serializer;
    }
}
