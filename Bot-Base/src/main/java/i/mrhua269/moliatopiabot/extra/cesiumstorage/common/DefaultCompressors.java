package i.mrhua269.moliatopiabot.extra.cesiumstorage.common;

import i.mrhua269.moliatopiabot.extra.cesiumstorage.api.io.ICompressor;
import i.mrhua269.moliatopiabot.extra.cesiumstorage.common.zstd.ZSTDCompressor;

public class DefaultCompressors {
    public static final ICompressor NONE = new ICompressor() {
        @Override
        public byte[] compress(byte[] input) {
            return input;
        }

        @Override
        public byte[] decompress(byte[] input) {
            return input;
        }
    };

    public static final ICompressor ZSTD = new ZSTDCompressor();
}
