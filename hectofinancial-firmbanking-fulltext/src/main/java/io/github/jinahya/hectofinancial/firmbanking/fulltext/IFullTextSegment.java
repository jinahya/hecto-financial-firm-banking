package io.github.jinahya.hectofinancial.firmbanking.fulltext;

public interface IFullTextSegment {

    int getIndex();

    int getLength();

    IFullTextSegmentCodec<?> getCodec();
}
