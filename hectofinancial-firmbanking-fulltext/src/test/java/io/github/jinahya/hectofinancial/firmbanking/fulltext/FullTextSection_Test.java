package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.intThat;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
class FullTextSection_Test {

    private static Stream<Arguments> getCategoryAndHeadSectionArgumentsStream() {
        return FullTextSection_TestUtils.getCategoryAndHeadSectionArgumentsStream();
    }

    @Nested
    class GetDateTest {

        private static Stream<Arguments> getCategoryAndHeadSectionArgumentsStream() {
            return FullTextSection_Test.getCategoryAndHeadSectionArgumentsStream();
        }

        @MethodSource({"getCategoryAndHeadSectionArgumentsStream"})
        @ParameterizedTest
        void __(final FullTextCategory category, final FullTextSection headSection) {
            final var spy = Mockito.spy(headSection);
            assertThat(category.getHeadDate(headSection)).isNull();
//            verify(spy, times(1)).getDate(intThat(i -> i > 0));
        }
    }

    @Nested
    class SetDateTest {

        private static Stream<Arguments> getCategoryAndHeadSectionArgumentsStream() {
            return FullTextSection_Test.getCategoryAndHeadSectionArgumentsStream();
        }

        @MethodSource({"getCategoryAndHeadSectionArgumentsStream"})
        @ParameterizedTest
        void __(final FullTextCategory category, final FullTextSection headSection) {
            final var spy = Mockito.spy(headSection);
            category.setHeadDate(spy, null);
            verify(spy, times(1)).setDate(intThat(i -> i > 0), isNull());
        }
    }
}