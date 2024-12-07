package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@Slf4j
class FullText_Write_Test {

    private static Stream<Arguments> getTextCategoryTextCodeAndTaskCodeArgumentsStream() {
        return FullTextSection_NewBodyInstance_Test.getTextCategoryTextCodeAndTaskCodeArgumentsStream();
    }

    @DisplayName("write(channel)")
    @Nested
    class WriteChannelTest {

        private static Stream<Arguments> getTextCategoryTextCodeAndTaskCodeArgumentsStream() {
            return FullText_Write_Test.getTextCategoryTextCodeAndTaskCodeArgumentsStream();
        }

        @DisplayName("should throw <NullPointerException> when <channel> is <null>")
        @Test
        void _ThrowNullPointerException_ChannelIsNull() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = spy(FullText.class);
            // ----------------------------------------------------------------------------------------------- when/then
            assertThatThrownBy(() -> {
                instance.write((WritableByteChannel) null);
            }).isInstanceOf(NullPointerException.class);
        }

        @DisplayName("should throw <IllegalArgumentException> when <channel.isOpen()> returns <false>")
        @Test
        void _IllegalArgumentException_ChannelIsNotOpen() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = spy(FullText.class);
            final var channel = mock(WritableByteChannel.class);
            given(channel.isOpen()).willReturn(false);
            // ----------------------------------------------------------------------------------------------- when/then
            assertThatThrownBy(() -> {
                instance.write(channel);
            }).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("write(stream)")
    @Nested
    class WriteStreamTest {

        private static Stream<Arguments> getTextCategoryTextCodeAndTaskCodeArgumentsStream() {
            return FullText_Write_Test.getTextCategoryTextCodeAndTaskCodeArgumentsStream();
        }

        @DisplayName("should throw <NullPointerException> when <stream> is <null>")
        @Test
        void _ThrowNullPointerException_StreamIsNull() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = spy(FullText.class);
            // ----------------------------------------------------------------------------------------------- when/then
            assertThatThrownBy(() -> {
                instance.write((OutputStream) null);
            }).isInstanceOf(NullPointerException.class);
        }
    }

    @MethodSource({"getTextCategoryTextCodeAndTaskCodeArgumentsStream"})
    @ParameterizedTest
    void __(final FullTextCategory category, final String textCode, final String taskCode) throws IOException {
        // ------------------------------------------------------------------------------------------------------- given
        final var instance = FullText.newInstance(category, textCode, taskCode);
        final var baos = new ByteArrayOutputStream();
        // -------------------------------------------------------------------------------------------------------- when
        final var bytes = instance.write(baos).toByteArray();
        assertThat(bytes).isNotNull().hasSize(FullTextUtils.LENGTH_BYTES + instance.getLength());
        final var data = Arrays.copyOfRange(bytes, FullTextUtils.LENGTH_BYTES, bytes.length);
        instance.setData(ByteBuffer.wrap(data));
        // -------------------------------------------------------------------------------------------------------- then
        assertThat(instance.getCategory()).isEqualTo(category);
        assertThat(instance.getTextCode()).isEqualTo(textCode);
        assertThat(instance.getTaskCode()).isEqualTo(taskCode);
    }

    @MethodSource({"getTextCategoryTextCodeAndTaskCodeArgumentsStream"})
    @ParameterizedTest
    void __WithSecurity(final FullTextCategory category, final String textCode, final String taskCode)
            throws IOException {
        // ------------------------------------------------------------------------------------------------------- given
        final var instance = FullText.newInstance(category, textCode, taskCode);
        FullTextCrypto_TestUtils.acceptFullTextCrypto(instance::setCrypto);
        final var baos = new ByteArrayOutputStream();
        // -------------------------------------------------------------------------------------------------------- when
        final var bytes = instance.write(baos).toByteArray();
        assertThat(bytes).isNotNull().hasSizeGreaterThanOrEqualTo(FullTextUtils.LENGTH_BYTES + instance.getLength());
        final var data = Arrays.copyOfRange(bytes, FullTextUtils.LENGTH_BYTES, bytes.length);
        instance.setData(ByteBuffer.wrap(data));
        // -------------------------------------------------------------------------------------------------------- then
        assertThat(instance.getCategory()).isEqualTo(category);
        assertThat(instance.getTextCode()).isEqualTo(textCode);
        assertThat(instance.getTaskCode()).isEqualTo(taskCode);
    }
}