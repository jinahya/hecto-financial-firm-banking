package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class FullText_Write_Test {

    static Stream<Arguments> getTextCategoryTextCodeAndTaskCodeArgumentsStream() {
        return FullTextSection_NewBodyInstance_Test.getTextCategoryTextCodeAndTaskCodeArgumentsStream();
    }

    @MethodSource({"getTextCategoryTextCodeAndTaskCodeArgumentsStream"})
    @ParameterizedTest
    void __(final FullTextCategory category, final String textCode, final String taskCode) throws IOException {
        final var instance = FullText.newInstance(category, textCode, taskCode);
        final var baos = new ByteArrayOutputStream();
        instance.write(Channels.newChannel(baos));
        final var bytes = baos.toByteArray();
        instance.setData(ByteBuffer.wrap(Arrays.copyOfRange(bytes, FullTextUtils.LENGTH_BYTES, bytes.length)));
        assertThat(instance.getTextCode()).isEqualTo(textCode);
        assertThat(instance.getTaskCode()).isEqualTo(taskCode);
    }

    @MethodSource({"getTextCategoryTextCodeAndTaskCodeArgumentsStream"})
    @ParameterizedTest
    void __secure(final FullTextCategory category, final String textCode, final String taskCode) throws IOException {
        final var instance = FullText.newInstance(category, textCode, taskCode);
        FullTextSecurityTestUtils.acceptCipherKeyAndParams(c -> k -> p -> {
            instance.setSecurity(FullTextSecurity.newInstance(c, k, p));
        });
        final var baos = new ByteArrayOutputStream();
        instance.write(Channels.newChannel(baos));
        final var bytes = baos.toByteArray();
        instance.setData(ByteBuffer.wrap(Arrays.copyOfRange(bytes, FullTextUtils.LENGTH_BYTES, bytes.length)));
        assertThat(instance.getTextCode()).isEqualTo(textCode);
        assertThat(instance.getTaskCode()).isEqualTo(taskCode);
    }
}