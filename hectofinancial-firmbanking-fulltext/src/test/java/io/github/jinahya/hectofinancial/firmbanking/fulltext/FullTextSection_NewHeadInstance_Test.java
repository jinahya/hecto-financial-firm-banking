package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("newHeadInstance(category)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class FullTextSection_NewHeadInstance_Test {

    @EnumSource(FullTextCategory.class)
    @ParameterizedTest
    void __(final FullTextCategory category) {
        final var instance = FullTextSection.newHeadInstance(category);
        instance.getSegments().forEach(s -> {
            log.debug("segment: {}", s);
        });
        assertThat(instance.getSegments())
                .isNotNull()
                .isNotEmpty()
                .doesNotContainNull();
        assertThat(instance.getSegments())
                .extracting(FullTextSegment::getOffset)
                .doesNotHaveDuplicates()
                .isSorted();
    }
}