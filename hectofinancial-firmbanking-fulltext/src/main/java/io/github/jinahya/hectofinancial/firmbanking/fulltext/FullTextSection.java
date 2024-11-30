package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

public abstract class FullTextSection {

    private FullTextSection(final List<IFullTextSegment> segments) {
        super();
        assert segments != null;
        assert !segments.isEmpty();
        if (getClass().desiredAssertionStatus()) {
            for (final var segment : segments) {
                assert segment != null;
            }
        }
        this.segments = Collections.unmodifiableList(segments);
        final var last = this.segments.getLast();
        buffer = ByteBuffer.allocate(last.getIndex() + last.getLength());
    }

    // -------------------------------------------------------------------------------------------------------- segments
    <VALUE> FullTextSection set(final int index, final VALUE value) {
        // TODO: implement
        return this;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final List<IFullTextSegment> segments;

    private final ByteBuffer buffer;
}
