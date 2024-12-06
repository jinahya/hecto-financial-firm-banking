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

    static void sendData(final WritableByteChannel channel, final ByteBuffer data) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        Objects.requireNonNull(data, "data is null");
        // write length
        for (var b = ByteBuffer.wrap(LENGTH_CODEC.encode(data.remaining(), LENGTH_BYTES)); b.hasRemaining(); ) {
            final var bytes = channel.write(b);
            assert bytes >= 0;
        }
        // write text
        while (data.hasRemaining()) {
            final var bytes = channel.write(data);
            assert bytes >= 0;
        }
    }

    static ByteBuffer receiveData(final ReadableByteChannel channel) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isOpen()) {
            throw new IllegalArgumentException("channel is not open");
        }
        // read length
        final int length;
        {
            final var a = new byte[LENGTH_BYTES];
            for (final var b = ByteBuffer.wrap(a); b.hasRemaining(); ) {
                if (channel.read(b) == -1) {
                    throw new EOFException("unexpected end-of-file while reading length bytes");
                }
            }
            length = LENGTH_CODEC.decode(a);
        }
        // read text
        final var b = ByteBuffer.allocate(length);
        while (b.hasRemaining()) {
            if (channel.read(b) == -1) {
                throw new EOFException("unexpected end-of-file while reading text bytes");
            }
        }
        return b;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private FullTextUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}