package io.github.jinahya.hectofinancial.firmbanking.fulltext;

abstract class FullTextSegmentCodec<V> {

    // -----------------------------------------------------------------------------------------------------------------
    static FullTextSegmentCodec<Integer> of9() {
        return new FullTextSegmentCodec9();
    }

    static FullTextSegmentCodec<String> ofX() {
        return new FullTextSegmentCodecX();
    }

    // -----------------------------------------------------------------------------------------------------------------
    abstract byte[] encode(Object decoded, int length);

    abstract V decode(byte[] encoded, int length);
}
