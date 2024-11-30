package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class FullTextSegmentCodec_OfX_Test
        extends FullTextSegmentCodec__Test<FullTextSegmentCodecOfX, String> {

    FullTextSegmentCodec_OfX_Test() {
        super(FullTextSegmentCodecOfX.class, String.class);
    }

    @Test
    void encode__110() {
        final var decoded = "1";
        final var encoded = newCodecInstance().encode(decoded, 10);
        log.debug("encoded: {}", encoded);
        assertThat(new String(encoded)).isEqualTo("1         ");
    }

    @Test
    void decode__110() {
        final var encoded = "1         ".getBytes();
        final var decoded = newCodecInstance().decode(encoded, 10);
        log.debug("decoded: {}", decoded);
        assertThat(decoded).isEqualTo("1");
    }

    @Test
    void encode__홍길동10() {
        final var decoded = "홍길동";
        final var encoded = newCodecInstance().encode(decoded, 10);
        log.debug("encoded: {}", encoded);
        assertThat(new String(encoded, FullTextSegmentCodecOfX.CHARSET)).isEqualTo("홍길동    ");
    }

    @Test
    void decode__홍길동10() {
        final var encoded = "1         ".getBytes();
        final var decoded = newCodecInstance().decode(encoded, 10);
        log.debug("decoded: {}", decoded);
        assertThat(decoded).isEqualTo("1");
    }

    @DisplayName("공통부/1/식별코드/X(9)")
    @Test
    void 공통부_식별코드() {
        final String decoded = "SETTLEBNK"; // mind the spelling
        final int length = 9;
        final byte[] encoded = newCodecInstance().encode(decoded, length);
        log.debug("encoded: {}", encoded);
        assertThat(encoded).isEqualTo(decoded.getBytes(FullTextSegmentCodecOfX.CHARSET));
        assertThat(newCodecInstance().decode(encoded, length)).isEqualTo(decoded);
    }

    @DisplayName("공통부/3/은행코드/9(3)")
    @Test
    void 공통부_은행코드() {
        final String decoded = "002"; // 산업은행
        final int length = 3;
        final byte[] encoded = newCodecInstance().encode(decoded, length);
        log.debug("decoded: {}, encoded: {}", decoded, encoded);
        assertThat(newCodecInstance().decode(encoded, length)).isEqualTo(decoded);
    }

    @DisplayName("공통부/4/전문구분코드/X(3)")
    @Test
    void 공통부_전문구분코드() {
        final String decoded = "1000";
        final int length = 4;
        final byte[] encoded = newCodecInstance().encode(decoded, length);
        log.debug("encoded: {}", encoded);
        assertThat(encoded).isEqualTo(decoded.getBytes(FullTextSegmentCodecOfX.CHARSET));
        assertThat(newCodecInstance().decode(encoded, length)).isEqualTo(decoded);
    }

    @DisplayName("공통부/5/업무구분코드/X(3)")
    @Test
    void 공통부_업무구분코드() {
        final String decoded = "100";
        final int length = 3;
        final byte[] encoded = newCodecInstance().encode(decoded, length);
        log.debug("encoded: {}", encoded);
        assertThat(encoded).isEqualTo(decoded.getBytes(FullTextSegmentCodecOfX.CHARSET));
        assertThat(newCodecInstance().decode(encoded, length)).isEqualTo(decoded);
    }

    @DisplayName("공통부/10/응답코드/X(4)")
    @Test
    void 공통부_응답코드() {
        final String decoded = "0000";
        final int length = 4;
        final byte[] encoded = newCodecInstance().encode(decoded, length);
        log.debug("encoded: {}", encoded);
        assertThat(encoded).isEqualTo(decoded.getBytes(FullTextSegmentCodecOfX.CHARSET));
        assertThat(newCodecInstance().decode(encoded, length)).isEqualTo(decoded);
    }
}