package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class FullTextSection {

    /**
     * An interface for building instances of {@link FullTextSection}.
     *
     * @param <T> self type parameter
     */
    public interface Builder<T extends Builder<T>> {

        T add(final FullTextSegment segment);

        FullTextSection build();
    }

    private abstract static class BuilderImpl
            implements Builder<BuilderImpl> {

        private BuilderImpl() {
            super();
        }

        @Override
        public BuilderImpl add(final FullTextSegment segment) {
            Objects.requireNonNull(segment, "segment is null");
            segment.setPrevious(previous);
            segments.add(segment);
            previous = segment;
            return this;
        }

        @Override
        public FullTextSection build() {
            if (segments.isEmpty()) {
                throw new IllegalStateException("no segments have been added");
            }
            try {
                return new FullTextSection(this) {
                };
            } finally {
                segments.clear();
            }
        }

        private final List<FullTextSegment> segments = new ArrayList<>();

        private FullTextSegment previous;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @SuppressWarnings({
            "java:S1452" // Generic wildcard types should not be used in return types
    })
    public static Builder<?> builder() {
        return new BuilderImpl() {
        };
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * {@code 공통부} 개체를 반환한다.
     *
     * @return {@code 공통부} 개체.
     */
    public static FullTextSection newInstanceForCommonSection() {
        return builder()
                .add(FullTextSegment.newInstanceOfX(9))  //  1 식별코드
                .add(FullTextSegment.newInstanceOfX(12)) //  2 업체번호
                .add(FullTextSegment.newInstanceOfX(3))  //  3 은행코드 // 9(3)
                .add(FullTextSegment.newInstanceOfX(4))  //  4 전문구분코드
                .add(FullTextSegment.newInstanceOfX(3))  //  5 업무구분코드
                .add(FullTextSegment.newInstanceOf9(1))  //  6 송신회수
                .add(FullTextSegment.newInstanceOf9(6))  //  7 전문번호
                .add(FullTextSegment.newInstanceOfX(8))  //  8 전송일자 // 9(9)
                .add(FullTextSegment.newInstanceOfX(6))  //  9 전송시간 // 9(6)
                .add(FullTextSegment.newInstanceOfX(4))  // 10 응답코드
                .add(FullTextSegment.newInstanceOfX(15)) // 11 예비영역1
                .add(FullTextSegment.newInstanceOfX(11)) // 12 예비영역2
                .add(FullTextSegment.newInstanceOfX(18)) // 13 예비영역3
                .build();
    }

    // -----------------------------------------------------------------------------------------------------------------
    private FullTextSection(final BuilderImpl builder) {
        super();
        Objects.requireNonNull(builder, "builder is null");
        this.segments = Collections.unmodifiableList(new ArrayList<>(builder.segments));
        length = segments.stream()
                .mapToInt(FullTextSegment::getLength)
                .sum();
    }

    // -------------------------------------------------------------------------------------------------------- segments
    List<FullTextSegment> getSegments() {
        return segments;
    }

    @SuppressWarnings({
            "unchecked",
            "java:S1117" // Local variables should not shadow class fields
    })
    <V> V getValue(final int index, final ByteBuffer buffer) {
        if (index <= 0) {
            throw new IllegalArgumentException("index(" + index + ") is not positive");
        }
        if (index > segments.size()) {
            throw new IllegalArgumentException("no segment at index(" + index + ")");
        }
        Objects.requireNonNull(buffer, "buffer is null");
        final var segment = segments.get(index - 1);
        final var offset = segment.getOffset();
        final var length = segment.getLength();
        final var encoded = new byte[length];
        buffer.get(offset, encoded);
        return ((FullTextSegmentCodec<V>) segment.getCodec()).decode(encoded, length);
    }

    @SuppressWarnings({
            "unchecked",
            "java:S1117" // Local variables should not shadow class fields
    })
    <V> void setValue(final int index, final ByteBuffer buffer, final V value) {
        if (index <= 0) {
            throw new IllegalArgumentException("index(" + index + ") is not positive");
        }
        if (index > segments.size()) {
            throw new IllegalArgumentException("no segment at index(" + index + ")");
        }
        Objects.requireNonNull(buffer, "buffer is null");
        final var segment = segments.get(index - 1);
        final var offset = segment.getOffset();
        final var length = segment.getLength();
        final var encoded = ((FullTextSegmentCodec<V>) segment.getCodec()).encode(value, length);
        assert encoded.length == length;
        buffer.put(offset, encoded);
    }

    public <V> FullTextSection value(final int index, final ByteBuffer buffer, final V value) {
        setValue(index, buffer, value);
        return this;
    }

    // ------------------------------------------------------------------------------------------------------------- int
    public int int__(final int index, final ByteBuffer buffer) {
        return this.<Integer>getValue(index, buffer);
    }

    public FullTextSection int__(final int index, final ByteBuffer buffer, final int value) {
        return value(index, buffer, value);
    }

    // ------------------------------------------------------------------------------------------------------------ date
    public LocalDate date_(final int index, final ByteBuffer buffer) {
        final var value = this.<String>getValue(index, buffer);
        return LocalDate.parse(value, FullTextSegmentCodecConstants.FORMATTER_DATE);
    }

    public FullTextSection date_(final int index, final ByteBuffer buffer, final LocalDate value) {
        Objects.requireNonNull(value, "value is null");
        setValue(index, buffer, FullTextSegmentCodecConstants.FORMATTER_DATE.format(value));
        return this;
    }

    // ------------------------------------------------------------------------------------------------------------ time
    public LocalTime time_(final int index, final ByteBuffer buffer) {
        final var value = this.<String>getValue(index, buffer);
        return LocalTime.parse(value, FullTextSegmentCodecConstants.FORMATTER_TIME);
    }

    public FullTextSection time_(final int index, final ByteBuffer buffer, final LocalTime value) {
        Objects.requireNonNull(value, "value is null");
        setValue(index, buffer, FullTextSegmentCodecConstants.FORMATTER_TIME.format(value));
        return this;
    }

    // --------------------------------------------------------------------------------------------------------- length
    int getLength() {
        return length;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final List<FullTextSegment> segments;

    private final int length;
}
