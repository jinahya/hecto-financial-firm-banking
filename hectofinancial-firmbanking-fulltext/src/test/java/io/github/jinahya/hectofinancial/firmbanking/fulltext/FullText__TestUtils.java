package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import org.junit.jupiter.params.provider.Arguments;

import java.util.Arrays;
import java.util.stream.Stream;

final class FullText__TestUtils {

    static Stream<Arguments> getTextCategoryTextCodeAndTaskCodeArgumentsStream() {
        return Stream.of(
                Arguments.of(FullTextCategory.D, "1000", "100"),
                Arguments.of(FullTextCategory.D, "1000", "200"),
                Arguments.of(FullTextCategory.D, "1000", "500"),
                Arguments.of(FullTextCategory.D, "2000", "100"),
                Arguments.of(FullTextCategory.D, "2000", "200")
        );
    }

    static Stream<Arguments> getTextCategoryTextCodeTaskCodeAndResourceNameArgumentsStream() {
        return getTextCategoryTextCodeAndTaskCodeArgumentsStream().map(a -> {
            var got = a.get();
            final var category = (FullTextCategory) got[0];
            final var textCode = (String) got[1];
            final var taskCode = (String) got[2];
            got = Arrays.copyOf(got, got.length + 1);
            got[3] = category.name() + textCode + '_' + taskCode + ".fulltext";
            return Arguments.of(got);
        });
    }

    private FullText__TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}