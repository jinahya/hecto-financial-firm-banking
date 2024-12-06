package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class FullTextUtils_Test {

    @DisplayName("readLength(channel)")
    @Nested
    class ReadLengthTest {

        @Test
        void readLength() throws IOException {
            final var expected = ThreadLocalRandom.current().nextInt(2048) + 1;
            final var bytes = FullTextSegmentCodec.of9().encode(expected, FullTextUtils.LENGTH_BYTES);
            final var channel = Channels.newChannel(new ByteArrayInputStream(bytes));
            final var actual = FullTextUtils.readLength(channel);
            assertThat(actual).isEqualTo(expected);
        }
    }

    @DisplayName("writeLength(channel, length)")
    @Nested
    class WriteLengthTest {

        @Test
        void readLength() throws IOException {
            final var expected = ThreadLocalRandom.current().nextInt(2048) + 1;
            final var baos = new ByteArrayOutputStream();
            final var channel = Channels.newChannel(baos);
            FullTextUtils.writeLength(channel, expected);
            final var bytes = baos.toByteArray();
            final var actual = FullTextSegmentCodec.of9().decode(bytes);
            assertThat(actual).isEqualTo(expected);
        }
    }

    @DisplayName("writeData(channel, data)")
    @Nested
    class WriteDataTest {

        @Test
        void _NullPointerException_ChannelIsNull() {
            // --------------------------------------------------------------------------------------------------- given
            final var data = ByteBuffer.allocate(0);
            // ----------------------------------------------------------------------------------------------- when/then
            assertThatThrownBy(() -> {
                FullTextUtils.writeData(null, data);
            }).isInstanceOf(NullPointerException.class);
        }

        @Test
        void _IllegalArgumentException_ChannelIsOpen() {
            // --------------------------------------------------------------------------------------------------- given
            final var channel = mock(WritableByteChannel.class);
            given(channel.isOpen()).willReturn(false);
            final var data = ByteBuffer.allocate(0);
            // ----------------------------------------------------------------------------------------------- when/then
            assertThatThrownBy(() -> {
                FullTextUtils.writeData(channel, data);
            }).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("readData(channel)")
    @Nested
    class ReadDataTest {

        @Test
        void _NullPointerException_ChannelIsNull() {
            // --------------------------------------------------------------------------------------------------- given
            // ----------------------------------------------------------------------------------------------- when/then
            assertThatThrownBy(() -> {
                FullTextUtils.readData(null);
            }).isInstanceOf(NullPointerException.class);
        }

        @Test
        void _IllegalArgumentException_ChannelIsOpen() {
            // --------------------------------------------------------------------------------------------------- given
            final var channel = mock(ReadableByteChannel.class);
            given(channel.isOpen()).willReturn(false);
            // ----------------------------------------------------------------------------------------------- when/then
            assertThatThrownBy(() -> {
                FullTextUtils.readData(channel);
            }).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
