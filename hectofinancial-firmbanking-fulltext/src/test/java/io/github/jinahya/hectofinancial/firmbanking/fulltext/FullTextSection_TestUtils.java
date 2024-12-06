package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Arrays;
import java.util.stream.Stream;

@Slf4j
final class FullTextSection_TestUtils {

    static Stream<Arguments> getCategoryAndHeadSectionArgumentsStream() {
        return Arrays.stream(FullTextCategory.values())
                .map(c -> Arguments.of(c, FullTextSection.newHeadInstance(c)));
    }

    private FullTextSection_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}