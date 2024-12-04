package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FullText_ReadInstance_Test {

    static Stream<Arguments> getFullTextResourceArgumentsStream() {
        return Stream.of(
                Arguments.of(
                        FullTextCategory.D,
                        "1000",
                        "100"
                )
        );
    }

    @MethodSource({"getFullTextResourceArgumentsStream"})
    @ParameterizedTest
    void __(final FullTextCategory category, final String textCode, final String taskCode)
            throws IOException {
        final var name = category.name() + textCode + '_' + taskCode + ".fulltext";
        try (var resource = FullText_ReadInstance_Test.class.getResourceAsStream(name)) {
            assertThat(resource)
                    .as("resource for %1$s", name)
                    .isNotNull();
        }
    }
}