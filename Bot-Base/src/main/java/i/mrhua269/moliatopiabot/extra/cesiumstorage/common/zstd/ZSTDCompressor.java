package i.mrhua269.moliatopiabot.extra.cesiumstorage.common.zstd;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdCompressCtx;
import com.github.luben.zstd.ZstdDecompressCtx;
import i.mrhua269.moliatopiabot.extra.cesiumstorage.api.io.ICompressor;
import i.mrhua269.moliatopiabot.manager.ConfigManager;

import java.util.Arrays;

public class ZSTDCompressor implements ICompressor {

    private final ThreadLocal<ZSTDContext> ctx = ThreadLocal.withInitial(this::createContext);

    private ZSTDContext createContext() {
        return new ZSTDContext(ConfigManager.INSTANCE.getReadConfig().getAiDatabaseCompression());
    }

    private static long checkError(long rc) {
        if (Zstd.isError(rc)) {
            throw new IllegalStateException(Zstd.getErrorName(rc));
        }

        return rc;
    }

    @Override
    public byte[] compress(final byte[] input) {
        final byte[] dst = new byte[(int) Zstd.compressBound(input.length)];
        final ZstdCompressCtx ctx = this.ctx.get().compress();

        final int size = (int) checkError(ctx.compress(dst, input));

        return Arrays.copyOfRange(dst, 0, size);
    }

    @Override
    public byte[] decompress(byte[] input) {
        byte[] dst = new byte[(int) checkError(Zstd.getFrameContentSize(input))];

        final ZstdDecompressCtx ctx = this.ctx.get().decompress(0);

        checkError(ctx.decompress(dst, input));

        return dst;
    }
}
