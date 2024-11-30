package io.github.jinahya.hectofinancial.firmbanking.fulltext;

public abstract class FullTextSegment {

    private FullTextSegment(final FullTextSegment previous, final int offset, final int length,
                            final IFullTextSegmentCodec<?> codec) {
        super();
        this.previous = previous;
        this.offset = offset;
        this.length = length;
        this.codec = codec;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final FullTextSegment previous;

    private final int offset;

    private final int length;

    private final IFullTextSegmentCodec<?> codec;
}
