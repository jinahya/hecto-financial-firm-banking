package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import javax.crypto.Cipher;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class FullText {

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS
    public static FullText newInstance(final FullTextCategory category, final String textCode,
                                       final String taskCode) {
        final var sections = List.of(
                FullTextSection.newHeadInstance(category),
                FullTextSection.newInstance(category, textCode, taskCode)
        );
        final var text = new FullText(category, sections) {
        };
        text.sections.forEach(s -> s.setText(text));
        return text;
    }

    public static FullText readInstance(final FullTextCategory category,
                                        final ReadableByteChannel channel)
            throws IOException {
        Objects.requireNonNull(category, "category is null");
        final var buffer = FullTextUtils.readBuffer(channel).clear();
        final var textCode = category.getTextCode(buffer);
        final var taskCode = category.getTaskCode(buffer);
        return newInstance(category, textCode, taskCode).setData(buffer.clear());
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private FullText(final FullTextCategory category,
                     final List<? extends FullTextSection> sections) {
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

    // -----------------------------------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------------------------------
    public String getDataString() {
        return FullTextSegmentCodecX.CHARSET.decode(buffer.clear()).toString();
    }

    // -------------------------------------------------------------------------------------------------------- sections

    /**
     * Applies the section of specified index to specified function, and return the result.
     *
     * @param index    the section index; starting at {@code 1}.
     * @param function the function to be applied with the section of {@code index}.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
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
     *
     * @param index    the section index; starting at {@code 1}.
     * @param consumer the consumer to be accepted with the section of {@code index}.
     * @see #applySection(int, Function)
     */
    public void acceptSection(final int index, final Consumer<? super FullTextSection> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applySection(index, s -> {
            consumer.accept(s);
            return null;
        });
    }

    // ---------------------------------------------------------------------------------------------------------- buffer
    public <T extends ByteBuffer> T getData(final T dst) {
        Objects.requireNonNull(dst, "dst is null");
        return (T) dst.put(buffer.clear());
    }

    public byte[] getData(final byte[] dst) {
        return ByteBuffer.wrap(Objects.requireNonNull(dst, "dst is null")).array();
    }

    public FullText setData(final ByteBuffer src) {
        Objects.requireNonNull(src, "src is null");
        buffer.clear().put(src);
        return this;
    }

    public FullText setData(final byte[] src) {
        return setData(ByteBuffer.wrap(Objects.requireNonNull(src, "src is null")));
    }

    /**
     * Returns the value of {@code 전문구분코드} of this full text.
     *
     * @return the value of {@code 전문구분코드} of this full text.
     */
    public final String getTextCode() {
        return category.getTextCode(buffer);
    }

    public FullText setTextCode(final String textCode) {
        Objects.requireNonNull(textCode, "textCode is null");
        category.setTextCode(buffer, textCode);
        return this;
    }

    /**
     * Returns the value of {@code 업무구분코드} of this full text.
     *
     * @return the value of {@code 업무구분코드} of this full text.
     */
    public final String getTaskCode() {
        return category.getTaskCode(buffer);
    }

    public FullText setTaskCode(final String taskCode) {
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
    public final FullText write(final WritableByteChannel channel) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        FullTextUtils.writeBuffer(channel, buffer.clear());
        return this;
    }

    public final FullText read(final ReadableByteChannel channel) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        return setData(FullTextUtils.readBuffer(channel).flip());
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
        FullTextUtils.writeBuffer(channel, output.flip());
        return this;
    }

    public FullText read(final ReadableByteChannel channel, final Cipher cipher) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        Objects.requireNonNull(cipher, "cipher is null");
        final var input = FullTextUtils.readBuffer(channel);
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
