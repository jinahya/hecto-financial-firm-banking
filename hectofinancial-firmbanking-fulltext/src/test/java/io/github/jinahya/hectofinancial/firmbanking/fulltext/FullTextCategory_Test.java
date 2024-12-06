package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("newHeadInstance(category)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class FullTextCategory_Test {

    @EnumSource(FullTextCategory.class)
    @ParameterizedTest
    void getHeadDate_NumberFormatException_(final FullTextCategory category) {
        final var headSection = FullTextSection.newHeadInstance(category);
        assertThat(category.getHeadDate(headSection)).isNull();
    }

    @EnumSource(FullTextCategory.class)
    @ParameterizedTest
    void setHeadDate_NumberFormatException_(final FullTextCategory category) {
        final var headSection = FullTextSection.newHeadInstance(category);
        final var headDate = LocalDate.now();
        category.setHeadDate(headSection, headDate);
        assertThat(category.getHeadDate(headSection)).isEqualTo(headDate);
    }

    @EnumSource(FullTextCategory.class)
    @ParameterizedTest
    void getHeadTime_NumberFormatException_(final FullTextCategory category) {
        final var headSection = FullTextSection.newHeadInstance(category);
        assertThat(category.getHeadTime(headSection)).isNull();
    }

    @EnumSource(FullTextCategory.class)
    @ParameterizedTest
    void setHeadTime_NumberFormatException_(final FullTextCategory category) {
        final var headSection = FullTextSection.newHeadInstance(category);
        final var headTime = LocalTime.now().withNano(0); // HHmmss
        category.setHeadTime(headSection, headTime);
        assertThat(category.getHeadTime(headSection)).isEqualTo(headTime);
    }

    // ---------------------------------------------------------------------------------------------------- headTextCode
    @EnumSource(FullTextCategory.class)
    @ParameterizedTest
    void getTextCode__(final FullTextCategory category) {
        final var headSection = FullTextSection.newHeadInstance(category);
        assertThat(category.getHeadTextCode(headSection.getBuffer())).isBlank();
    }

    @EnumSource(FullTextCategory.class)
    @ParameterizedTest
    void setTextCode__(final FullTextCategory category) {
        final var headSection = FullTextSection.newHeadInstance(category);
        category.setHeadTextCode(headSection.getBuffer(), "0000");
    }

    // ---------------------------------------------------------------------------------------------------- headTaskCode
    @EnumSource(FullTextCategory.class)
    @ParameterizedTest
    void getTaskCode__(final FullTextCategory category) {
        final var headSection = FullTextSection.newHeadInstance(category);
        assertThat(category.getHeadTaskCode(headSection.getBuffer())).isBlank();
    }

    @EnumSource(FullTextCategory.class)
    @ParameterizedTest
    void setTaskCode__(final FullTextCategory category) {
        final var headSection = FullTextSection.newHeadInstance(category);
        category.setHeadTaskCode(headSection.getBuffer(), "000");
    }
}