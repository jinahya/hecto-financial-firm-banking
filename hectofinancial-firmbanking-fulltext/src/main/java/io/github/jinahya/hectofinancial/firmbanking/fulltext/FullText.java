package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
 * @see #readInstance(FullTextCategory, ReadableByteChannel, FullTextCrypto)
 * @see #readInstance(FullTextCategory, InputStream, FullTextCrypto)
 * @see FullTextSection
 * @see FullTextCrypto
 */
@SuppressWarnings({
        "java:S1192" // String literals should not be duplicated
})
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
        final var instance = new FullText(category, sections);
        instance.setTextCode(textCode);
        instance.setTaskCode(taskCode);
        return instance;
    }

    /**
     * Reads an instance from specified channel.
     *
     * @param category a category of the {@code 전문}.
     * @param channel  the channel.
     * @param crypto   a crypto; may be {@code null}.
     * @return a new instance.
     * @throws IOException if an I/O error occurs.
     * @see #readInstance(FullTextCategory, InputStream, FullTextCrypto)
     */
    public static FullText readInstance(final FullTextCategory category, final ReadableByteChannel channel,
                                        final FullTextCrypto crypto)
            throws IOException {
        Objects.requireNonNull(category, "category is null");
        Objects.requireNonNull(channel, "channel is null");
        var data = FullTextUtils.readData(channel);
        if (crypto != null) {
            data = crypto.decrypt(data.flip());
        }
        final var textCode = category.getHeadTextCode(data);
        final var taskCode = category.getHeadTaskCode(data);
        final var instance = newInstance(category, textCode, taskCode);
        instance.setCrypto(crypto);
        instance.setRawData(data.flip());
        return instance;
    }

    /**
     * Reads an instance from specified stream.
     *
     * @param category a category of the {@code 전문}.
     * @param stream   the stream.
     * @param crypto   a crypto; may be {@code null}.
     * @return a new instance.
     * @throws IOException if an I/O error occurs.
     * @see #readInstance(FullTextCategory, ReadableByteChannel, FullTextCrypto)
     */
    public static FullText readInstance(final FullTextCategory category, final InputStream stream,
                                        final FullTextCrypto crypto)
            throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        return readInstance(category, Channels.newChannel(stream), crypto);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance with specified category and sections.
     *
     * @param category the category.
     * @param sections the sections.
     * @see #category
     * @see #sections
     */
    private FullText(final FullTextCategory category, final List<? extends FullTextSection> sections) {
        super();
        this.category = Objects.requireNonNull(category, "category is null");
        if (Objects.requireNonNull(sections, "sections is null").isEmpty()) {
            throw new IllegalArgumentException("empty sections");
        }
        this.sections = List.copyOf(Objects.requireNonNull(sections, "sections is null"));
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

    /**
     * Returns a string representation of this text.
     *
     * @return a string representation of this text.
     */
    @Override
    public String toString() {
        return super.toString() + '{' +
                "category=" + category +
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

    /**
     * Returns the {@code 전문구분코드} of this text.
     *
     * @return the {@code 전문구분코드} of this text.
     */
    public String getTextCode() {
        return applyHeadSection(s -> category.getHeadTextCode(s.getBuffer()));
    }

    void setTextCode(final String textCode) {
        acceptHeadSection(s -> category.setHeadTextCode(s.getBuffer(), textCode));
    }

    /**
     * Returns the {@code 업무구분코드} of this text.
     *
     * @return the {@code 업무구분코드} of this text.
     */
    public String getTaskCode() {
        return applyHeadSection(s -> category.getHeadTaskCode(s.getBuffer()));
    }

    void setTaskCode(final String taskCode) {
        acceptHeadSection(s -> category.setHeadTaskCode(s.getBuffer(), taskCode));
    }

    // -------------------------------------------------------------------------------------------------------- sections
    List<FullTextSection> getSections() {
        return sections;
    }

    /**
     * Applies this text's section of specified index to specified function, and return the result.
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
     * @see #applyHeadSection(Function)
     * @see #applyBodySection(Function)
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
     * Accepts this text's section of specified index to specified consumer.
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
     * @see #acceptHeadSection(Consumer)
     * @see #acceptBodySection(Consumer)
     */
    public void acceptSection(final int index, final Consumer<? super FullTextSection> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applySection(index, s -> {
            consumer.accept(s);
            return null;
        });
    }

    /**
     * Applies this text's {@link FullTextConstants#SECTION_INDEX_HEAD head} section to specified function, and returns
     * the result.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see #applySection(int, Function)
     * @see #acceptHeadSection(Consumer)
     */
    public <R> R applyHeadSection(final Function<? super FullTextSection, ? extends R> function) {
        return applySection(FullTextConstants.SECTION_INDEX_HEAD, function);
    }

    /**
     * Accepts this text's {@link FullTextConstants#SECTION_INDEX_HEAD head} section to specified consumer.
     *
     * @param consumer the consumer.
     * @see #applyHeadSection(Function)
     */
    public void acceptHeadSection(final Consumer<? super FullTextSection> consumer) {
        acceptSection(FullTextConstants.SECTION_INDEX_HEAD, consumer);
    }

    /**
     * Applies this text's {@link FullTextConstants#SECTION_INDEX_BODY body} section to specified function, and returns
     * the result.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see #applySection(int, Function)
     * @see #acceptBodySection(Consumer)
     */
    public <R> R applyBodySection(final Function<? super FullTextSection, ? extends R> function) {
        return applySection(FullTextConstants.SECTION_INDEX_BODY, function);
    }

    /**
     * Accepts this text's {@link FullTextConstants#SECTION_INDEX_BODY body} section to specified consumer.
     *
     * @param consumer the consumer.
     * @see #applyBodySection(Function)
     */
    public void acceptBodySection(final Consumer<? super FullTextSection> consumer) {
        acceptSection(FullTextConstants.SECTION_INDEX_BODY, consumer);
    }

    /**
     * Returns {@code 전송일자} from the {@link FullTextConstants#SECTION_INDEX_HEAD head} section of this text.
     *
     * @return the value of {@code 전송일자} segment.
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see #setHeadDate(LocalDate)
     */
    public LocalDate getHeadDate() {
        return applyHeadSection(category::getHeadDate);
    }

    /**
     * Sets {@code 전송일자} to the {@link FullTextConstants#SECTION_INDEX_HEAD head} section of this text, with specified
     * value.
     *
     * @param headDate new value for the {@code 전송일자} segment.
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see #getHeadDate()
     */
    public void setHeadDate(final LocalDate headDate) {
        acceptHeadSection(s -> category.setHeadDate(s, headDate));
    }

    /**
     * Returns {@code 전송시간} from the {@link FullTextConstants#SECTION_INDEX_HEAD head} section of this text.
     *
     * @return the value of {@code 전송시간} segment.
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see #setHeadDate(LocalDate)
     */
    public LocalTime getHeadTime() {
        return applyHeadSection(category::getHeadTime);
    }

    /**
     * Sets {@code 전송시간} from the {@link FullTextConstants#SECTION_INDEX_HEAD head} section of this text, with specified
     * value.
     *
     * @param headTime new value for the {@code 전송시간} segment.
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see #getHeadDate()
     */
    public void setHeadTime(final LocalTime headTime) {
        acceptHeadSection(s -> category.setHeadTime(s, headTime));
    }

    /**
     * Returns {@code 전송일시} from the {@link FullTextConstants#SECTION_INDEX_HEAD head} section of this text.
     *
     * @return the value of {@code 전송일시}; {@code null} when either {@link #getHeadDate() 전송일자} or
     * {@link #getHeadTime() 전송시간} is {@code null}.
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see #getHeadDate()
     * @see #getHeadTime()
     * @see #setHeadDateTime(LocalDateTime)
     */
    public LocalDateTime getHeadDateTime() {
        return Optional.ofNullable(getHeadDate())
                .flatMap(d -> Optional.ofNullable(getHeadTime()).map(t -> LocalDateTime.of(d, t)))
                .orElse(null);
    }

    /**
     * Sets {@code 전송일시} to the {@link FullTextConstants#SECTION_INDEX_HEAD head} section of this text, with specified
     * value.
     *
     * @param headDateTime new value for the {@code 전송일자/전송시간} segment.
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see #setHeadDate(LocalDate)
     * @see #setHeadTime(LocalTime)
     * @see #getHeadDateTime()
     */
    public void setHeadDateTime(final LocalDateTime headDateTime) {
        setHeadDate(Optional.ofNullable(headDateTime).map(LocalDate::from).orElse(null));
        setHeadTime(Optional.ofNullable(headDateTime).map(LocalTime::from).orElse(null));
    }

    /**
     * Sets {@code 전송일시} to the {@link FullTextConstants#SECTION_INDEX_HEAD head} section of this text, with
     * {@link LocalDateTime#now() now}.
     *
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see #setHeadDateTime(LocalDateTime)
     */
    public void setHeadDateTimeWithNow() {
        setHeadDateTime(LocalDateTime.now());
    }

    /**
     * Returns a string representation of the {@link FullTextConstants#SECTION_INDEX_HEAD head} section of this text.
     *
     * @return a string representation of the {@link FullTextConstants#SECTION_INDEX_HEAD head} section of this text.
     */
    public String getHeadDataString() {
        return applyHeadSection(FullTextSection::getDataString);
    }

    /**
     * Returns a string representation of the {@link FullTextConstants#SECTION_INDEX_BODY body} section of this text.
     *
     * @return a string representation of the {@link FullTextConstants#SECTION_INDEX_BODY body} section of this text.
     */
    public String getBodyDataString() {
        return applyBodySection(FullTextSection::getDataString);
    }

    /**
     * Returns a string representation of this text's data.
     *
     * @return a string representation of this text's data.
     */
    public String getDataString() {
        return sections.stream()
                .map(FullTextSection::getDataString)
                .collect(Collectors.joining());
    }

    /**
     * Returns a byte buffer of this text's raw(unencrypted) data.
     *
     * @return a byte buffer of this text's raw(unencrypted) data.
     * @see #setRawData(ByteBuffer)
     */
    private ByteBuffer getRawData() {
        final var dst = ByteBuffer.allocate(length);
        sections.forEach(s -> s.getData(dst));
        return dst;
    }

    /**
     * Sets specified buffer of raw(unencrypted) data to this text.
     *
     * @param src the buffer of war(unencrypted) data, whose {@link ByteBuffer#remaining() remaining} should be equal to
     *            {@link #getLength() length} of this text.
     * @see #getRawData()
     */
    void setRawData(final ByteBuffer src) {
        if (Objects.requireNonNull(src, "src is null").remaining() != length) {
            throw new IllegalArgumentException("src.remaining(" + src.remaining() + ") != length(" + length + ")");
        }
        for (final var section : sections) {
            src.limit(src.position() + section.getLength());
            section.setData(src);
        }
    }

    /**
     * Returns a byte buffer of this text's data, while encrypting when {@link #setCrypto(FullTextCrypto) crypto} is
     * set.
     *
     * @return a byte buffer of data copied.
     * @see #setData(ByteBuffer)
     */
    public ByteBuffer getData() {
        final var data = getRawData();
        if (crypto != null) {
            return crypto.encrypt(data.flip());
        }
        return data;
    }

    /**
     * Sets data with specified buffer's remaining bytes, while decrypting when
     * {@link #setCrypto(FullTextCrypto) crypto} set.
     *
     * @param src the source buffer.
     * @see #getData()
     */
    void setData(final ByteBuffer src) {
        if (Objects.requireNonNull(src, "src is null").remaining() < length) {
            throw new IllegalArgumentException("src.remaining(" + src.remaining() + ") < length(" + length + ")");
        }
        if (crypto != null) {
            final var decrypted = crypto.decrypt(src);
            setRawData(decrypted.flip());
            return;
        }
        setRawData(src);
    }

    /**
     * Writes this text's data to specified channel.
     *
     * @param channel the channel.
     * @param <T>     channel type parameter
     * @return given {@code channel}.
     * @throws IOException if an I/O error occurs.
     * @see #write(OutputStream)
     */
    public <T extends WritableByteChannel> T write(final T channel) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        final var data = getData();
        FullTextUtils.writeData(channel, data.flip());
        return channel;
    }

    /**
     * Writes this text's data to specified stream.
     *
     * @param stream the stream.
     * @param <T>    stream type parameter
     * @return given {@code stream}.
     * @throws IOException if an I/O error occurs.
     * @see #write(WritableByteChannel)
     */
    public <T extends OutputStream> T write(final T stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        write(Channels.newChannel(stream));
        return stream;
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

    // ---------------------------------------------------------------------------------------------------------- crypto

    /**
     * Sets specified crypto for this text.
     *
     * @param crypto crypto for this text; {@code null} to clear.
     */
    public void setCrypto(final FullTextCrypto crypto) {
        this.crypto = crypto;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final FullTextCategory category;

    private final List<FullTextSection> sections;

    private final int length;

    private transient FullTextCrypto crypto;
}
