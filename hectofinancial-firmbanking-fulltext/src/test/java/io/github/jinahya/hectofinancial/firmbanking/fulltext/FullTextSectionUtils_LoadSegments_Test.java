package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class FullTextSectionUtils_LoadSegments_Test {

    static Stream<Arguments> getTextCategoryTextCodeAndTaskCodeArgumentsStream() {
        return Stream.of(
                Arguments.of(FullTextCategory.D, "1000", "100"),
                Arguments.of(FullTextCategory.D, "2000", "100"),
                Arguments.of(FullTextCategory.D, "2000", "200"),
                Arguments.of(FullTextCategory.F, "1000", "100")
        );
    }

    static String getResourceName(final FullTextCategory category, final String textCode, final String taskCode) {

        return category.name() + textCode + "_" + taskCode + ".segments";
    }

    @MethodSource({
            "getTextCategoryTextCodeAndTaskCodeArgumentsStream"
    })
    @ParameterizedTest
    void __(final FullTextCategory category, final String textCode, final String taskCode) {
        final var name = getResourceName(category, textCode, taskCode);
        final var segments = FullTextSectionUtils.loadSegments(name);
        assertThat(segments).isNotNull().isNotEmpty();
        segments.forEach(s -> {
            log.debug("\tsegment: {}", s);
        });
        assertThat(segments.stream().mapToInt(s -> s.length).sum()).isEqualTo(category.bodyLength);
    }
}