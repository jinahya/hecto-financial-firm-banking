package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class FullText_D1000_500_Test {

    private static final String TEXT_CODE = "1000";

    private static final String TASK_CODE = "100";

    @Test
    void __() {
        // ------------------------------------------------------------------------------------------------------- given
        final var instance = FullText_TestUtils.loadFullText(FullTextCategory.D, TEXT_CODE, TASK_CODE);
        final var textNumber = ThreadLocalRandom.current().nextInt(950000) + 1;
        // -------------------------------------------------------------------------------------------------------- when
        instance.acceptHeadSection(s -> {
            s.setValue(2, "jt3oc;tsmv"); // 2 업체번호 X(12)
            s.setValue(3, "002");        // 3 은행코드 9(3)
            s.setValue(6, 1);            // 6 송신회수 9(1)
            s.setValue(7, textNumber);   // 7 전문번호 9(6)
            s.setDate(8, LocalDate.now());
            s.setTime(9, LocalTime.now());
        });
        // -------------------------------------------------------------------------------------------------------- then
        assertThat(instance.getTextCode()).isEqualTo(TEXT_CODE);
        assertThat(instance.getTaskCode()).isEqualTo(TASK_CODE);
        log.debug("head: [{}]", instance.getHeadDataString());
        log.debug("body: [{}]", instance.getBodyDataString());
        final var string = instance.getDataString();
        log.debug("data: [{}]", string);
        assertThat(string).isNotNull().hasSize(instance.getLength());
    }
}