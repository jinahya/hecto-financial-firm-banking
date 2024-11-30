package io.github.jinahya.hectofinancial.firmbanking.fulltext;

@SuppressWarnings({
        "java:S119" // Type parameter names should comply with a naming convention
})
public interface IFullTextSegmentCodec<VALUE> {

    static IFullTextSegmentCodec<Integer> of9() {
        return new IFullTextSegmentCodecOf9();
    }

    static IFullTextSegmentCodec<String> ofX() {
        return new IFullTextSegmentCodecOfX();
    }

    // -----------------------------------------------------------------------------------------------------------------
    byte[] encode(VALUE decoded, int length);

    VALUE decode(byte[] encoded, int length);
}
