package i.mrhua269.moliatopiabot.extra.cesiumstorage.common.serializer;

import i.mrhua269.moliatopiabot.extra.cesiumstorage.api.io.ISerializer;

import java.nio.charset.StandardCharsets;

public class StringSerializer implements ISerializer<String> {
    @Override
    public byte[] serialize(String input) {
        return input.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String deserialize(byte[] input) {
        return new String(input, StandardCharsets.UTF_8);
    }
}
