package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class FullText {

    protected FullText(final List<IFullTextSection> sections) {
        super();
        this.sections = Collections.unmodifiableList(Objects.requireNonNull(sections, "sections is null"));
        final var capacity = this.sections.stream()
                .map(s -> {
                    return s.getSegments().getLast();
                }).mapToInt(s -> {
                    return s.getIndex() + s.getLength();
                })
                .sum();
        buffer = ByteBuffer.allocate(capacity);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final List<IFullTextSection> sections;

    private final ByteBuffer buffer;
}
