package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("newInstance(category, textCode, taskCode)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class FullText_NewInstance_Test {

    static Stream<Arguments> getTextCategoryTextCodeTaskCodeAndResourceNameArgumentsStream() {
        return FullText__TestUtils.getTextCategoryTextCodeTaskCodeAndResourceNameArgumentsStream();
    }

    static Stream<Arguments> getTextCategoryTextCodeAndTaskCodeArgumentsStream() {
        return FullTextSection_NewBodyInstance_Test.getTextCategoryTextCodeAndTaskCodeArgumentsStream();
    }

    @MethodSource({
            "getTextCategoryTextCodeAndTaskCodeArgumentsStream"
    })
    @ParameterizedTest
    void __(final FullTextCategory category, final String textCode, final String taskCode)
            throws IOException {
        final var text = FullText.newInstance(category, textCode, taskCode);
        assertThat(text).isNotNull().satisfies(t -> {
            assertThat(t.sections)
                    .isNotNull()
                    .doesNotContainNull()
                    .hasSizeGreaterThanOrEqualTo(2)
                    .allSatisfy(s -> {
                        assertThat(s.getLength()).isPositive();
                    });
        });
        assertThat(text.getTextCode()).isEqualTo(textCode);
        assertThat(text.getTaskCode()).isEqualTo(taskCode);
        log.debug("decoded: [{}]", text.getDataString());
    }
}