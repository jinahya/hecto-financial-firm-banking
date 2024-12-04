package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.nio.ByteBuffer;
import java.util.Objects;

class FullTextSegment {

    static FullTextSegment newInstanceOf9(final int offset, final int length, final String tag) {
        return new FullTextSegment(offset, length, FullTextSegmentCodec.of9(), tag) {
        };
    }

    static FullTextSegment newInstanceOfX(final int offset, final int length, final String tag) {
        return new FullTextSegment(offset, length, FullTextSegmentCodec.ofX(), tag);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private FullTextSegment(final int offset, final int length, final FullTextSegmentCodec<?> codec, final String tag) {
        super();
        if (offset < 0) {
            throw new IllegalArgumentException("offset(" + offset + ") is negative");
        }
        if (length <= 0) {
            throw new IllegalArgumentException("length(" + length + ") is not positive");
        }
        this.offset = offset;
        this.length = length;
        this.codec = Objects.requireNonNull(codec, "codec is null");
        this.tag = Objects.requireNonNull(tag, "tag is null").strip();

    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return super.toString() + '{' +
                "offset=" + offset +
                ",length=" + length +
                ",codec=" + codec +
                ",tag=" + tag +
                '}';
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final var that = (FullTextSegment) obj;
        return offset == that.offset &&
                length == that.length &&
                Objects.equals(codec, that.codec) &&
                Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, length, codec, tag);
    }

    // -----------------------------------------------------------------------------------------------------------------
    <V> V getValue(final ByteBuffer data) {
        final var dst = new byte[length];
        data.get(offset, dst);
        return (V) codec.decode(dst, dst.length);
    }

    void setValue(final ByteBuffer data, final Object value) {
        final var src = codec.encode(value, length);
        data.put(offset, src);
    }

    // ----------------------------------------------------------------------------------------------------------- range

    // ----------------------------------------------------------------------------------------------------------- codec

    // ------------------------------------------------------------------------------------------------------------- tag

    // -----------------------------------------------------------------------------------------------------------------
    final int offset;

    final int length;

    final FullTextSegmentCodec<?> codec;

    final String tag;
}
