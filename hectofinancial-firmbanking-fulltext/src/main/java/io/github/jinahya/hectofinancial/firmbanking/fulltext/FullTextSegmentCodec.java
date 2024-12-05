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

    /**
     * Encodes specified value for specified length of the target segment.
     *
     * @param decoded the value to encode.
     * @param length  the length of the target segment.
     * @return encoded value of {@code decoded}.
     */
    abstract byte[] encode(Object decoded, int length);

    /**
     * Decodes specified value for specified length of the source segment.
     *
     * @param encoded the value to decode.
     * @param length  the length of the source segment.
     * @return decoded value of {@code encoded}.
     */
    abstract V decode(byte[] encoded, int length);
}
