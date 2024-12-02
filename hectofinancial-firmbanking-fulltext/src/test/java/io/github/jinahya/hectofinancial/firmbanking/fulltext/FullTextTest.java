package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class FullTextTest {

    @Test
    void getHeadDateTime__() {
        // ------------------------------------------------------------------------------------------------------- given
        final var text = Mockito.spy(FullText.class);
        final var headDateTime = LocalDateTime.now();
        doAnswer(i -> LocalDate.from(headDateTime)).when(text).getHeadDate();
        doAnswer(i -> LocalTime.from(headDateTime)).when(text).getHeadTime();
        // -------------------------------------------------------------------------------------------------------- when
        final var result = text.getHeadDateTime();
        // -------------------------------------------------------------------------------------------------------- then
        verify(text, times(1)).getHeadDate();
        verify(text, times(1)).getHeadTime();
        assertThat(result).isEqualTo(headDateTime);
    }

    @Test
    void setHeadDateTime__() {
        // ------------------------------------------------------------------------------------------------------- given
        final var text = Mockito.spy(FullText.class);
        final var headDateTime = LocalDateTime.now();
        doNothing().when(text).setHeadDate(Mockito.notNull());
        doNothing().when(text).setHeadTime(Mockito.notNull());
        // -------------------------------------------------------------------------------------------------------- when
        text.setHeadDateTime(headDateTime);
        // -------------------------------------------------------------------------------------------------------- then
        verify(text, times(1)).setHeadDate(LocalDate.from(headDateTime));
        verify(text, times(1)).setHeadTime(LocalTime.from(headDateTime));
    }
}