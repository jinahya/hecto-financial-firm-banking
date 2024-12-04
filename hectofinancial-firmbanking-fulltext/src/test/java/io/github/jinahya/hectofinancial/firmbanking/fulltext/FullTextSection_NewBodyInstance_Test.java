package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("newBodyInstance(category, textCode, taskCode)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class FullTextSection_NewBodyInstance_Test {

    static Stream<Arguments> getTextCategoryTextCodeAndTaskCodeArgumentsStream() {
        return Stream.of(
                Arguments.of(FullTextCategory.D, "1000", "100"),
                Arguments.of(FullTextCategory.D, "1000", "200"),
                Arguments.of(FullTextCategory.D, "1000", "500"),
                Arguments.of(FullTextCategory.D, "2000", "100"),
                Arguments.of(FullTextCategory.D, "2000", "200"),
                Arguments.of(FullTextCategory.D, "2000", "550"),
                Arguments.of(FullTextCategory.D, "2000", "650"),
                Arguments.of(FullTextCategory.D, "7000", "100"),
                Arguments.of(FullTextCategory.D, "7000", "200")
        );
    }

    @MethodSource({"getTextCategoryTextCodeAndTaskCodeArgumentsStream"})
    @ParameterizedTest
    void __(final FullTextCategory category, final String textCode, final String taskCode) {
        final var instance = FullTextSection.newBodyInstance(category, textCode, taskCode);
        assertThat(instance.getSegments())
                .isNotNull()
                .isNotEmpty()
                .doesNotContainNull().allSatisfy(s -> {
                    log.debug("segment: {}", s);
                    assertThat(s.getLength())
                            .as("length(%1$d) of %2$s", s.getLength(), s)
                            .isNotNegative();
                    assertThat(s.getOffset())
                            .as("offset(%1$d) of %2$s", s.getOffset(), s)
                            .isNotNegative();
                });
        assertThat(instance.getSegments())
                .extracting(FullTextSegment::getOffset)
                .as("all offsets")
                .doesNotHaveDuplicates()
                .isSorted();
    }
}