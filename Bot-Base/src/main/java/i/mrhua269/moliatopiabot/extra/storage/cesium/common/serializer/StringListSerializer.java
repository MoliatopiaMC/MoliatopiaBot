package i.mrhua269.moliatopiabot.extra.storage.cesium.common.serializer;

import i.mrhua269.moliatopiabot.extra.storage.cesium.api.io.ISerializer;

import java.io.IOException;
import java.util.List;

public class StringListSerializer implements ISerializer<List<String>> {
    @Override
    public byte[] serialize(List<String> input) throws IOException {
        return new byte[0];
    }

    @Override
    public List<String> deserialize(byte[] input) throws IOException {
        return List.of();
    }
}
