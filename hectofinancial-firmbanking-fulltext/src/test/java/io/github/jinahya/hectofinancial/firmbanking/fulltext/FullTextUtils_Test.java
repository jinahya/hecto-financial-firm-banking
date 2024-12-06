package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class FullTextUtils_Test {

    @DisplayName("sendData(channel, data)")
    @Nested
    class SendDataTest {

        @Test
        void _NullPointerException_ChannelIsNull() {
            // --------------------------------------------------------------------------------------------------- given
            final var data = ByteBuffer.allocate(0);
            // ----------------------------------------------------------------------------------------------- when/then
            assertThatThrownBy(() -> {
                FullTextUtils.sendData(null, data);
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
                FullTextUtils.sendData(channel, data);
            }).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("receiveData(channel)")
    @Nested
    class ReceiveDataTest {

        @Test
        void _NullPointerException_ChannelIsNull() {
            // --------------------------------------------------------------------------------------------------- given
            // ----------------------------------------------------------------------------------------------- when/then
            assertThatThrownBy(() -> {
                FullTextUtils.receiveData(null);
            }).isInstanceOf(NullPointerException.class);
        }

        @Test
        void _IllegalArgumentException_ChannelIsOpen() {
            // --------------------------------------------------------------------------------------------------- given
            final var channel = mock(ReadableByteChannel.class);
            given(channel.isOpen()).willReturn(false);
            // ----------------------------------------------------------------------------------------------- when/then
            assertThatThrownBy(() -> {
                FullTextUtils.receiveData(channel);
            }).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
