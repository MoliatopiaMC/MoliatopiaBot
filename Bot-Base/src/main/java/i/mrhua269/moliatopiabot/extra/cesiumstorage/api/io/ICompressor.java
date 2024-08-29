package i.mrhua269.moliatopiabot.extra.cesiumstorage.api.io;

public interface ICompressor {
    byte[] compress(final byte[] input);

    byte[] decompress(final byte[] input);
}
