package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class FullText {

//    public static FullText from(final Iterable<? extends IFullTextSection> iterable) {
//        final var sections = StreamSupport.stream(
//                        Spliterators.spliteratorUnknownSize(iterable.iterator(), Spliterator.ORDERED),
//                        false
//                ).peek(s -> {
//                    if (s == null) {
//                        throw new IllegalArgumentException("null section");
//                    }
//                })
//                .collect(Collectors.toUnmodifiableList())) {
//            return new FullText(
//
//        };
//    }

    private FullText(final List<IFullTextSection> sections) {
        super();
        assert sections != null;
        assert !sections.isEmpty();
        this.sections = sections;
//        Objects.requireNonNull(sections, "sections is null");
//        this.sections = StreamSupport.stream(
//                        Spliterators.spliteratorUnknownSize(sections.iterator(), Spliterator.ORDERED),
//                        false
//                ).peek(s -> {
//                    if (s == null) {
//                        throw new IllegalArgumentException("null section");
//                    }
//                })
//                .collect(Collectors.toUnmodifiableList());
        final var capacity = this.sections.stream()
                .mapToInt(IFullTextSection::getLength)
                .sum();
        buffer = ByteBuffer.allocate(capacity);
    }

    // -------------------------------------------------------------------------------------------------------- sections

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
        for (buffer.reset(); buffer.hasRemaining(); ) {
            final var bytes = channel.write(buffer);
            assert bytes >= 0;
        }
    }

    public void readFrom(final ReadableByteChannel channel) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        for (buffer.reset(); buffer.hasRemaining(); ) {
            final var bytes = channel.read(buffer);
            assert bytes >= 0;
        }
    }

    /**
     * Writes this full text to specified channel while encrypting with specified cipher.
     *
     * @param cipher  the cipher.
     * @param channel the channel.
     * @throws IOException if an I/O error occurs.
     */
    public void writeTo(final Cipher cipher, WritableByteChannel channel) throws IOException {
        Objects.requireNonNull(cipher, "cipher is null");
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        var output = ByteBuffer.allocate(cipher.getOutputSize(buffer.capacity()));
        // ------------------------------------------------------------------------------------------------------ update
        for (buffer.clear(); buffer.hasRemaining(); ) {
            try {
                cipher.update(buffer, output.clear());
            } catch (final ShortBufferException sbe) {
                output = ByteBuffer.allocate(output.capacity() << 1);
            }
            for (output.flip(); output.hasRemaining(); ) {
                final var bytes = channel.write(output);
                assert bytes >= 0;
            }
        }
        // ----------------------------------------------------------------------------------------------------- doFinal
        assert !buffer.hasRemaining();
        while (true) {
            try {
                cipher.doFinal(buffer, output.clear());
                break;
            } catch (final ShortBufferException sbe) {
                output = ByteBuffer.allocate(output.capacity() << 1);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException("failed to do final", e);
            }
        }
        for (output.flip(); output.hasRemaining(); ) {
            final var bytes = channel.write(output);
            assert bytes >= 0;
        }
    }

    public void readFrom(final Cipher cipher, final ReadableByteChannel channel) throws IOException {
        Objects.requireNonNull(cipher, "cipher is null");
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        var input = ByteBuffer.allocate(cipher.getOutputSize(buffer.capacity()));
        // ------------------------------------------------------------------------------------------------------ update
        for (buffer.clear(); channel.read(input.clear()) != -1; ) {
            try {
                final var bytes = cipher.update(input.flip(), buffer);
                assert bytes >= 0;
            } catch (final ShortBufferException sbe) {
                throw new RuntimeException("failed to update", sbe);
            }
        }
        // ----------------------------------------------------------------------------------------------------- doFinal
        assert !input.hasRemaining();
        try {
            final var bytes = cipher.doFinal(input, buffer);
            assert bytes >= 0;
            assert !buffer.hasRemaining();
        } catch (final Exception e) {
            throw new RuntimeException("failed to do final", e);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final List<IFullTextSection> sections;

    private final ByteBuffer buffer;
}
