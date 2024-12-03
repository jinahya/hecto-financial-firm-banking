package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import javax.crypto.Cipher;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A class for {@code 전문(全文)} of {@code 실시간펌뱅킹} and {@code 실시간펌뱅킹(외화)}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://develop.sbsvc.online/27/onlineDocList.do">실시간펌뱅킹</a>
 * @see <a href="https://develop.sbsvc.online/31/onlineDocList.do">실시간펌뱅킹(외화)</a>
 */
public class FullText {

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    /**
     * Creates a new instance of specified category, {@code 전문구분코드}, and {@code 업무구분코드}.
     *
     * @param category the category.
     * @param textCode the {@code 전문구분코드}.
     * @param taskCode the {@code 업무구분코드}.
     * @return a new instance.
     */
    public static FullText newInstance(final FullTextCategory category, final String textCode, final String taskCode) {
        Objects.requireNonNull(category, "category is null");
        Objects.requireNonNull(textCode, "textCode is null");
        Objects.requireNonNull(taskCode, "taskCode is null");
        final var sections = List.of(
                FullTextSection.newHeadInstance(category),
                FullTextSection.newBodyInstance(category, textCode, taskCode)
        );
        final var instance = new FullText(category, sections);
        instance.setTextCode(textCode);
        instance.setTaskCode(taskCode);
        return instance;
    }

    public static FullText newInstance(final FullTextCategory category, final ByteBuffer data) {
        Objects.requireNonNull(category, "category is null");
        Objects.requireNonNull(data, "data is null");
        final var textCode = category.getHeadTextCode(data);
        final var taskCode = category.getHeadTaskCode(data);
        return newInstance(category, textCode, taskCode).setData(data);
    }

    /**
     * Reads an instance of specified category, from specified channel.
     *
     * @param category the category.
     * @param channel  the channel.
     * @return a new instance read from the {@code channel}.
     * @throws IOException if an I/O error occurs.
     * @see #readInstance(FullTextCategory, ReadableByteChannel, Cipher)
     */
    public static FullText readInstance(final FullTextCategory category, final ReadableByteChannel channel)
            throws IOException {
        Objects.requireNonNull(category, "category is null");
        final var data = FullTextUtils.readData(channel);
        return newInstance(category, data);
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
        final var encrypted = FullTextUtils.readData(channel);
        final var decrypted = ByteBuffer.allocate(cipher.getOutputSize(encrypted.flip().remaining()));
        try {
            final var bytes = cipher.doFinal(encrypted, decrypted);
            assert bytes <= decrypted.limit();
        } catch (final Exception e) {
            throw new RuntimeException("failed to decrypt with " + cipher, e);
        }
        return newInstance(category, decrypted.flip());
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
        length = this.sections.stream().mapToInt(FullTextSection::getLength).sum();
    }

    // JUST FOR THE MOCKING
    FullText() {
        super();
        category = null;
        sections = null;
        length = 0;
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
        return applySection(FullTextConstants.SECTION_INDEX_HEAD, category::getHeadDate);
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
        return applySection(FullTextConstants.SECTION_INDEX_HEAD, category::getHeadTime);
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

    /**
     * Returns a string representation of the internal data buffer.
     *
     * @return a string representation of the internal data buffer
     */
    public String getDataString() {
        return sections.stream()
                .map(FullTextSection::getDataString)
                .collect(Collectors.joining());
    }

    /**
     * Puts internal data buffer to specified buffer.
     *
     * @param dst the buffer.
     * @return given {@code dst}.
     */
    public ByteBuffer getData(final ByteBuffer dst) {
        Objects.requireNonNull(dst, "dst is null");
        sections.forEach(s -> s.getData(dst));
        return dst;
    }

    /**
     * Sets internal data buffer's data to specified array.
     *
     * @param dst the array.
     * @return given {@code dst}.
     */
    public byte[] getData(final byte[] dst) {
        Objects.requireNonNull(dst, "dst is null");
        return getData(ByteBuffer.wrap(dst)).array();
    }

    /**
     * Returns a copy of the internal data buffer's data.
     *
     * @return an array of internal data buffer's content.
     */
    public byte[] getData() {
        return getData(new byte[length]);
    }

    FullText setData(final ByteBuffer src) {
        if (Objects.requireNonNull(src, "src is null").remaining() != length) {
            throw new IllegalArgumentException("src.remaining(" + src.remaining() + ") != length(" + length + ")");
        }
        for (final var section : sections) {
            src.limit(src.position() + section.getLength());
            section.setData(src);
        }
        return this;
    }

    FullText setData(final byte[] src) {
        if (Objects.requireNonNull(src, "src is null").length != length) {
            throw new IllegalArgumentException("src.length(" + src.length + ") != length(" + length + ")");
        }
        return setData(ByteBuffer.wrap(src));
    }

    /**
     * Returns the value of {@code 전문구분코드} of this full text.
     *
     * @return the value of {@code 전문구분코드} of this full text.
     */
    public String getTextCode() {
        return applySection(FullTextConstants.SECTION_INDEX_HEAD, s -> {
            return category.getHeadTextCode(s.data);
        });
    }

    FullText setTextCode(final String textCode) {
        Objects.requireNonNull(textCode, "textCode is null");
        acceptSection(FullTextConstants.SECTION_INDEX_HEAD, s -> {
            category.setHeadTextCode(s.data, textCode);
        });
        return this;
    }

    /**
     * Returns the value of {@code 업무구분코드} of this full text.
     *
     * @return the value of {@code 업무구분코드} of this full text.
     */
    public String getTaskCode() {
        return applySection(FullTextConstants.SECTION_INDEX_HEAD, s -> {
            return category.getHeadTaskCode(s.data);
        });
    }

    FullText setTaskCode(final String taskCode) {
        Objects.requireNonNull(taskCode, "taskCode is null");
        acceptSection(FullTextConstants.SECTION_INDEX_HEAD, s -> {
            category.setHeadTaskCode(s.data, taskCode);
        });
        return this;
    }

    /**
     * Writes this full text to specified channel.
     *
     * @param channel the channel.
     * @throws IOException if an I/O error occurs.
     */
    public void write(final WritableByteChannel channel) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        final var data = ByteBuffer.wrap(getData());
        FullTextUtils.writeData(channel, data);
    }

    FullText read(final ReadableByteChannel channel) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        final var data = FullTextUtils.readData(channel);
        return setData(data.flip());
    }

    /**
     * Writes this full text to specified channel while encrypting bytes with specified cipher.
     *
     * @param channel the channel.
     * @param cipher  the cipher.
     * @throws IOException if an I/O error occurs.
     */
    public void write(final WritableByteChannel channel, final Cipher cipher) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        Objects.requireNonNull(cipher, "cipher is null");
        final var input = ByteBuffer.wrap(getData());
        assert input.position() == 0;
        assert input.remaining() == length;
        final var output = ByteBuffer.allocate(cipher.getOutputSize(input.remaining()));
        try {
            final var bytes = cipher.doFinal(input, output);
            assert bytes <= output.limit();
        } catch (final Exception e) {
            throw new RuntimeException("failed to encrypt with " + cipher, e);
        }
        FullTextUtils.writeData(channel, output.flip());
    }

    FullText read(final ReadableByteChannel channel, final Cipher cipher) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        Objects.requireNonNull(cipher, "cipher is null");
        final var input = FullTextUtils.readData(channel);
        final var output = ByteBuffer.allocate(cipher.getOutputSize(input.flip().remaining()));
        try {
            final var bytes = cipher.doFinal(input, output);
            assert bytes >= 0;
        } catch (final Exception e) {
            throw new RuntimeException("failed to decrypt", e);
        }
        return setData(output.flip());
    }

    // ---------------------------------------------------------------------------------------------------------- length

    // -----------------------------------------------------------------------------------------------------------------
    private final FullTextCategory category;

    final List<? extends FullTextSection> sections;

    private final int length;
}
