package i.mrhua269.moliatopiabot.extra.cesiumstorage.api.io;

import java.io.IOException;

public interface IScannable<T> {
    void scan(byte[] input, T scanner) throws IOException;
}
