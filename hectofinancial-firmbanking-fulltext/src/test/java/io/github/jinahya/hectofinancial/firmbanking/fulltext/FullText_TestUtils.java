package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.provider.Arguments;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
final class FullText_TestUtils {

    static Stream<Arguments> getCategoryTextCodeAndTaskCodeArgumentsStream() {
        return Stream.of(
                Arguments.of(FullTextCategory.D, "1000", "500")
        );
    }

    static String getResourceNameForFullText(final FullTextCategory category, final String textCode,
                                             final String taskCode) {
        return category.name() + textCode + '_' + taskCode + ".fulltext";
    }

    static byte[] loadFullTextData(final FullTextCategory category, final String textCode, final String taskCode) {
        final var name = getResourceNameForFullText(category, textCode, taskCode);
        try (var resource = FullText_TestUtils.class.getResourceAsStream(name)) {
            assertThat(resource)
                    .as("resource for '%1$s'", name)
                    .isNotNull();
            final var instance = FullText.newInstance(category, textCode, taskCode);
            return resource.readNBytes(instance.getLength());
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    static FullText loadFullText(final FullTextCategory category, final String textCode, final String taskCode) {
        final var instance = FullText.newInstance(category, textCode, taskCode);
        final var data = loadFullTextData(category, textCode, taskCode);
        instance.setRawData(ByteBuffer.wrap(data));
        assertThat(instance.getTextCode()).isEqualTo(textCode);
        assertThat(instance.getTaskCode()).isEqualTo(taskCode);
        return instance;
    }

    private FullText_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}