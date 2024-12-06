package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class FullTextSegmentCodec9Test
        extends FullTextSegmentCodec$Test<FullTextSegmentCodec9, Integer> {

    FullTextSegmentCodec9Test() {
        super(FullTextSegmentCodec9.class, Integer.class);
    }

    @DisplayName("encode(1, 10)")
    @Test
    void encode__110() {
        final var decoded = 1;
        final var encoded = newCodecInstance().encode(decoded, 10);
        log.debug("encoded: {}", encoded);
        assertThat(new String(encoded)).isEqualTo("0000000001");
    }

    @DisplayName("decode(\"0000000001\", 10)")
    @Test
    void decode__110() {
        final var encoded = "0000000001".getBytes();
        final var decoded = newCodecInstance().decode(encoded);
        log.debug("decoded: {}", decoded);
        assertThat(decoded).isEqualTo(1);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("공통부/3/은행코드/9(3)")
    @Test
    void 공통부_은행코드() {
        final int decoded = 2; // 002: 산업은행
        final int length = 3;
        final byte[] encoded = newCodecInstance().encode(decoded, length);
        log.debug("decoded: {}, encoded: {}", decoded, encoded);
        assertThat(newCodecInstance().decode(encoded)).isEqualTo(decoded);
    }

    @DisplayName("공통부/7/전문번호/9(6)")
    @Test
    void 공통부_전문번호() {
        final int decoded = ThreadLocalRandom.current().nextInt(950000) + 1;
        final int length = 6;
        final byte[] encoded = newCodecInstance().encode(decoded, length);
        log.debug("decoded: {}, encoded: {}", decoded, encoded);
        assertThat(newCodecInstance().decode(encoded)).isEqualTo(decoded);
    }

    @DisplayName("공통부/8/전송일자/9(8)")
    @Test
    void 공통부_전송일자() {
        final int decoded = Integer.parseInt(
                LocalDate.now().format(DateTimeFormatter.ofPattern("uuuuMMdd"))
        );
        final int length = 8;
        final byte[] encoded = newCodecInstance().encode(decoded, length);
        log.debug("decoded: {}, encoded: {}", decoded, encoded);
        assertThat(newCodecInstance().decode(encoded)).isEqualTo(decoded);
    }

    @DisplayName("공통부/9/전송시간/9(6)")
    @Test
    void 공통부_전송시간() {
        final int decoded = Integer.parseInt(
                LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"))
        );
        final int length = 6;
        final byte[] encoded = newCodecInstance().encode(decoded, length);
        log.debug("decoded: {}, encoded: {}", decoded, encoded);
        assertThat(newCodecInstance().decode(encoded)).isEqualTo(decoded);
    }
}