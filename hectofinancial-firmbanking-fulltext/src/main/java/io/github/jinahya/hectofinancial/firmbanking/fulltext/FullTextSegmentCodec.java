package io.github.jinahya.hectofinancial.firmbanking.fulltext;

public interface FullTextSegmentCodec<V> {

    // -----------------------------------------------------------------------------------------------------------------
    static FullTextSegmentCodec<Integer> of9() {
        return new FullTextSegmentCodecOf9();
    }

    static FullTextSegmentCodec<String> ofX() {
        return new FullTextSegmentCodecOfX();
    }

    // -----------------------------------------------------------------------------------------------------------------
    byte[] encode(V decoded, int length);

    V decode(byte[] encoded, int length);
}
