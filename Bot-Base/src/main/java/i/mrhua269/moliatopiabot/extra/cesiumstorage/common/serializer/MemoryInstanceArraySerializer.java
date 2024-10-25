package i.mrhua269.moliatopiabot.extra.cesiumstorage.common.serializer;

import i.mrhua269.moliatopiabot.extra.cesiumstorage.api.io.ISerializer;
import i.mrhua269.moliatopiabot.extra.ai.OpenAIAPIRequester;

import java.io.*;

public class MemoryInstanceArraySerializer implements ISerializer<OpenAIAPIRequester.MemoryInstance[]> {
    @Override
    public byte[] serialize(OpenAIAPIRequester.MemoryInstance[] input) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(bos);

        dos.writeInt(input.length);
        for (OpenAIAPIRequester.MemoryInstance record : input) {
            dos.writeUTF(record.getRole());
            dos.writeUTF(record.getContent());
        }
        dos.flush();
        dos.close();

        return bos.toByteArray();
    }

    @Override
    public OpenAIAPIRequester.MemoryInstance[] deserialize(byte[] input) throws IOException {
        final ByteArrayInputStream bis = new ByteArrayInputStream(input);
        final DataInputStream dis = new DataInputStream(bis);

        final int len = dis.readInt();
        final OpenAIAPIRequester.MemoryInstance[] records = new OpenAIAPIRequester.MemoryInstance[len];

        for (int i = 0; i < len; i++) {
            records[i] = new OpenAIAPIRequester.MemoryInstance(dis.readUTF(), dis.readUTF());
        }

        return records;
    }
}
