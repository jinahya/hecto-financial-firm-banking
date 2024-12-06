package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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