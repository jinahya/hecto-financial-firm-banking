package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

class IFullTextSegmentCodecOf9
        implements IFullTextSegmentCodec<Integer> {

    static final Charset CHARSET = StandardCharsets.US_ASCII;

    private static final int RADIX = 10;

    @Override
    public byte[] encode(final Integer decoded, final int length) {
        Objects.requireNonNull(decoded, "decoded is null");
        if (length <= 0) {
            throw new IllegalArgumentException("length(" + length + ") is not positive");
        }
        final var bytes = String.format(String.format("%%1$0%1$dd", length), decoded).getBytes(CHARSET);
        if (bytes.length > length) {
            throw new IllegalArgumentException(
                    "decoded.bytes.length(" + bytes.length + ") > length(" + length + ")");
        }
        return bytes;
    }

    @Override
    public Integer decode(final byte[] encoded, final int length) {
        Objects.requireNonNull(encoded, "encoded is null");
        if (length <= 0) {
            throw new IllegalArgumentException("length(" + length + ") is not positive");
        }
        if (encoded.length > length) {
            throw new IllegalArgumentException("encoded.length(" + encoded.length + ") > length(" + length + ")");
        }
        return Integer.parseInt(new String(encoded, CHARSET), RADIX);
    }
}
