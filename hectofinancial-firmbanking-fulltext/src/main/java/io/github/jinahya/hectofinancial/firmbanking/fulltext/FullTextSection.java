package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({
        "java:S100" // Method names should comply with a naming convention
})
public class FullTextSection {

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS
    // category -> textCode -> taskCode -> segments
    private static final Map<FullTextCategory, List<FullTextSegment>> HEAD_SEGMENTS =
            Collections.synchronizedMap(new EnumMap<>(FullTextCategory.class));

    /**
     * Returns a new instance of the {@code 공통부} for specified category.
     *
     * @param category the category.
     * @return a new instance for the {@code 공통부} for {@code category}.
     */
    static FullTextSection newHeadInstance(final FullTextCategory category) {
        Objects.requireNonNull(category, "category is null");
        final var segments = HEAD_SEGMENTS.computeIfAbsent(category, FullTextSectionUtils::loadHeadSegments);
        final var instance = new FullTextSection(segments);
        instance.reset();
        return instance;
    }

    // category -> textCode -> taskCode -> segments
    private static final Map<FullTextCategory, Map<String, Map<String, List<FullTextSegment>>>> BODY_SEGMENTS =
            Collections.synchronizedMap(new EnumMap<>(FullTextCategory.class));

    /**
     * Returns a new instance for specified category, {@code 전문구분코드}, and {@code 업무구분코드}.
     *
     * @param category the category.
     * @param textCode the {@code 전문구분코드}.
     * @param taskCode the {@code 업무구분코드}.
     * @return a new instance.
     */
    static FullTextSection newBodyInstance(final FullTextCategory category, final String textCode,
                                           final String taskCode) {
        Objects.requireNonNull(category, "category is null");
        Objects.requireNonNull(textCode, "textCode is null");
        Objects.requireNonNull(taskCode, "taskCode is null");
        final var segments = BODY_SEGMENTS
                .computeIfAbsent(category, c -> Collections.synchronizedMap(new HashMap<>()))
                .computeIfAbsent(textCode, tc -> Collections.synchronizedMap(new HashMap<>()))
                .computeIfAbsent(taskCode, tc -> FullTextSectionUtils.loadBodySegments(category, textCode, tc));
        final var instance = new FullTextSection(segments);
        instance.reset();
        return instance;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private FullTextSection(final List<FullTextSegment> segments) {
        super();
        this.segments = List.copyOf(segments);
        length = this.segments.stream().mapToInt(FullTextSegment::getLength).sum();
        buffer = ByteBuffer.allocate(length);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return super.toString() + '{' +
                "segments=" + segments +
                ",length=" + length +
                ",data=" + buffer +
                '}';
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final var that = (FullTextSection) obj;
        return length == that.length &&
                Objects.equals(segments, that.segments) &&
                Objects.equals(buffer, that.buffer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(segments, length, buffer);
    }

    // -------------------------------------------------------------------------------------------------------- segments
    List<FullTextSegment> getSegments() {
        return segments;
    }

    private int requireValidIndex(final int index) {
        if (index <= 0) {
            throw new IllegalArgumentException("index(" + index + ") is not positive");
        }
        if (index > segments.size()) {
            throw new IllegalArgumentException("no segment at index(" + index + ")");
        }
        return index;
    }

    public <T> T getValue(final int index) {
        final var segment = segments.get(requireValidIndex(index) - 1);
        return segment.getValue(buffer);
    }

    public void setValue(final int index, final Object value) {
        final var segment = segments.get(requireValidIndex(index) - 1);
        segment.setValue(buffer, value);
    }

    /**
     * Returns the value, of a segment of specified index, in {@code int}.
     *
     * @param index the index of the segment.
     * @return the value of the segment of specified index in {@code int}.
     */
    public int getInt(final int index) {
        return this.<Integer>getValue(index);
    }

    /**
     * Sets specified value of {@code int} to the segment of specified index.
     *
     * @param index the index of the segment.
     * @param value new value for the segment.
     */
    public void setInt(final int index, final int value) {
        setValue(index, value);
    }

    /**
     * Returns the value of specified segment index as an instance of {@link LocalDate}.
     *
     * @param index the segment index; starts from {@code 1}.
     * @return the date value of {@code index}; {@code null} if not set.
     * @see #setDate(int, LocalDate)
     */
    public LocalDate getDate(final int index) {
        return Optional.ofNullable(this.<Integer>getValue(index))
                .filter(v -> v != 0)
                .map(Objects::toString)
                .map(String::strip)
                .filter(v -> !v.isBlank())
                .map(v -> LocalDate.parse(v, FullTextSegmentCodecConstants.FORMATTER_DATE))
                .orElse(null);
    }

    /**
     * Replaces the value of specified segment index with specified value.
     *
     * @param index the segment index; starts from {@code 1}.
     * @param value new value for the segment at {@code index}.
     * @see #getDate(int)
     */
    public void setDate(final int index, final LocalDate value) {
        if (value == null) {
            setValue(index, 0);
            return;
        }
        setValue(index, FullTextSegmentCodecConstants.FORMATTER_DATE.format(value));
    }

    /**
     * Returns the value of specified segment index as an instance of {@link LocalTime}.
     *
     * @param index the segment index; starts from {@code 1}.
     * @return the time value of {@code index}; {@code null} if not set.
     * @see #setTime(int, LocalTime)
     */
    public LocalTime getTime(final int index) {
        return Optional.ofNullable(this.<Integer>getValue(index))
                .filter(v -> v != 0)
                .map(Objects::toString)
                .map(String::strip)
                .filter(s -> !s.isBlank())
                .map(v -> LocalTime.parse(v, FullTextSegmentCodecConstants.FORMATTER_TIME))
                .orElse(null);
    }

    /**
     * Replaces the value of specified segment index with specified value.
     *
     * @param index the segment index; starts from {@code 1}.
     * @param value new value for the segment at {@code index}.
     * @see #getDate(int)
     */
    public void setTime(final int index, final LocalTime value) {
        if (value == null) {
            setValue(index, 0);
            return;
        }
        setValue(index, FullTextSegmentCodecConstants.FORMATTER_TIME.format(value));
    }

    // ---------------------------------------------------------------------------------------------------------- length

    /**
     * Returns the length of this section.
     *
     * @return the length of this section.
     */
    public int getLength() {
        return length;
    }

    // ---------------------------------------------------------------------------------------------------------- buffer
    ByteBuffer getBuffer() {
        return buffer;
    }

    /**
     * Resets this section by setting data with spaces.
     */
    public void reset() {
        Arrays.fill(buffer.array(), (byte) 0x20);
    }

    /**
     * Returns a string representation of this section.
     *
     * @return a string representation of this section.
     */
    public String getDataString() {
        return FullTextSegmentCodecX.CHARSET.decode(buffer.clear()).toString();
    }

    ByteBuffer getData(final ByteBuffer dst) {
        if (Objects.requireNonNull(dst, "dst is null").remaining() < length) {
            throw new IllegalArgumentException(
                    "dst.remaining(" + dst.remaining() + ") < length(" + length + ")"
            );
        }
        return dst.put(buffer.clear());
    }

    byte[] getData(final byte[] dst) {
        if (Objects.requireNonNull(dst, "dst is null").length < length) {
            throw new IllegalArgumentException("dst.length(" + dst.length + ") < length(" + length + ")");
        }
        return getData(ByteBuffer.wrap(dst)).array();
    }

    byte[] getData() {
        return getData(new byte[length]);
    }

    void setData(final ByteBuffer src) {
        if (Objects.requireNonNull(src, "src is null").remaining() != length) {
            throw new IllegalArgumentException("src.remaining(" + src.remaining() + ") != length(" + length + ")");
        }
        buffer.clear().put(src);
    }

    void setData(final byte[] src) {
        if (Objects.requireNonNull(src, "src is null").length != length) {
            throw new IllegalArgumentException("src.length(" + src.length + ") > length(" + length + ")");
        }
        setData(ByteBuffer.wrap(src));
    }

    /**
     * Applies this section's data buffer to specified function, and returns the result.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     */
    public <R> R applyData(final Function<? super ByteBuffer, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(getBuffer());
    }

    /**
     * Accepts this section's data buffer to specified consumer.
     *
     * @param consumer the consumer.
     */
    public void acceptData(final Consumer<? super ByteBuffer> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyData(d -> {
            consumer.accept(d);
            return null;
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final List<FullTextSegment> segments;

    private final int length;

    private final ByteBuffer buffer;
}
