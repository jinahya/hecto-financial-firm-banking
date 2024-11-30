package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.util.Objects;
import java.util.Optional;

public abstract class FullTextSegment {

    // -----------------------------------------------------------------------------------------------------------------
    public static FullTextSegment newInstance(final int length, final FullTextSegmentCodec<?> codec) {
        return new FullTextSegment(length, codec) {
        };
    }

    public static FullTextSegment newInstanceOf9(final int length) {
        return newInstance(length, FullTextSegmentCodec.of9());
    }

    public static FullTextSegment newInstanceOfX(final int length) {
        return newInstance(length, FullTextSegmentCodec.ofX());
    }

    // -----------------------------------------------------------------------------------------------------------------
    private FullTextSegment(final int length, final FullTextSegmentCodec<?> codec) {
        super();
        if (length <= 0) {
            throw new IllegalArgumentException("length(" + length + ") is not positive");
        }
        Objects.requireNonNull(codec, "codec is null");
        this.length = length;
        this.codec = codec;
    }

    // -------------------------------------------------------------------------------------------------------- previous
    FullTextSegment getPrevious() {
        return previous;
    }

    void setPrevious(final FullTextSegment previous) {
        this.previous = previous;
        setOffset(Optional.ofNullable(this.previous).map(p -> p.offset + p.length).orElse(0));
    }

    // ---------------------------------------------------------------------------------------------------------- offset
    int getOffset() {
        return offset;
    }

    void setOffset(final int offset) {
        this.offset = offset;
    }

    // ---------------------------------------------------------------------------------------------------------- length
    int getLength() {
        return length;
    }

    // ----------------------------------------------------------------------------------------------------------- codec
    FullTextSegmentCodec<?> getCodec() {
        return codec;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private FullTextSegment previous;

    private int offset;

    private final int length;

    private final FullTextSegmentCodec<?> codec;
}
