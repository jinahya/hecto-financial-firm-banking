package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.nio.charset.Charset;
import java.util.Arrays;

class FullTextSegmentCodecX
        extends FullTextSegmentCodec<String> {

    static final Charset CHARSET = Charset.forName("euc-kr");

    // -----------------------------------------------------------------------------------------------------------------
    FullTextSegmentCodecX() {
        super();
    }

    String toSimplifiedString() {
        return "X";
    }

    // -----------------------------------------------------------------------------------------------------------------
    private byte[] encode_(final String decoded, final int length) {
        assert decoded != null;
        assert length > 0;
        final var encoded = new byte[length];
        {
            final var bytes = decoded.stripTrailing().getBytes(CHARSET);
            if (bytes.length > encoded.length) {
                throw new IllegalArgumentException(
                        "decoded.bytes.length(" + bytes.length + ") > encoded.length(" + encoded.length + ")"
                );
            }
            System.arraycopy(bytes, 0, encoded, 0, bytes.length);
            Arrays.fill(encoded, bytes.length, encoded.length, (byte) 0x20);
        }
        return encoded;
    }

    @Override
    byte[] encode(final Object decoded, final int length) {
        assert length > 0;
        if (decoded == null) {
            final var a = new byte[length];
            Arrays.fill(a, (byte) 0x20);
            return a;
        }
        return encode_(decoded.toString(), length);
    }

    @Override
    String decode(final byte[] encoded) {
        assert encoded != null;
        return new String(encoded, CHARSET).stripTrailing();
    }
}
