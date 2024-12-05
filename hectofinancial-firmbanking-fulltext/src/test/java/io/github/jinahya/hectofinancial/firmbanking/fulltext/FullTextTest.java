package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class FullTextTest {

    static Stream<Arguments> getCategoryTextCodeAndTaskCodeArgumentsStream() {
        return FullTextTestUtils.getCategoryTextCodeAndTaskCodeArgumentsStream();
    }

    @DisplayName("write(channel)")
    @MethodSource({
            "getCategoryTextCodeAndTaskCodeArgumentsStream"
    })
    @ParameterizedTest
    void write__(final FullTextCategory category, final String textCode, final String taskCode) throws IOException {
        // ------------------------------------------------------------------------------------------------------- given
        final var instance = FullTextTestUtils.loadFullText(category, textCode, taskCode);
        final var baos = new ByteArrayOutputStream();
        // -------------------------------------------------------------------------------------------------------- when
        instance.write(Channels.newChannel(baos));
        // -------------------------------------------------------------------------------------------------------- then
        final var bytes = baos.toByteArray();
        assertThat(bytes).hasSize(instance.getLength() + FullTextUtils.LENGTH_BYTES);
        instance.setData(ByteBuffer.wrap(Arrays.copyOfRange(bytes, FullTextUtils.LENGTH_BYTES, bytes.length)));
        assertThat(instance.getTextCode()).isEqualTo(textCode);
        assertThat(instance.getTaskCode()).isEqualTo(taskCode);
    }

    @DisplayName("write(channel)")
    @MethodSource({
            "getCategoryTextCodeAndTaskCodeArgumentsStream"
    })
    @ParameterizedTest
    void write__Cipher(final FullTextCategory category, final String textCode, final String taskCode) {
        // ------------------------------------------------------------------------------------------------------- given
        final var instance = FullTextTestUtils.loadFullText(category, textCode, taskCode);
        // -------------------------------------------------------------------------------------------------------- when
        FullTextTestUtils.acceptCipherKeyAndParams(c -> k -> p -> {
            instance.setSecurity(FullTextSecurity.newInstance(c, k, p));
            final var baos = new ByteArrayOutputStream();
            try {
                instance.write(Channels.newChannel(baos));
                // ------------------------------------------------------------------------------------------------ then
                final var bytes = baos.toByteArray();
                assertThat(bytes).hasSizeGreaterThanOrEqualTo(instance.getLength() + FullTextUtils.LENGTH_BYTES);
                instance.setData(ByteBuffer.wrap(Arrays.copyOfRange(bytes, FullTextUtils.LENGTH_BYTES, bytes.length)));
                assertThat(instance.getTextCode()).isEqualTo(textCode);
                assertThat(instance.getTaskCode()).isEqualTo(taskCode);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

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