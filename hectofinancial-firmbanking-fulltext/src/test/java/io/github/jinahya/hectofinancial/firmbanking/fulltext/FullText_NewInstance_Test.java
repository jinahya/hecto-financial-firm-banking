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

    static Stream<Arguments> getTextCategoryTextCodeAndTaskCodeArgumentsStream() {
        return FullText__TestUtils.getTextCategoryTextCodeAndTaskCodeArgumentsStream();
    }

    static Stream<Arguments> getTextCategoryTextCodeTaskCodeAndResourceNameArgumentsStream() {
        return FullText__TestUtils.getTextCategoryTextCodeTaskCodeAndResourceNameArgumentsStream();
    }

    @MethodSource({
            "getTextCategoryTextCodeTaskCodeAndResourceNameArgumentsStream"
    })
    @ParameterizedTest
    void __(final FullTextCategory category, final String textCode, final String taskCode)
            throws IOException {
        final var text = FullText.newInstance(category, textCode, taskCode);
        assertThat(text).isNotNull().satisfies(t -> {
            assertThat(t.sections)
                    .isNotNull()
                    .doesNotContainNull()
                    .hasSize(2)
                    .allSatisfy(s -> {
                        assertThat(s.getLength()).isPositive();
                    });
        });
        assertThat(text.getTextCode()).isEqualTo(textCode);
        assertThat(text.getTaskCode()).isEqualTo(taskCode);
        log.debug("decoded: [{}]", text.getDataString());
//        try (var resource = FullText_NewInstance_Test.class.getResourceAsStream(resourceName)) {
//            if (resource == null) {
//                return;
//            }
//            final var bytes = resource.readNBytes(category.textLength);
//            text.setData(bytes);
//            assertThat(text.getTextCode()).isEqualTo(textCode);
//            assertThat(text.getTaskCode()).isEqualTo(taskCode);
//        }
//        text.acceptSection(FullTextConstants.SECTION_INDEX_HEAD, s -> {
//            s.value(1, "SETTLEBNK");
//            s.value(2, "jt3oc;tsmv");
//            s.value(3, "002");
//            s.value(7, ThreadLocalRandom.current().nextInt(950000) + 1);
//            s.date_(8, LocalDate.now());
//            s.time_(9, LocalTime.now());
//            s.value(11, "j7;djeajex1");
//            s.value(12, "j7;djeajex2");
//            s.value(13, "j7;djeajex3");
//        });
//        log.debug("decoded: [{}]", text.getDataString());
    }
}