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
                Arguments.of(FullTextCategory.F, "1000", "100")
        );
    }

    static Stream<String> getNameStream() {
        return getTextCategoryTextCodeAndTaskCodeArgumentsStream().map(a -> {
            final var got = a.get();
            return ((FullTextCategory) got[0]).name() + got[1] + "_" + got[2] + ".segments";
        });
    }

    @MethodSource({
            "getNameStream"
    })
    @ParameterizedTest
    void __(final String name) {
        log.debug("name: {}", name);
        final var segments = FullTextSectionUtils.loadSegments(name);
        assertThat(segments).isNotNull().isNotEmpty();
        segments.forEach(s -> {
            log.debug("\tsegment: {}", s);
        });
    }
}