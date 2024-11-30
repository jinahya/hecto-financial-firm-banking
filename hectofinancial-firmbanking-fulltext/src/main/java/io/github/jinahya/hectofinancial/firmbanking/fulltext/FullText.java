package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import javax.crypto.Cipher;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.StreamSupport;

public abstract class FullText {

    private static final int LENGTH_BYTES = 4;

    private static final FullTextSegmentCodec<Integer> LENGTH_CODEC = FullTextSegmentCodec.of9();

    private static void writeBufferTo(final WritableByteChannel channel, final ByteBuffer buffer) throws IOException {
        Objects.requireNonNull(buffer, "buffer is null");
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        // write length
        for (var b = ByteBuffer.wrap(LENGTH_CODEC.encode(buffer.remaining(), LENGTH_BYTES)); b.hasRemaining(); ) {
            final var bytes = channel.write(b);
            assert bytes >= 0;
        }
        // write text
        while (buffer.hasRemaining()) {
            final var bytes = channel.write(buffer);
            assert bytes >= 0;
        }
    }

    private static ByteBuffer readBufferFrom(final ReadableByteChannel channel) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        // read length
        final int length;
        {
            final var b = ByteBuffer.allocate(LENGTH_BYTES);
            while (b.hasRemaining()) {
                final var bytes = channel.read(b);
                assert bytes >= 0;
            }
            length = LENGTH_CODEC.decode(b.array(), LENGTH_BYTES);
        }
        final var b = ByteBuffer.allocate(length);
        while (b.hasRemaining()) {
            final var bytes = channel.read(b);
            assert bytes >= 0;
        }
        return b;
    }

    // -----------------------------------------------------------------------------------------------------------------
    public static final int SECTION_INDEX_1 = 1;

    public static final int SECTION_INDEX_2 = 2;

    // -----------------------------------------------------------------------------------------------------------------
    public static FullText from(final Iterable<? extends FullTextSection> iterable) {
        Objects.requireNonNull(iterable, "iterable is null");
        final var sections = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterable.iterator(), Spliterator.ORDERED),
                false
        ).peek(s -> {
            if (s == null) {
                throw new IllegalArgumentException("null section is not allowed");
            }
        }).toList();
        return new FullText(sections) {
        };
    }

    // -----------------------------------------------------------------------------------------------------------------
    private FullText(final List<? extends FullTextSection> sections) {
        super();
        assert sections != null;
        assert !sections.isEmpty();
        this.sections = sections;
        final var capacity = this.sections.stream()
                .mapToInt(FullTextSection::getLength)
                .sum();
        buffer = ByteBuffer.allocate(capacity);
    }

    // -------------------------------------------------------------------------------------------------------- sections
    public <R> R applySection(final int index, final Function<? super FullTextSection, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        if (index <= 0) {
            throw new IllegalArgumentException("index(" + index + ") is not positive");
        }
        if (index >= sections.size()) {
            throw new IllegalArgumentException(
                    "index(" + index + ") > sections.size(" + sections.size() + ")"
            );
        }
        return function.apply(sections.get(index));
    }

    public void acceptSection(final int index, final Consumer<? super FullTextSection> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applySection(index, s -> {
            consumer.accept(s);
            return null;
        });
    }

    public <V> V getValue(final int sectionIndex, final int segmentIndex) {
        return applySection(sectionIndex, s -> s.getValue(segmentIndex, buffer));
    }

    public <V> void setValue(final int sectionIndex, final int segmentIndex, final V value) {
        acceptSection(
                sectionIndex,
                s -> s.setValue(segmentIndex, buffer, value)
        );
    }

    public <V> FullText value(final int sectionIndex, final int segmentIndex, final V value) {
        setValue(sectionIndex, segmentIndex, value);
        return this;
    }

    // ---------------------------------------------------------------------------------------------------------- buffer

    /**
     * Writes this full text to specified channel.
     *
     * @param channel the channel.
     * @throws IOException if an I/O error occurs.
     */
    public void writeTo(final WritableByteChannel channel) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        writeBufferTo(channel, buffer.clear());
    }

    public void readFrom(final ReadableByteChannel channel) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        buffer.clear().put(readBufferFrom(channel).flip());
    }

    /**
     * Writes this full text to specified channel while encrypting with specified cipher.
     *
     * @param channel the channel.
     * @param cipher  the cipher.
     * @throws IOException if an I/O error occurs.
     */
    public void writeTo(final WritableByteChannel channel, final Cipher cipher) throws IOException {
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
        writeBufferTo(channel, output.flip());
    }

    public void readFrom(final ReadableByteChannel channel, final Cipher cipher) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        Objects.requireNonNull(cipher, "cipher is null");
        final var input = readBufferFrom(channel);
        try {
            final var bytes = cipher.doFinal(input.flip(), buffer.clear());
            assert bytes >= 0;
        } catch (final Exception e) {
            throw new RuntimeException("failed to decrypt", e);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final List<? extends FullTextSection> sections;

    private final ByteBuffer buffer;
}
