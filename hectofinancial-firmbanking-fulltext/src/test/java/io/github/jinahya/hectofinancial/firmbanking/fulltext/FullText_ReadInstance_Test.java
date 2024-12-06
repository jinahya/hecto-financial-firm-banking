package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class FullText_ReadInstance_Test {

    static Stream<Arguments> getTextCategoryTextCodeAndTaskCodeArgumentsStream() {
        return FullTextSection_NewBodyInstance_Test.getTextCategoryTextCodeAndTaskCodeArgumentsStream();
    }

    @MethodSource({"getTextCategoryTextCodeAndTaskCodeArgumentsStream"})
    @ParameterizedTest
    void __(final FullTextCategory category, final String textCode, final String taskCode)
            throws IOException {
        final byte[] bytes;
        {
            final var instance = FullText.newInstance(category, textCode, taskCode);
            final var baos = new ByteArrayOutputStream();
            bytes = instance.write(baos).toByteArray();
        }
        final var instance = FullText.readInstance(
                category,
                Channels.newChannel(new ByteArrayInputStream(bytes)),
                null
        );
        assertThat(instance.getCategory()).isSameAs(category);
        assertThat(instance.getTextCode()).isEqualTo(textCode);
        assertThat(instance.getTaskCode()).isEqualTo(taskCode);
    }

    @MethodSource({"getTextCategoryTextCodeAndTaskCodeArgumentsStream"})
    @ParameterizedTest
    void __secure(final FullTextCategory category, final String textCode, final String taskCode)
            throws IOException {
        final var security = FullTextCryptoTestUtils.applyCipherKeyAndParams(
                c -> k -> p -> FullTextCrypto.newInstance(c, k, p)
        );
        final byte[] bytes;
        {
            final var instance = FullText.newInstance(category, textCode, taskCode);
            instance.setCrypto(security);
            final var baos = new ByteArrayOutputStream();
            bytes = instance.write(baos).toByteArray();
        }
        final var instance = FullText.readInstance(
                category,
                Channels.newChannel(new ByteArrayInputStream(bytes)),
                security
        );
        assertThat(instance.getCategory()).isSameAs(category);
        assertThat(instance.getTextCode()).isEqualTo(textCode);
        assertThat(instance.getTaskCode()).isEqualTo(taskCode);
    }
}