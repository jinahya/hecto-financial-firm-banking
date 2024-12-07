package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;

/**
 * Constants for {@link FullText}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class FullTextConstants {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The default charset name for the {@link FullText}.
     */

    static final String CHARSET_NAME = "euc-kr";

    /**
     * The default charset for the {@link FullText}.
     */
    static final Charset CHARSET = Charset.forName(CHARSET_NAME);

    // --------------------------------------------------------------------------------------------------------------- D
    static final int SEGMENT_OFFSET_TEXT_CODE_D = 24;

    static final int SEGMENT_LENGTH_TEXT_CODE_D = 4;

    static final int SEGMENT_OFFSET_TASK_CODE_D = SEGMENT_OFFSET_TEXT_CODE_D + SEGMENT_LENGTH_TEXT_CODE_D;

    static final int SEGMENT_LENGTH_TASK_CODE_D = 3;

    static final int SEGMENT_INDEX_HEAD_DATE_D = 8;

    static final int SEGMENT_INDEX_HEAD_TIME_D = 9;

    // --------------------------------------------------------------------------------------------------------------- F
    static final int SEGMENT_OFFSET_TEXT_CODE_F = 24;

    static final int SEGMENT_LENGTH_TEXT_CODE_F = 4;

    static final int SEGMENT_OFFSET_TASK_CODE_F = SEGMENT_OFFSET_TEXT_CODE_F + SEGMENT_LENGTH_TEXT_CODE_F;

    static final int SEGMENT_LENGTH_TASK_CODE_F = 3;

    static final int SEGMENT_INDEX_HEAD_DATE_F = 8;

    static final int SEGMENT_INDEX_HEAD_TIME_F = 9;

    // -----------------------------------------------------------------------------------------------------------------
    public static final int SECTION_INDEX_HEAD = 1;

    public static final int SECTION_INDEX_BODY = SECTION_INDEX_HEAD + 1;

    // -----------------------------------------------------------------------------------------------------------------
    private static final String DATE_TIME_PATTERN_DATE = "uuuuMMdd";

    static final DateTimeFormatter DATE_TIME_FORMATTER_DATE = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_DATE);

    private static final String DATE_TIME_PATTERN_TIME = "HHmmss";

    static final DateTimeFormatter DATE_TIME_FORMATTER_TIME = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_TIME);

    // -----------------------------------------------------------------------------------------------------------------
    private FullTextConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
