package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class FullText_Test {

    static Stream<Arguments> getCategoryTextCodeAndTaskCodeArgumentsStream() {
        return FullText_TestUtils.getCategoryTextCodeAndTaskCodeArgumentsStream();
    }

    private static Stream<Arguments> getTextCategoryTextCodeAndTaskCodeArgumentsStream() {
        return FullTextSection_NewBodyInstance_Test.getTextCategoryTextCodeAndTaskCodeArgumentsStream();
    }

    private static Stream<FullText> getFullTextStream() {
        return FullTextSection_NewBodyInstance_Test.getTextCategoryTextCodeAndTaskCodeArgumentsStream().map(a -> {
            final var got = a.get();
            final var category = (FullTextCategory) got[0];
            final var textCode = (String) got[1];
            final var taskCode = (String) got[2];
            return FullText.newInstance(category, textCode, taskCode);
        });
    }

    @DisplayName("write(channel)")
    @MethodSource({
            "getCategoryTextCodeAndTaskCodeArgumentsStream"
    })
    @ParameterizedTest
    void write__(final FullTextCategory category, final String textCode, final String taskCode) throws IOException {
        // ------------------------------------------------------------------------------------------------------- given
        final var instance = FullText_TestUtils.loadFullText(category, textCode, taskCode);
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
        final var instance = FullText_TestUtils.loadFullText(category, textCode, taskCode);
        // -------------------------------------------------------------------------------------------------------- when
        FullTextCrypto_TestUtils.acceptCipherKeyAndParams(c -> k -> p -> {
            instance.setCrypto(FullTextCrypto.newInstance(c, k, p));
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

    @DisplayName("applySection(index, function")
    @Nested
    class ApplySectionTest {

        private static Stream<FullText> getFullTextStream() {
            return FullText_Test.getFullTextStream();
        }

        @MethodSource({
                "getFullTextStream"
        })
        @ParameterizedTest
        void applySection__(final FullText text) {
            text.applySection(FullTextConstants.SECTION_INDEX_HEAD, s -> {
                assertThat(s).isNotNull();
                return null;
            });
            text.applySection(FullTextConstants.SECTION_INDEX_BODY, s -> {
                assertThat(s).isNotNull();
                return null;
            });
        }
    }

    @DisplayName("acceptSection(index, function")
    @Nested
    class AcceptSectionTest {

        private static Stream<FullText> getFullTextStream() {
            return FullText_Test.getFullTextStream();
        }

        @MethodSource({
                "getFullTextStream"
        })
        @ParameterizedTest
        void applySection__(final FullText text) {
            text.acceptSection(FullTextConstants.SECTION_INDEX_HEAD, s -> {
                assertThat(s).isNotNull();
            });
            text.acceptSection(FullTextConstants.SECTION_INDEX_BODY, s -> {
                assertThat(s).isNotNull();
            });
        }
    }

    @DisplayName("applyHeadSection(function")
    @Nested
    class ApplyHeadSectionTest {

        private static Stream<FullText> getFullTextStream() {
            return FullText_Test.getFullTextStream();
        }

        @MethodSource({
                "getFullTextStream"
        })
        @ParameterizedTest
        void applySection__(final FullText text) {
            text.applyHeadSection(s -> {
                assertThat(s).isNotNull();
                return null;
            });
            text.applyHeadSection(s -> {
                assertThat(s).isNotNull();
                return null;
            });
        }
    }

    @DisplayName("applyBodySection(function")
    @Nested
    class ApplyBodySectionTest {

        private static Stream<FullText> getFullTextStream() {
            return FullText_Test.getFullTextStream();
        }

        @MethodSource({
                "getFullTextStream"
        })
        @ParameterizedTest
        void applySection__(final FullText text) {
            text.applyBodySection(s -> {
                assertThat(s).isNotNull();
                return null;
            });
            text.applyBodySection(s -> {
                assertThat(s).isNotNull();
                return null;
            });
        }
    }

    @DisplayName("acceptHeadSection(function")
    @Nested
    class AcceptHeadSectionTest {

        private static Stream<FullText> getFullTextStream() {
            return FullText_Test.getFullTextStream();
        }

        @MethodSource({
                "getFullTextStream"
        })
        @ParameterizedTest
        void acceptSection__(final FullText text) {
            text.acceptHeadSection(s -> {
                assertThat(s).isNotNull();
            });
            text.acceptHeadSection(s -> {
                assertThat(s).isNotNull();
            });
        }
    }

    @DisplayName("acceptBodySection(function")
    @Nested
    class AcceptBodySectionTest {

        private static Stream<FullText> getFullTextStream() {
            return FullText_Test.getFullTextStream();
        }

        @MethodSource({
                "getFullTextStream"
        })
        @ParameterizedTest
        void acceptSection__(final FullText text) {
            text.acceptBodySection(s -> {
                assertThat(s).isNotNull();
            });
            text.acceptBodySection(s -> {
                assertThat(s).isNotNull();
            });
        }
    }

    @DisplayName("getHeadDateTime")
    @Nested
    class GetHeadDateTimeTest {

        @DisplayName("()null")
        @Test
        void __Null() {
            // ------------------------------------------------------------------------------------------------------- given
            final var text = spy(FullText.class);
            doReturn(null).when(text).getHeadDate();
            doReturn(null).when(text).getHeadTime();
            // -------------------------------------------------------------------------------------------------------- when
            final var result = text.getHeadDateTime();
            // -------------------------------------------------------------------------------------------------------- then
            assertThat(result).isNull();
            ;
        }

        @DisplayName("()!null")
        @Test
        void __NotNull() {
            // ------------------------------------------------------------------------------------------------------- given
            final var text = spy(FullText.class);
            final var headDateTime = LocalDateTime.now();
            final var headDate = LocalDate.from(headDateTime);
            final var headTime = LocalTime.from(headDateTime);
            doAnswer(i -> headDate).when(text).getHeadDate();
            doAnswer(i -> headTime).when(text).getHeadTime();
            // -------------------------------------------------------------------------------------------------------- when
            final var result = text.getHeadDateTime();
            // -------------------------------------------------------------------------------------------------------- then
            verify(text, times(1)).getHeadDate();
            verify(text, times(1)).getHeadTime();
            assertThat(result).isEqualTo(headDateTime);
        }
    }

    /**
     * A nested test class for testing {@link FullText#setHeadDateTime(LocalDateTime)} method.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     */
    @DisplayName("setHeadDateTime")
    @Nested
    class SetHeadDateTimeTest {

        @DisplayName("(null)")
        @Test
        void __Null() {
            // ------------------------------------------------------------------------------------------------------- given
            final var text = spy(FullText.class);
            doNothing().when(text).setHeadDate(isNull());
            doNothing().when(text).setHeadTime(isNull());
            // -------------------------------------------------------------------------------------------------------- when
            assertThatCode(() -> {
                text.setHeadDateTime(null);
            }).doesNotThrowAnyException();
            // -------------------------------------------------------------------------------------------------------- then
            verify(text, times(1)).setHeadDate(null);
            verify(text, times(1)).setHeadTime(null);
        }

        @DisplayName("(!null)")
        @Test
        void __NotNull() {
            // ------------------------------------------------------------------------------------------------------- given
            final var text = spy(FullText.class);
            final var headDateTime = LocalDateTime.now();
            doNothing().when(text).setHeadDate(notNull());
            doNothing().when(text).setHeadTime(notNull());
            // -------------------------------------------------------------------------------------------------------- when
            assertThatCode(() -> {
                text.setHeadDateTime(headDateTime);
            })
                    .as("invoking setHeadDateTime with %1$s", headDateTime)
                    .doesNotThrowAnyException();
            // -------------------------------------------------------------------------------------------------------- then
            verify(text, times(1)).setHeadDate(LocalDate.from(headDateTime));
            verify(text, times(1)).setHeadTime(LocalTime.from(headDateTime));
        }
    }

    /**
     * A nested test class for testing {@link FullText#setHeadDateTimeWithNow()} method.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     */
    @DisplayName("setHeadDateTimeWithNow")
    @Nested
    class SetHeadDateTimeAsNowTest {

        @DisplayName("should invoke setLocalDateTime with now()")
        @Test
        void __() {
            // --------------------------------------------------------------------------------------------------- given
            final var text = spy(FullText.class);
            doNothing().when(text).setHeadDateTime(notNull());
            final var now = LocalDateTime.now();
            // ---------------------------------------------------------------------------------------------------- when
            try (var mockStatic = mockStatic(LocalDateTime.class)) {
                mockStatic.when(LocalDateTime::now).thenReturn(now);
                assertThatCode(text::setHeadDateTimeWithNow)
                        .as("invocation of setHeadDateTimeWithNow")
                        .doesNotThrowAnyException();
                // ------------------------------------------------------------------------------------------------ then
                verify(text, times(1)).setHeadDateTime(now);
            }
        }
    }

    @DisplayName("getHeadDataString()")
    @MethodSource({
            "getFullTextStream"
    })
    @ParameterizedTest
    void getHeadString_NotBlank_(final FullText text) {
        assertThat(text.getHeadDataString()).isNotBlank();
    }

    @DisplayName("getBodyDataString()")
    @MethodSource({
            "getFullTextStream"
    })
    @ParameterizedTest
    void getBodyString_NotBlank_(final FullText text) {
        assertThat(text.getBodyDataString()).isNotNull();
    }

    @DisplayName("getDataString()")
    @MethodSource({
            "getFullTextStream"
    })
    @ParameterizedTest
    void getDataString_NotBlank_(final FullText text) {
        assertThat(text.getDataString()).isNotBlank();
    }
}