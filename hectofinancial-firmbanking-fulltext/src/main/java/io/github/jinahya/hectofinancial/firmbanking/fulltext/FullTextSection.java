package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({
        "java:S100" // Method names should comply with a naming convention
})
public abstract class FullTextSection {

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    /**
     * Returns a new instance of the {@code 공통부} for specified category.
     *
     * @param category the category.
     * @return a new instance for the {@code 공통부} for {@code category}.
     */
    static FullTextSection newHeadInstance(final FullTextCategory category) {
        return new FullTextSection(FullTextSectionUtils.loadSegments(category.name() + ".segments")) {
        };
    }

    private static final Map<FullTextCategory, Map<String, Map<String, List<FullTextSegment>>>> MAP =
            Collections.synchronizedMap(new EnumMap<>(FullTextCategory.class));

    /**
     * Returns a new instance for specified category, {@code 전문구분코드}, and {@code 업무구분코드}.
     *
     * @param category the category.
     * @param textCode the {@code 전문구분코드}.
     * @param taskCode the {@code 업무구분코드}.
     * @return a new instance.
     */
    static FullTextSection newInstance(final FullTextCategory category, final String textCode, final String taskCode) {
        Objects.requireNonNull(category, "category is null");
        Objects.requireNonNull(textCode, "textCode is null");
        Objects.requireNonNull(taskCode, "taskCode is null");
        final var segments = MAP.computeIfAbsent(category, tc -> new HashMap<>())
                .computeIfAbsent(textCode, tc -> Collections.synchronizedMap(new HashMap<>()))
                .computeIfAbsent(taskCode, tc -> FullTextSectionUtils.loadSegments(
                        category.name() + textCode + '_' + tc + ".segments"
                ));
        return new FullTextSection(segments) {
        };
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private FullTextSection(final List<FullTextSegment> segments) {
        super();
        this.segments = List.copyOf(segments);
        length = this.segments.stream()
                .mapToInt(s -> s.length)
                .sum();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return super.toString() + '{' +
                "segments=" + segments +
                ",length=" + length +
                '}';
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final var that = (FullTextSection) obj;
        return length == that.length &&
                Objects.equals(segments, that.segments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(segments, length);
    }

    // -------------------------------------------------------------------------------------------------------- segments

    @SuppressWarnings({
            "unchecked",
            "java:S1117" // Local variables should not shadow class fields
    })
    public final <T> T getValue(final int index) {
        if (index <= 0) {
            throw new IllegalArgumentException("index(" + index + ") is not positive");
        }
        if (index > segments.size()) {
            throw new IllegalArgumentException("no segment at index(" + index + ")");
        }
        final var segment = segments.get(index - 1);
        final var offset = segment.getOffset();
        final var length = segment.length;
        final var encoded = new byte[length];
        text.buffer.get(offset, encoded);
        return ((FullTextSegmentCodec<T>) segment.codec).decode(encoded, length);
    }

    @SuppressWarnings({
            "java:S1117" // Local variables should not shadow class fields
    })
    public void setValue(final int index, final Object value) {
        if (index <= 0) {
            throw new IllegalArgumentException("index(" + index + ") is not positive");
        }
        if (index > segments.size()) {
            throw new IllegalArgumentException("no segment at index(" + index + ")");
        }
        final var segment = segments.get(index - 1);
        final var offset = segment.getOffset();
        final var length = segment.length;
        final var encoded = segment.codec.encode(value, length);
        assert encoded.length == length;
        text.buffer.put(offset, encoded);
    }

    public FullTextSection value(final int index, final Object value) {
        setValue(index, value);
        return this;
    }

    // ------------------------------------------------------------------------------------------------------------- int

    /**
     * Gets an {@code int} value, of the segment of specified index, from specified buffer.
     *
     * @param index the index of the segment.
     * @return the value of the segment of specified index as {@code int}.
     */
    public final int int__(final int index) {
        return this.<Integer>getValue(index);
    }

    /**
     * Sets specified new value, of the segment of specified index, to specified buffer.
     *
     * @param index the index of the segment.
     * @param value new value for the segment.
     * @return this section.
     */
    public final FullTextSection int__(final int index, final int value) {
        return value(index, value);
    }

    // ------------------------------------------------------------------------------------------------------------ date
    public final LocalDate date_(final int index) {
        final var value = getValue(index);
        return LocalDate.parse(String.valueOf(value), FullTextSegmentCodecConstants.FORMATTER_DATE);
    }

    public final FullTextSection date_(final int index, final LocalDate value) {
        Objects.requireNonNull(value, "value is null");
        setValue(index, FullTextSegmentCodecConstants.FORMATTER_DATE.format(value));
        return this;
    }

    // ------------------------------------------------------------------------------------------------------------ time
    public final LocalTime time_(final int index) {
        final var value = getValue(index);
        return LocalTime.parse(String.valueOf(value), FullTextSegmentCodecConstants.FORMATTER_TIME);
    }

    public final FullTextSection time_(final int index, final LocalTime value) {
        Objects.requireNonNull(value, "value is null");
        setValue(index, FullTextSegmentCodecConstants.FORMATTER_TIME.format(value));
        return this;
    }

    // ---------------------------------------------------------------------------------------------------------- length
    final int getLength() {
        return length;
    }

    // ------------------------------------------------------------------------------------------------------------ text
    void setText(final FullText text) {
        this.text = text;
    }

    // -----------------------------------------------------------------------------------------------------------------
    final List<FullTextSegment> segments;

    final int length;

    private FullText text;
}
