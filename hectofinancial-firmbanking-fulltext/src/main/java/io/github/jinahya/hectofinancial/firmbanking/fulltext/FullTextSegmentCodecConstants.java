package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.time.format.DateTimeFormatter;

public final class FullTextSegmentCodecConstants {

    // -----------------------------------------------------------------------------------------------------------------
    private static final String PATTERN_DATE = "uuuuMMdd";

    static final int LENGTH_DATE = PATTERN_DATE.length();

    static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern(PATTERN_DATE);

    // -----------------------------------------------------------------------------------------------------------------
    private static final String PATTERN_TIME = "HHmmss";

    private static final int LENGTH_TIME = PATTERN_TIME.length();

    public static final DateTimeFormatter FORMATTER_TIME = DateTimeFormatter.ofPattern(PATTERN_TIME);

    // -----------------------------------------------------------------------------------------------------------------
    private FullTextSegmentCodecConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
