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

@Slf4j
class FullText_ReadInstance_Test {

    static Stream<Arguments> getTextCategoryTextCodeAndTaskCodeArgumentsStream() {
        return FullTextSection_NewBodyInstance_Test.getTextCategoryTextCodeAndTaskCodeArgumentsStream();
    }

    @MethodSource({"getTextCategoryTextCodeAndTaskCodeArgumentsStream"})
    @ParameterizedTest
    void __(final FullTextCategory category, final String textCode, final String taskCode)
            throws IOException {
        final var instance1 = FullText.newInstance(category, textCode, taskCode);
        final var baos = new ByteArrayOutputStream();
        instance1.write(Channels.newChannel(baos));
        final var instance2 = FullText.readInstance(
                category,
                Channels.newChannel(new ByteArrayInputStream(baos.toByteArray())),
                null
        );
    }

    @MethodSource({"getTextCategoryTextCodeAndTaskCodeArgumentsStream"})
    @ParameterizedTest
    void __secure(final FullTextCategory category, final String textCode, final String taskCode)
            throws IOException {
        final var instance1 = FullText.newInstance(category, textCode, taskCode);
        final var security = FullTextCipherTestUtils.applyCipherKeyAndParams(
                c -> k -> p -> FullTextCipher.newInstance(c, k, p)
        );
        instance1.setCipher(security);
        final var baos = new ByteArrayOutputStream();
        instance1.write(Channels.newChannel(baos));
        final var instance2 = FullText.readInstance(
                category,
                Channels.newChannel(new ByteArrayInputStream(baos.toByteArray())),
                security
        );
    }
}