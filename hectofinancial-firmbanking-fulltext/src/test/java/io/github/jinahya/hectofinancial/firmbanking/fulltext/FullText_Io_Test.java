package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class FullText_Io_Test {

    static Stream<Arguments> getTextCategoryTextCodeAndTaskCodeArgumentsStream() {
        return FullTextSectionUtils_LoadSegments_Test.getTextCategoryTextCodeAndTaskCodeArgumentsStream();
    }

    @DisplayName("newInstance(textCode, taskCode)")
    @MethodSource({
            "getTextCategoryTextCodeAndTaskCodeArgumentsStream"
    })
    @ParameterizedTest
    void __(final FullTextCategory category, final String textCode, final String taskCode)
            throws IOException {
        final var text = FullText.newInstance(category, textCode, taskCode);
        final byte[] written;
        {
            final var baos = new ByteArrayOutputStream();
            final var result = text.write(Channels.newChannel(baos));
            assertThat(result).isSameAs(text);
            written = baos.toByteArray();
            assertThat(written).hasSize(category.textLength + FullTextUtils.LENGTH_BYTES);
        }
    }
}