package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;

class IFullTextSegmentCodecOfX
        implements IFullTextSegmentCodec<String> {

    static final Charset CHARSET = Charset.forName("euc-kr");

    @Override
    public byte[] encode(final String decoded, final int length) {
        Objects.requireNonNull(decoded, "decoded is null");
        if (length <= 0) {
            throw new IllegalArgumentException("length(" + length + ") is not positive");
        }
        final var encoded = new byte[length];
        {
            final var bytes = decoded.getBytes(CHARSET);
            if (bytes.length > encoded.length) {
                throw new IllegalArgumentException(
                        "decoded.bytes.length(" + bytes.length + " > encoded.length(" + encoded.length + ")"
                );
            }
            System.arraycopy(bytes, 0, encoded, 0, bytes.length);
            Arrays.fill(encoded, bytes.length, encoded.length, (byte) 0x20);
        }
        return encoded;
    }

    @Override
    public String decode(final byte[] encoded, final int length) {
        Objects.requireNonNull(encoded, "encoded is null");
        if (length <= 0) {
            throw new IllegalArgumentException("length(" + length + ") is not positive");
        }
        return new String(encoded, CHARSET).strip();
    }
}
