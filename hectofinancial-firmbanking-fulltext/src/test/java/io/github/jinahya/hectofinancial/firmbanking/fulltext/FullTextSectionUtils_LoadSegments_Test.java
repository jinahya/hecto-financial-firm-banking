package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class FullTextSectionUtils_LoadSegments_Test {

    static Stream<Arguments> getTextCategoryTextCodeAndTaskCodeArgumentsStream() {
        return Stream.of(
                Arguments.of(FullTextCategory.D, "1000", "100", 200),
                Arguments.of(FullTextCategory.D, "2000", "100", 200),
                Arguments.of(FullTextCategory.D, "2000", "200", 200),
                Arguments.of(FullTextCategory.D, "2000", "550", 200),
                Arguments.of(FullTextCategory.D, "2000", "650", 200),
                Arguments.of(FullTextCategory.D, "3000", "100", 200),
                Arguments.of(FullTextCategory.D, "3000", "200", 200),
                Arguments.of(FullTextCategory.D, "4000", "100", 200),
                Arguments.of(FullTextCategory.D, "4100", "100", 300), // 얜 body length 가 또 300 이네...
                Arguments.of(FullTextCategory.D, "7000", "100", 200),
                Arguments.of(FullTextCategory.D, "7000", "200", 200),
                Arguments.of(FullTextCategory.F, "1000", "100", 1900)
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    @EnumSource(FullTextCategory.class)
    @ParameterizedTest
    void loadHeadSegments__(final FullTextCategory category) {
        final var segments = FullTextSectionUtils.loadHeadSegments(category);
        assertThat(segments).isNotNull().isNotEmpty();
        segments.forEach(s -> {
            log.debug("\tsegment: {}", s);
        });
    }

    @MethodSource({
            "getTextCategoryTextCodeAndTaskCodeArgumentsStream"
    })
    @ParameterizedTest
    void loadBodySegments__(final FullTextCategory category, final String textCode, final String taskCode,
                            final int bodyLength) {
        final var segments = FullTextSectionUtils.loadBodySegments(category, textCode, taskCode);
        assertThat(segments).isNotNull().isNotEmpty();
        segments.forEach(s -> {
            log.debug("\tsegment: {}", s);
        });
        assertThat(segments.stream().mapToInt(s -> s.length).sum())
                .as("sum of all lengths")
                .isEqualTo(bodyLength);
    }
}