package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.util.List;

public interface IFullTextSection {

    List<IFullTextSegment> getSegments();

    default int getLength() {
        final var last = getSegments().getLast();
        return last.getIndex() + last.getLength();
    }
}
