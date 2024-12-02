package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import javax.crypto.Cipher;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A class for {@code 전문(全文)} of {@code 실시간펌뱅킹} and {@code 실시간펌뱅킹(외화)}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://develop.sbsvc.online/27/onlineDocList.do">실시간펌뱅킹</a>
 * @see <a href="https://develop.sbsvc.online/31/onlineDocList.do">실시간펌뱅킹(외화)</a>
 */
public abstract class FullText {

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    /**
     * Creates a new instance of specified category, {@code 전문구분코드}, and {@code 업무구분코드}.
     *
     * @param category the category.
     * @param textCode the {@code 전문구분코드}.
     * @param taskCode the {@code 업무구분코드}.
     * @return a new instance.
     */
    public static FullText newInstance(final FullTextCategory category, final String textCode,
                                       final String taskCode) {
        final var sections = List.of(
                FullTextSection.newHeadInstance(category),
                FullTextSection.newInstance(category, textCode, taskCode)
        );
        final var text = new FullText(category, sections) {
        };
        text.setTextCode(textCode);
        text.setTaskCode(taskCode);
        text.sections.forEach(s -> s.setText(text));
        return text;
    }

    /**
     * Reads an instance of specified category, from specified channel.
     *
     * @param category the category.
     * @param channel  the channel.
     * @return a new instance read from {@code channel}.
     * @throws IOException if an I/O error occurs.
     * @see #readInstance(FullTextCategory, ReadableByteChannel, Cipher)
     */
    public static FullText readInstance(final FullTextCategory category, final ReadableByteChannel channel)
            throws IOException {
        Objects.requireNonNull(category, "category is null");
        final var buffer = FullTextUtils.readBuffer(category, channel);
        final var textCode = category.getTextCode(buffer);
        final var taskCode = category.getTaskCode(buffer);
        return newInstance(category, textCode, taskCode).setData(buffer.clear());
    }

    /**
     * Reads an instance of specified category, from specified channel, while decrypting with specified cipher.
     *
     * @param category the category.
     * @param channel  the channel.
     * @param cipher   the cipher.
     * @return a new instance read from {@code channel}.
     * @throws IOException if an I/O error occurs.
     * @see #readInstance(FullTextCategory, ReadableByteChannel)
     */
    public static FullText readInstance(final FullTextCategory category, final ReadableByteChannel channel,
                                        final Cipher cipher)
            throws IOException {
        Objects.requireNonNull(category, "category is null");
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        Objects.requireNonNull(cipher, "cipher is null");
        final var buffer = FullTextUtils.readBuffer(category, channel);
        final var output = ByteBuffer.allocate(category.textLength);
        try {
            cipher.doFinal(buffer.clear(), output);
        } catch (final Exception e) {
            throw new RuntimeException("failed to decrypt", e);
        }
        final var textCode = category.getTextCode(buffer);
        final var taskCode = category.getTaskCode(buffer);
        return newInstance(category, textCode, taskCode).setData(output.clear());
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private FullText(final FullTextCategory category, final List<? extends FullTextSection> sections) {
        super();
        Objects.requireNonNull(category, "category is null");
        if (Objects.requireNonNull(sections, "sections is null").isEmpty()) {
            throw new IllegalArgumentException("empty sections");
        }
        this.category = category;
        this.sections = List.copyOf(sections);
        final var last = this.sections.stream()
                .flatMap(s -> s.segments.stream())
                .reduce((s1, s2) -> s2.previous(s1))
                .orElseThrow();
        buffer = ByteBuffer.allocate(last.getOffset() + last.length);
        Arrays.fill(buffer.array(), (byte) 0x20);
    }

    // JUST FOR MOCKING
    FullText() {
        super();
        category = null;
        sections = null;
        buffer = null;
    }

    // -----------------------------------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------------------- sections

    /**
     * Applies the section of specified index to specified function, and return the result.
     * <p>
     * {@snippet lang = java:
     * var date = applySection(FullTextConstants.SECTION_INDEX_HEAD, s -> {
     *     return s.date_(8); // 8 전송일자
     * });
     *}
     *
     * @param index    the section index; starting at {@code 1}.
     * @param function the function to be applied with the section of {@code index}.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see FullTextConstants#SECTION_INDEX_BODY
     * @see #acceptSection(int, Consumer)
     */
    public <R> R applySection(final int index, final Function<? super FullTextSection, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        if (index <= 0) {
            throw new IllegalArgumentException("index(" + index + ") is not positive");
        }
        if (index > sections.size()) {
            throw new IllegalArgumentException(
                    "index(" + index + ") > sections.size(" + sections.size() + ")"
            );
        }
        return function.apply(sections.get(index - 1));
    }

    /**
     * Accepts the section of specified index to specified consumer.
     * <p>
     * {@snippet lang = java:
     * acceptSection(FullTextConstants.SECTION_INDEX_HEAD, s -> {
     *     s.value(3, "002");           // 3 은행코드
     *     s.date_(8, LocalDate.now()); // 8 전송일자
     *     s.time_(9, LocalTime.now()); // 9 전송시간
     * });
     *}
     *
     * @param index    the section index; starting at {@code 1}.
     * @param consumer the consumer to be accepted with the section of {@code index}.
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see FullTextConstants#SECTION_INDEX_BODY
     * @see #applySection(int, Function)
     */
    public void acceptSection(final int index, final Consumer<? super FullTextSection> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applySection(index, s -> {
            consumer.accept(s);
            return null;
        });
    }

    /**
     * Returns {@code 전송일자} segment's value of section {@value FullTextConstants#SECTION_INDEX_HEAD}.
     *
     * @return the value of {@code 전송일자} segment.
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see #setHeadDate(LocalDate)
     */
    public LocalDate getHeadDate() {
        return applySection(FullTextConstants.SECTION_INDEX_HEAD, s -> category.getHeadDate(s));
    }

    /**
     * Sets {@code 전송일자} segment's value, of section {@value FullTextConstants#SECTION_INDEX_HEAD}, with specified
     * value.
     *
     * @param headDate new value for the {@code 전송일자} segment.
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see #getHeadDate()
     */
    public void setHeadDate(final LocalDate headDate) {
        Objects.requireNonNull(headDate, "headDate is null");
        acceptSection(FullTextConstants.SECTION_INDEX_HEAD, s -> category.setHeadDate(s, headDate));
    }

    public LocalTime getHeadTime() {
        return applySection(FullTextConstants.SECTION_INDEX_HEAD, s -> category.getHeadTime(s));
    }

    public void setHeadTime(final LocalTime headTime) {
        Objects.requireNonNull(headTime, "headTime is null");
        acceptSection(FullTextConstants.SECTION_INDEX_HEAD, s -> category.setHeadTime(s, headTime));
    }

    public LocalDateTime getHeadDateTime() {
        return LocalDateTime.of(getHeadDate(), getHeadTime());
    }

    public void setHeadDateTime(final LocalDateTime headDateTime) {
        Objects.requireNonNull(headDateTime, "headDateTime is null");
        setHeadDate(LocalDate.from(headDateTime));
        setHeadTime(LocalTime.from(headDateTime));
    }

    public void setHeadDateTimeAsNow() {
        setHeadDateTime(LocalDateTime.now());
    }

    // ---------------------------------------------------------------------------------------------------------- buffer
    public String getDataString() {
        return FullTextSegmentCodecX.CHARSET.decode(buffer.clear()).toString();
    }

    @SuppressWarnings({"unchecked"})
    public <T extends ByteBuffer> T getData(final T dst) {
        return (T) Objects.requireNonNull(dst, "dst is null").put(buffer.clear());
    }

    public byte[] getData(final byte[] dst) {
        return ByteBuffer.wrap(Objects.requireNonNull(dst, "dst is null")).array();
    }

    FullText setData(final ByteBuffer src) {
        Objects.requireNonNull(src, "src is null");
        buffer.clear().put(src);
        return this;
    }

    FullText setData(final byte[] src) {
        return setData(ByteBuffer.wrap(Objects.requireNonNull(src, "src is null")));
    }

    /**
     * Returns the value of {@code 전문구분코드} of this full text.
     *
     * @return the value of {@code 전문구분코드} of this full text.
     */
    public String getTextCode() {
        return category.getTextCode(buffer);
    }

    FullText setTextCode(final String textCode) {
        Objects.requireNonNull(textCode, "textCode is null");
        category.setTextCode(buffer, textCode);
        return this;
    }

    /**
     * Returns the value of {@code 업무구분코드} of this full text.
     *
     * @return the value of {@code 업무구분코드} of this full text.
     */
    public String getTaskCode() {
        return category.getTaskCode(buffer);
    }

    FullText setTaskCode(final String taskCode) {
        Objects.requireNonNull(taskCode, "taskCode is null");
        category.setTaskCode(buffer, taskCode);
        return this;
    }

    /**
     * Writes this full text to specified channel.
     *
     * @param channel the channel.
     * @throws IOException if an I/O error occurs.
     */
    public FullText write(final WritableByteChannel channel) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        FullTextUtils.writeBuffer(category, channel, buffer.clear());
        return this;
    }

    public FullText read(final ReadableByteChannel channel) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        return setData(FullTextUtils.readBuffer(category, channel).flip());
    }

    /**
     * Writes this full text to specified channel while encrypting with specified cipher.
     *
     * @param channel the channel.
     * @param cipher  the cipher.
     * @return this full text.
     * @throws IOException if an I/O error occurs.
     */
    public FullText write(final WritableByteChannel channel, final Cipher cipher) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        Objects.requireNonNull(cipher, "cipher is null");
        final var output = ByteBuffer.allocate(cipher.getOutputSize(buffer.capacity()));
        try {
            final var bytes = cipher.doFinal(buffer.clear(), output);
            assert bytes <= output.limit();
        } catch (final Exception e) {
            throw new RuntimeException("failed to encrypt", e);
        }
        FullTextUtils.writeBuffer(category, channel, output.flip());
        return this;
    }

    public FullText read(final ReadableByteChannel channel, final Cipher cipher) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        Objects.requireNonNull(cipher, "cipher is null");
        final var input = FullTextUtils.readBuffer(category, channel);
        try {
            final var bytes = cipher.doFinal(input.flip(), buffer.clear());
            assert bytes >= 0;
        } catch (final Exception e) {
            throw new RuntimeException("failed to decrypt", e);
        }
        return this;
    }

    // -----------------------------------------------------------------------------------------------------------------
    final FullTextCategory category;

    final List<? extends FullTextSection> sections;

    final ByteBuffer buffer;
}
