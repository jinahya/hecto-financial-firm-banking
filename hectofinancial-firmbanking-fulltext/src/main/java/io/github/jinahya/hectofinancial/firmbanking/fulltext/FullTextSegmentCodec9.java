package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
    private byte[] encode_(final int decoded, final int length) {
        assert decoded >= 0;
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
        if (length <= 0) {
            throw new IllegalArgumentException("length(" + length + ") is not positive");
        }
        if (decoded == null) {
            final var a = new byte[length];
            Arrays.fill(a, (byte) 0x20);
            return a;
        }
        if (decoded instanceof Number i) {
            return encode_(i.intValue(), length);
        }
        try {
            return encode_(Integer.parseInt(decoded.toString()), length);
        } catch (final NumberFormatException nfe) {
            System.err.println("failed to parse " + decoded);
            return encode(null, length);
        }
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
            System.err.println("failed to decode " + string);
            return null;
        }
    }
}
