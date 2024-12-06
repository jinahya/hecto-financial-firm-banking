package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

/**
 * Utilities for {@link FullText} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class FullTextUtils {

    // -----------------------------------------------------------------------------------------------------------------
    static final int LENGTH_BYTES = 4;

    private static final FullTextSegmentCodec<Integer> LENGTH_CODEC = FullTextSegmentCodec.of9();

    /**
     * Writes specified length to specified channel.
     *
     * @param channel the channel.
     * @param length  the value to write.
     * @throws IOException if an I/O error occurs.
     * @see #readLength(ReadableByteChannel)
     */
    public static void writeLength(final WritableByteChannel channel, final int length) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (length <= 0) {
            throw new IllegalArgumentException("length(" + length + ") is not positive");
        }
        for (var b = ByteBuffer.wrap(LENGTH_CODEC.encode(length, LENGTH_BYTES)); b.hasRemaining(); ) {
            final var bytes = channel.write(b);
            assert bytes >= 0;
        }
    }

    /**
     * Reads a value of {@code length} from specified channel.
     *
     * @param channel the channel.
     * @return a value of {@code length}.
     * @throws IOException if an I/O error occurs.
     * @see #writeLength(WritableByteChannel, int)
     */
    public static int readLength(final ReadableByteChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        final var a = new byte[LENGTH_BYTES];
        for (final var b = ByteBuffer.wrap(a); b.hasRemaining(); ) {
            if (channel.read(b) == -1) {
                throw new EOFException("unexpected end-of-file while reading data length");
            }
        }
        return LENGTH_CODEC.decode(a);
    }

    static void writeData(final WritableByteChannel channel, final ByteBuffer data) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        Objects.requireNonNull(data, "data is null");
        writeLength(channel, data.remaining());
        while (data.hasRemaining()) {
            final var bytes = channel.write(data);
            assert bytes >= 0;
        }
    }

    static ByteBuffer readData(final ReadableByteChannel channel) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        final int length = readLength(channel);
        final var data = ByteBuffer.allocate(length);
        while (data.hasRemaining()) {
            if (channel.read(data) == -1) {
                throw new EOFException("unexpected end-of-file while reading data");
            }
        }
        return data;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private FullTextUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}