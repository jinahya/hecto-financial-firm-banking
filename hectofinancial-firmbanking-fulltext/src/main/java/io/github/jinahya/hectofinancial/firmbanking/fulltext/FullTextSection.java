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
        return new FullTextSection(segments).reset();
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
        return new FullTextSection(segments).reset();
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

    public FullTextSection value(final int index, final Object value) {
        setValue(index, value);
        return this;
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
     * Sets specified value of {@code int} to the segment of specified index.
     *
     * @param index the index of the segment.
     * @param value new value for the segment.
     * @return this section.
     */
    public FullTextSection int__(final int index, final int value) {
        setInt(index, value);
        return this;
    }

    public LocalDate getDate(final int index) {
        final var value = getValue(index);
        return LocalDate.parse(String.valueOf(value), FullTextSegmentCodecConstants.FORMATTER_DATE);
    }

    public void setDate(final int index, final LocalDate value) {
        Objects.requireNonNull(value, "value is null");
        setValue(index, FullTextSegmentCodecConstants.FORMATTER_DATE.format(value));
    }

    public FullTextSection date_(final int index, final LocalDate value) {
        setDate(index, value);
        return this;
    }

    public LocalTime getTime(final int index) {
        final var value = getValue(index);
        return LocalTime.parse(String.valueOf(value), FullTextSegmentCodecConstants.FORMATTER_TIME);
    }

    public void setTime(final int index, final LocalTime value) {
        Objects.requireNonNull(value, "value is null");
        setValue(index, FullTextSegmentCodecConstants.FORMATTER_TIME.format(value));
    }

    public FullTextSection time_(final int index, final LocalTime value) {
        setTime(index, value);
        return this;
    }

    // ---------------------------------------------------------------------------------------------------------- length

    /**
     * Returns the length of this section, in bytes.
     *
     * @return the length of this section, in bytes
     */
    public int getLength() {
        return length;
    }

    // ---------------------------------------------------------------------------------------------------------- buffer
    ByteBuffer getBuffer() {
        return buffer;
    }

    /**
     * Resets this section by setting data with zeros.
     *
     * @return this section.
     */
    public FullTextSection reset() {
        Arrays.fill(buffer.array(), (byte) 0x20);
        return this;
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
        if (Objects.requireNonNull(dst, "dst is null").remaining() < buffer.capacity()) {
            throw new IllegalArgumentException(
                    "dst.remaining(" + dst.remaining() + ") < data.capacity(" + buffer.capacity() + ")"
            );
        }
        return dst.put(buffer.clear());
    }

    byte[] getData(final byte[] dst) {
        if (Objects.requireNonNull(dst, "dst is null").length < buffer.capacity()) {
            throw new IllegalArgumentException(
                    "dst.length(" + dst.length + ") < data.capacity(" + buffer.capacity() + ")"
            );
        }
        return getData(ByteBuffer.wrap(dst)).array();
    }

    byte[] getData() {
        return getData(new byte[buffer.capacity()]);
    }

    void setData(final ByteBuffer src) {
        if (Objects.requireNonNull(src, "src is null").remaining() > buffer.capacity()) {
            throw new IllegalArgumentException(
                    "src.remaining(" + src.remaining() + ") > data.capacity(" + buffer.capacity() + ")"
            );
        }
        buffer.clear().put(src);
    }

    void setData(final byte[] src) {
        if (Objects.requireNonNull(src, "src is null").length > buffer.capacity()) {
            throw new IllegalArgumentException(
                    "src.length(" + src.length + ") > data.capacity(" + buffer.capacity() + ")"
            );
        }
        setData(ByteBuffer.wrap(src));
    }

    /**
     * Applies this section's data to specified function, and returns the result.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     */
    public <R> R applyData(final Function<? super ByteBuffer, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(buffer);
    }

    /**
     * Accepts this section's data to specified consumer.
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
