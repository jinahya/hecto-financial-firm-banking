package io.github.jinahya.hectofinancial.firmbanking.fulltext;

public final class FullTextConstants {

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

    public static final int SECTION_INDEX_BODY = 2;

    // -----------------------------------------------------------------------------------------------------------------
    private FullTextConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
