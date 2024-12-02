package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

final class FullTextUtils {

    // -----------------------------------------------------------------------------------------------------------------
    static final int LENGTH_BYTES = 4;

    private static final FullTextSegmentCodec<Integer> LENGTH_CODEC = FullTextSegmentCodec.of9();

    static void writeBuffer(final FullTextCategory category, final WritableByteChannel channel, final ByteBuffer buffer)
            throws IOException {
        Objects.requireNonNull(category, "category is null");
        Objects.requireNonNull(buffer, "buffer is null");
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        // write length
        for (var b = ByteBuffer.wrap(LENGTH_CODEC.encode(buffer.capacity(), LENGTH_BYTES)); b.hasRemaining(); ) {
            final var bytes = channel.write(b);
            assert bytes >= 0;
        }
        // write text
        for (buffer.clear(); buffer.hasRemaining(); ) {
            final var bytes = channel.write(buffer);
            assert bytes >= 0;
        }
    }

    static ByteBuffer readBuffer(final FullTextCategory category, final ReadableByteChannel channel)
            throws IOException {
        Objects.requireNonNull(category, "category is null");
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        // read length
        final int length;
        {
            final var b = ByteBuffer.allocate(LENGTH_BYTES);
            while (b.hasRemaining()) {
                if (channel.read(b) == -1) {
                    throw new EOFException("unexpected end-of-file while reading length bytes");
                }
            }
            length = LENGTH_CODEC.decode(b.array(), LENGTH_BYTES);
        }
        final var b = ByteBuffer.allocate(length);
        while (b.hasRemaining()) {
            if (channel.read(b) == -1) {
                throw new EOFException("unexpected end-of-file while reading a full text");
            }
        }
        return b;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private FullTextUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}