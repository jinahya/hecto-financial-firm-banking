package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.intThat;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.spy;
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
            final var date = headSection.getDate(FullTextConstants.SEGMENT_INDEX_HEAD_DATE_D);
            assertThat(date).isNull();
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
            final var spy = spy(headSection);
            category.setHeadDate(spy, null);
            verify(spy, times(1)).setDate(intThat(i -> i > 0), isNull());
        }
    }

    @Nested
    class GetTimeTest {

        private static Stream<Arguments> getCategoryAndHeadSectionArgumentsStream() {
            return FullTextSection_Test.getCategoryAndHeadSectionArgumentsStream();
        }

        @MethodSource({"getCategoryAndHeadSectionArgumentsStream"})
        @ParameterizedTest
        void __(final FullTextCategory category, final FullTextSection headSection) {
            final var time = headSection.getTime(category.headDateSegmentIndex);
            assertThat(time).isNull();
        }

        @MethodSource({"getCategoryAndHeadSectionArgumentsStream"})
        @ParameterizedTest
        void __000000(final FullTextCategory category, final FullTextSection headSection) {
            final var timeSegment = headSection.getSegments().get(FullTextConstants.SEGMENT_INDEX_HEAD_TIME_D - 1);
            final var headTime = LocalTime.of(0, 0, 0).withNano(0);
            final var s = FullTextConstants.DATE_TIME_FORMATTER_TIME.format(headTime);
            final var b = s.getBytes(FullTextConstants.CHARSET);
            log.debug("s: {}, b: {}", s, b);
            headSection.getBuffer().put(timeSegment.getOffset(), b);
            final var time = headSection.getTime(category.headTimeSegmentIndex);
            assertThat(time).isEqualTo(headTime);
        }
    }

    @Nested
    class SetTimeTest {

        private static Stream<Arguments> getCategoryAndHeadSectionArgumentsStream() {
            return FullTextSection_Test.getCategoryAndHeadSectionArgumentsStream();
        }

        @MethodSource({"getCategoryAndHeadSectionArgumentsStream"})
        @ParameterizedTest
        void __(final FullTextCategory category, final FullTextSection headSection) {
            final var spy = spy(headSection);
            category.setHeadTime(spy, null);
            verify(spy, times(1)).setTime(intThat(i -> i > 0), isNull());
        }
    }
}