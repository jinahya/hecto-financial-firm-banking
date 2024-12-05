package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

class FullTextSegmentCodec9
        extends FullTextSegmentCodec<Integer> {

    static final Charset CHARSET = StandardCharsets.US_ASCII;

    private static final int RADIX = 10;

    // -----------------------------------------------------------------------------------------------------------------
    FullTextSegmentCodec9() {
        super();
    }

    String toSimplifiedString() {
        return "9";
    }

    // -----------------------------------------------------------------------------------------------------------------
    private byte[] encode(final Integer decoded, final int length) {
        assert decoded != null;
        assert length > 0;
        final var format = String.format("%%1$0%1$dd", length);
        final var bytes = String.format(format, decoded).getBytes(CHARSET);
        if (bytes.length > length) {
            throw new IllegalArgumentException("decoded.bytes.length(" + bytes.length + ") > length(" + length + ")");
        }
        return bytes;
    }

    @Override
    byte[] encode(final Object decoded, final int length) {
        Objects.requireNonNull(decoded, "decoded is null");
        if (length <= 0) {
            throw new IllegalArgumentException("length(" + length + ") is not positive");
        }
        if (decoded instanceof Integer i) {
            return encode(i, length);
        }
        if (decoded instanceof Number n) {
            return encode(n.intValue(), length);
        }
        return encode(Integer.valueOf(decoded.toString()), length);
    }

    @Override
    Integer decode(final byte[] encoded, final int length) {
        Objects.requireNonNull(encoded, "encoded is null");
        if (length <= 0) {
            throw new IllegalArgumentException("length(" + length + ") is not positive");
        }
        if (encoded.length > length) {
            throw new IllegalArgumentException("encoded.length(" + encoded.length + ") > length(" + length + ")");
        }
        final var string = new String(encoded, CHARSET);
        try {
            return Integer.parseInt(string, RADIX);
        } catch (final NumberFormatException nfe) {
            return null;
        }
    }
}
