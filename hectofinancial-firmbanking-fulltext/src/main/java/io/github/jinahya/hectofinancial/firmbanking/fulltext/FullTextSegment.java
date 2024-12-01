package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;

abstract class FullTextSegment {

    // -----------------------------------------------------------------------------------------------------------------
    private static FullTextSegment newInstance(final int length, final FullTextSegmentCodec<?> codec) {
        return new FullTextSegment(codec, length) {
        };
    }

    static FullTextSegment newInstanceOf9(final int length) {
        return newInstance(length, FullTextSegmentCodec.of9());
    }

    static FullTextSegment newInstanceOfX(final int length) {
        return newInstance(length, FullTextSegmentCodec.ofX());
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private FullTextSegment(final FullTextSegmentCodec<?> codec, final int length) {
        super();
        Objects.requireNonNull(codec, "codec is null");
        if (length <= 0) {
            throw new IllegalArgumentException("length(" + length + ") is not positive");
        }
        this.codec = codec;
        this.length = length;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return super.toString() + '{' +
                "codec=" + codec +
                ",length=" + length +
                ",offset=" + offset +
                ",previous=" + previous +
                ",tag=" + tag +
                '}';
    }

    // -----------------------------------------------------------------------------------------------------------------
    <V> V getValue(final ByteBuffer buffer) {
        final var dst = new byte[length];
        buffer.get(offset, dst);
        return (V) codec.decode(dst, length);
    }

    void setValue(final ByteBuffer buffer, final Object value) {
        final var src = codec.encode(value, length);
        buffer.put(offset, src);
    }

    // ----------------------------------------------------------------------------------------------------------- codec

    // ---------------------------------------------------------------------------------------------------------- length

    // ---------------------------------------------------------------------------------------------------------- offset
    int getOffset() {
        return offset;
    }

    void setOffset(final int offset) {
        this.offset = offset;
    }

    FullTextSegment offset(final int offset) {
        setOffset(offset);
        return this;
    }

    // -------------------------------------------------------------------------------------------------------- previous
    FullTextSegment getPrevious() {
        return previous;
    }

    void setPrevious(final FullTextSegment previous) {
        this.previous = previous;
        setOffset(Optional.ofNullable(this.previous).map(p -> p.offset + p.length).orElse(0));
    }

    FullTextSegment previous(final FullTextSegment previous) {
        setPrevious(previous);
        return this;
    }

    // ------------------------------------------------------------------------------------------------------------- tag
    public String getTag() {
        return tag;
    }

    void setTag(final String tag) {
        this.tag = tag;
    }

    // -----------------------------------------------------------------------------------------------------------------
    final FullTextSegmentCodec<?> codec;

    final int length;

    private int offset;

    private FullTextSegment previous;

    private String tag;
}
