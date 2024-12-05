package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import javax.crypto.Cipher;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
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

    /**
     * Returns {@code 전송시간} segment's value of section {@value FullTextConstants#SECTION_INDEX_HEAD}.
     *
     * @return the value of {@code 전송시간} segment.
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see #setHeadDate(LocalDate)
     */
    public LocalTime getHeadTime() {
        return applySection(FullTextConstants.SECTION_INDEX_HEAD, category::getHeadTime);
    }

    /**
     * Sets {@code 전송시간} segment's value, of section {@value FullTextConstants#SECTION_INDEX_HEAD}, with specified
     * value.
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
     * Returns {@code 전송일자/전송시간} segment's value of section {@value FullTextConstants#SECTION_INDEX_HEAD}.
     *
     * @return the value of {@code 전송일자/전송시간} segment.
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see #setHeadDate(LocalDate)
     */
    public LocalDateTime getHeadDateTime() {
        return LocalDateTime.of(getHeadDate(), getHeadTime());
    }

    /**
     * Sets {@code 전송일자/전송시간} segment's value, of section {@value FullTextConstants#SECTION_INDEX_HEAD}, with specified
     * value.
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
     * Sets {@code 전송일자/전송시간} segment's value, of section {@value FullTextConstants#SECTION_INDEX_HEAD}, with
     * {@link LocalDateTime#now()}.
     *
     * @see FullTextConstants#SECTION_INDEX_HEAD
     * @see #setHeadDateTime(LocalDateTime)
     */
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
     * @see #rawData(ByteBuffer)
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
     * Replaces in internal data buffer's content with specified buffer's content, and returns this text.
     *
     * @param src the buffer whose {@link ByteBuffer#remaining() remaining} should be equal to {@link #getLength()}.
     * @see #setRawData(ByteBuffer)
     */
    public FullText rawData(final ByteBuffer src) {
        setRawData(src);
        return this;
    }

    private boolean initCipher(final int opmode) {
        if (cipher != null && key != null) {
            if (params != null) {
                try {
                    cipher.init(opmode, key, params);
                } catch (final Exception e) {
                    throw new RuntimeException("failed to initialize the cipher", e);
                }
            } else {
                try {
                    cipher.init(opmode, key);
                } catch (final Exception e) {
                    throw new RuntimeException("failed to initialize the cipher", e);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Returns a byte buffer of data copied, while encrypting when {@code cipher}, {@code key}, and(or) {@code params}
     * are set.
     *
     * @return a byte buffer of data copied.
     * @see #setData(ByteBuffer)
     */
    public ByteBuffer getData() {
        final var data = getRawData();
        if (initCipher(Cipher.ENCRYPT_MODE)) {
            final var encrypted = ByteBuffer.allocate(cipher.getOutputSize(data.flip().remaining()));
            try {
                final var bytes = cipher.doFinal(data, encrypted);
                assert bytes >= encrypted.capacity();
            } catch (final Exception e) {
                throw new RuntimeException("failed to encrypt", e);
            }
            return encrypted;
        }
        return data;
    }

    /**
     * Sets data with specified source buffer, while decrypting when {@code cipher}, {@code key}, and(or) {@code params}
     * are set.
     *
     * @param src the source buffer.
     * @see #getData()
     * @see #data(ByteBuffer)
     */
    public void setData(final ByteBuffer src) {
        if (Objects.requireNonNull(src, "src is null").remaining() < length) {
            throw new IllegalArgumentException("src.remaining(" + src.remaining() + ") < length(" + length + ")");
        }
        if (initCipher(Cipher.DECRYPT_MODE)) {
            final var decrypted = ByteBuffer.allocate(cipher.getOutputSize(src.remaining()));
            try {
                cipher.doFinal(src, decrypted);
                setRawData(decrypted.flip());
                return;
            } catch (final Exception e) {
                throw new RuntimeException("failed to decrypt", e);
            }
        }
        setRawData(src);
    }

    /**
     * Sets data with specified source buffer, and returns this text.
     *
     * @param src the source buffer.
     * @return this text.
     * @see #setData(ByteBuffer)
     */
    public FullText data(final ByteBuffer src) {
        setData(src);
        return this;
    }

    /**
     * Returns the value of {@code 전문구분코드} of this full text.
     *
     * @return the value of {@code 전문구분코드}  this full text.
     */
    public String getTextCode() {
        return applySection(FullTextConstants.SECTION_INDEX_HEAD, s -> {
            return category.getHeadTextCode(s.getBuffer());
        });
    }

    /**
     * Sets the value of {@code 전문구분코드} of this full text with specified value.
     *
     * @param textCode new value for the {@code 전문구분코드}.
     */
    FullText setTextCode(final String textCode) {
        Objects.requireNonNull(textCode, "textCode is null");
        acceptSection(FullTextConstants.SECTION_INDEX_HEAD, s -> {
            category.setHeadTextCode(s.getBuffer(), textCode);
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
            return category.getHeadTaskCode(s.getBuffer());
        });
    }

    /**
     * Sets the value of {@code 업무구분코드} of this full text with specified value.
     *
     * @param taskCode new value for the {@code 업무구분코드}.
     */
    void setTaskCode(final String taskCode) {
        Objects.requireNonNull(taskCode, "taskCode is null");
        acceptSection(FullTextConstants.SECTION_INDEX_HEAD, s -> {
            category.setHeadTaskCode(s.getBuffer(), taskCode);
        });
    }

    /**
     * Writes this text's data to specified channel.
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

    /**
     * Reads this text's data from specified channel.
     *
     * @param channel the channel.
     * @throws IOException if an I/O error occurs.
     */
    public void read(final ReadableByteChannel channel) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        final var data = FullTextUtils.receiveData(channel);
        setData(data.flip());
    }

    // ---------------------------------------------------------------------------------------------------------- length

    /**
     * Returns the length of this text, in bytes.
     *
     * @return the length of this text, in bytes.
     */
    public int getLength() {
        return length;
    }

    // ---------------------------------------------------------------------------------------------------------- cipher

    /**
     * Sets specified cipher to this text.
     *
     * @param cipher the cipher.
     */
    public void setCipher(final Cipher cipher) {
        this.cipher = cipher;
    }

    /**
     * Sets specified cipher to this text, and returns this text.
     *
     * @param cipher the cipher.
     * @return this text.
     */
    public FullText cipher(final Cipher cipher) {
        setCipher(cipher);
        return this;
    }

    // ------------------------------------------------------------------------------------------------------------- key
    public void setKey(final Key key) {
        this.key = key;
    }

    public FullText key(final Key key) {
        setKey(key);
        return null;
    }

    // ---------------------------------------------------------------------------------------------------------- params
    public void setParams(final AlgorithmParameterSpec params) {
        this.params = params;
    }

    public FullText params(final AlgorithmParameterSpec params) {
        setParams(params);
        return this;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final FullTextCategory category;

    final List<? extends FullTextSection> sections;

    private final int length;

    // -----------------------------------------------------------------------------------------------------------------
    private transient Cipher cipher;

    private transient Key key;

    private transient AlgorithmParameterSpec params;
}
