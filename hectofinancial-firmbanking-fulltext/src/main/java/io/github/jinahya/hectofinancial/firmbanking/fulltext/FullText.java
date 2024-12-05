package io.github.jinahya.hectofinancial.firmbanking.fulltext;

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
 * @see #newInstance(FullTextCategory, String, String)
 * @see #readInstance(FullTextCategory, ReadableByteChannel, FullTextSecurity)
 */
public class FullText {

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    /**
     * Creates a new instance of specified category, {@code 전문구분코드}, and {@code 업무구분코드}.
     *
     * @param category the category.
     * @param textCode the {@code 전문구분코드}.
     * @param taskCode the {@code 업무구분코드}.
     * @return a new instance of {@code category} with {@code textCode} and {@code taskCode}.
     */
    public static FullText newInstance(final FullTextCategory category, final String textCode, final String taskCode) {
        Objects.requireNonNull(category, "category is null");
        Objects.requireNonNull(textCode, "textCode is null");
        Objects.requireNonNull(taskCode, "taskCode is null");
        final var sections = List.of(
                FullTextSection.newHeadInstance(category),
                FullTextSection.newBodyInstance(category, textCode, taskCode)
        );
        final var instance = new FullText(category, textCode, taskCode, sections);
        instance.setTextCode(textCode);
        instance.setTaskCode(taskCode);
        return instance;
    }

    /**
     * Reads an instance from specified channel.
     *
     * @param category a category of the {@code 전문}.
     * @param channel  the channel.
     * @param security a security; may be {@code null}.
     * @return a new instance.
     * @throws IOException if an I/O error occurs.
     */
    public static FullText readInstance(final FullTextCategory category, final ReadableByteChannel channel,
                                        final FullTextSecurity security)
            throws IOException {
        Objects.requireNonNull(category, "category is null");
        Objects.requireNonNull(channel, "channel is null");
        var data = FullTextUtils.receiveData(channel);
        if (security != null) {
            data = security.decrypt(data.flip());
        }
        final var textCode = category.getHeadTextCode(data);
        final var taskCode = category.getHeadTaskCode(data);
        final var instance = newInstance(category, textCode, taskCode);
        instance.setData(data.flip()); // set data without the security
        instance.setSecurity(security); // should be set after the data.
        return instance;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private FullText(final FullTextCategory category, final String textCode, final String taskCode,
                     final List<? extends FullTextSection> sections) {
        super();
        this.category = Objects.requireNonNull(category, "category is null");
        this.textCode = Objects.requireNonNull(textCode, "textCode is null");
        this.taskCode = Objects.requireNonNull(taskCode, "taskCode is null");
        if (Objects.requireNonNull(sections, "sections is null").isEmpty()) {
            throw new IllegalArgumentException("empty sections");
        }
        this.sections = List.copyOf(Objects.requireNonNull(sections, "sections is null"));
        length = this.sections.stream().mapToInt(FullTextSection::getLength).sum(); // NullPointerException
    }

    // JUST FOR THE MOCKING
    FullText() {
        super();
        category = null;
        textCode = null;
        taskCode = null;
        sections = null;
        length = 0;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns a string representation of this text.
     *
     * @return a string representation of this text.
     */
    @Override
    public String toString() {
        return super.toString() + '{' +
                "category=" + category +
                ",textCode=" + textCode +
                ",taskCode=" + taskCode +
                ",sections=" + sections +
                ",length=" + length +
                '}';
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final var that = (FullText) obj;
        return length == that.length &&
                category == that.category &&
                Objects.equals(sections, that.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, sections, length);
    }

    // -------------------------------------------------------------------------------------------------------- category

    /**
     * Returns the category of this text.
     *
     * @return the category of this text.
     */
    public FullTextCategory getCategory() {
        return category;
    }

    // -------------------------------------------------------------------------------------------------------- textCode

    /**
     * Returns the {@code 전문구분코드} of this text.
     *
     * @return the {@code 전문구분코드} of this text.
     */
    public String getTextCode() {
        return applyHeadSection(s -> category.getHeadTextCode(s.getBuffer()));
//        return textCode;
    }

    void setTextCode(final String textCode) {
        acceptHeadSection(s -> category.setHeadTextCode(s.getBuffer(), textCode));
    }

    // -------------------------------------------------------------------------------------------------------- taskCode

    /**
     * Returns the {@code 업무구분코드} of this text.
     *
     * @return the {@code 업무구분코드} of this text.
     */
    public String getTaskCode() {
        return applyHeadSection(s -> category.getHeadTaskCode(s.getBuffer()));
//        return taskCode;
    }

    void setTaskCode(final String taskCode) {
        acceptHeadSection(s -> category.setHeadTaskCode(s.getBuffer(), taskCode));
    }

    // -------------------------------------------------------------------------------------------------------- sections

    /**
     * Applies the section of specified index to specified function, and return the result.
     * <p>
     * {@snippet lang = java:
     * var date = applySection(FullTextConstants.SECTION_INDEX_HEAD, s -> {
     *     return s.getDate(8); // 8 전송일자
     * });
     *}
     *
     * @param index    the index of the section to apply; starting at {@code 1}.
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
     * @param index    the index of the section to accept; starting at {@code 1}.
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

    public <R> R applyHeadSection(final Function<? super FullTextSection, ? extends R> function) {
        return applySection(FullTextConstants.SECTION_INDEX_HEAD, function);
    }

    public void acceptHeadSection(final Consumer<? super FullTextSection> consumer) {
        acceptSection(FullTextConstants.SECTION_INDEX_HEAD, consumer);
    }

    public <R> R applyBodySection(final Function<? super FullTextSection, ? extends R> function) {
        return applySection(FullTextConstants.SECTION_INDEX_BODY, function);
    }

    public void acceptBodySection(final Consumer<? super FullTextSection> consumer) {
        acceptSection(FullTextConstants.SECTION_INDEX_BODY, consumer);
    }

    /**
     * Returns {@code 전송일자} from the {@link FullTextConstants#SECTION_INDEX_HEAD head} section.
     *
     * @return the value of {@code 전송일자} segment.
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see #setHeadDate(LocalDate)
     */
    public LocalDate getHeadDate() {
        return applySection(FullTextConstants.SECTION_INDEX_HEAD, category::getHeadDate);
    }

    /**
     * Sets {@code 전송일자} to the {@link FullTextConstants#SECTION_INDEX_HEAD head} section, with specified value.
     *
     * @param headDate new value for the {@code 전송일자} segment.
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see #getHeadDate()
     */
    public void setHeadDate(final LocalDate headDate) {
        Objects.requireNonNull(headDate, "headDate is null");
        acceptSection(FullTextConstants.SECTION_INDEX_HEAD, s -> category.setHeadDate(s, headDate));
    }

    /**
     * Returns {@code 전송시간} from the {@link FullTextConstants#SECTION_INDEX_HEAD head} section.
     *
     * @return the value of {@code 전송시간} segment.
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see #setHeadDate(LocalDate)
     */
    public LocalTime getHeadTime() {
        return applySection(FullTextConstants.SECTION_INDEX_HEAD, category::getHeadTime);
    }

    /**
     * Sets {@code 전송시간} from the {@link FullTextConstants#SECTION_INDEX_HEAD head} section, with specified value.
     *
     * @param headTime new value for the {@code 전송시간} segment.
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see #getHeadDate()
     */
    public void setHeadTime(final LocalTime headTime) {
        Objects.requireNonNull(headTime, "headTime is null");
        acceptSection(FullTextConstants.SECTION_INDEX_HEAD, s -> category.setHeadTime(s, headTime));
    }

    /**
     * Returns {@code 전송일자/전송시간} from the {@link FullTextConstants#SECTION_INDEX_HEAD head} section.
     *
     * @return the value of {@code 전송일자/전송시간} segment.
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see #setHeadDate(LocalDate)
     */
    public LocalDateTime getHeadDateTime() {
        return LocalDateTime.of(getHeadDate(), getHeadTime());
    }

    /**
     * Sets {@code 전송일자/전송시간} to the {@link FullTextConstants#SECTION_INDEX_HEAD head} section, with specified value.
     *
     * @param headDateTime new value for the {@code 전송일자/전송시간} segment.
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see #getHeadDate()
     * @see #setHeadDateTimeAsNow()
     */
    public void setHeadDateTime(final LocalDateTime headDateTime) {
        Objects.requireNonNull(headDateTime, "headDateTime is null");
        setHeadDate(LocalDate.from(headDateTime));
        setHeadTime(LocalTime.from(headDateTime));
    }

    /**
     * Sets {@code 전송일자/전송시간} to the {@link FullTextConstants#SECTION_INDEX_HEAD head} section, with
     * {@link LocalDateTime#now() now}.
     *
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see #setHeadDateTime(LocalDateTime)
     */
    public void setHeadDateTimeAsNow() {
        setHeadDateTime(LocalDateTime.now());
    }

    /**
     * Returns a string representation of the {@link FullTextConstants#SECTION_INDEX_HEAD head} section.
     *
     * @return a string representation of the {@link FullTextConstants#SECTION_INDEX_HEAD head} section.
     */
    public String getHeadString() {
        return applyHeadSection(FullTextSection::getDataString);
    }

    /**
     * Returns a string representation of the {@link FullTextConstants#SECTION_INDEX_BODY body} section.
     *
     * @return a string representation of the {@link FullTextConstants#SECTION_INDEX_BODY body} section.
     */
    public String getBodyString() {
        return applyBodySection(FullTextSection::getDataString);
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
     * Returns a copy of the internal raw data.
     *
     * @return a copy of the internal raw data.
     * @see #setRawData(ByteBuffer)
     */
    public ByteBuffer getRawData() {
        final var dst = ByteBuffer.allocate(length);
        sections.forEach(s -> s.getData(dst));
        return dst;
    }

    /**
     * Replaces in internal data buffer's content with specified buffer's content.
     *
     * @param src the buffer whose {@link ByteBuffer#remaining() remaining} should be equal to {@link #getLength()}.
     * @see #getRawData()
     */
    public void setRawData(final ByteBuffer src) {
        if (Objects.requireNonNull(src, "src is null").remaining() != length) {
            throw new IllegalArgumentException("src.remaining(" + src.remaining() + ") != length(" + length + ")");
        }
        for (final var section : sections) {
            src.limit(src.position() + section.getLength());
            section.setData(src);
        }
    }

    /**
     * Returns a byte buffer of data copied, while encrypting when {@link #setSecurity(FullTextSecurity) security} is
     * set.
     *
     * @return a byte buffer of data copied.
     * @see #setData(ByteBuffer)
     */
    public ByteBuffer getData() {
        final var data = getRawData();
        if (security != null) {
            return security.encrypt(data.flip());
        }
        return data;
    }

    /**
     * Sets data with specified buffer's remaining bytes, while decrypting when
     * {@link #setSecurity(FullTextSecurity) security} set.
     *
     * @param src the source buffer.
     * @see #getData()
     */
    void setData(final ByteBuffer src) {
        if (Objects.requireNonNull(src, "src is null").remaining() < length) {
            throw new IllegalArgumentException("src.remaining(" + src.remaining() + ") < length(" + length + ")");
        }
        if (security != null) {
            final var decrypted = security.decrypt(src);
            setRawData(decrypted.flip());
            return;
        }
        setRawData(src);
    }

    /**
     * Writes this text to specified channel.
     *
     * @param channel the channel.
     * @throws IOException if an I/O error occurs.
     */
    public void write(final WritableByteChannel channel) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        final var data = getData();
        FullTextUtils.sendData(channel, data.flip());
    }

    // ---------------------------------------------------------------------------------------------------------- length

    /**
     * Returns the length of this text.
     *
     * @return the length of this text.
     */
    public int getLength() {
        return length;
    }

    // -------------------------------------------------------------------------------------------------------- security

    /**
     * Sets specified security for this text.
     *
     * @param security security for this text; {@code null} to clear.
     */
    public void setSecurity(final FullTextSecurity security) {
        this.security = security;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final FullTextCategory category;

    private final String textCode;

    private final String taskCode;

    final List<? extends FullTextSection> sections;

    private final int length;

    private transient FullTextSecurity security;
}
