package io.github.jinahya.hectofinancial.firmbanking.fulltext;

public final class FullTextConstants {

    // -----------------------------------------------------------------------------------------------------------------
    static final int LENGTH_HEAD_D = 100;

    static final int TEXT_CODE_OFFSET_D = 24;

    static final int TEXT_CODE_LENGTH_D = 4;

    static final int TASK_CODE_OFFSET_D = TEXT_CODE_OFFSET_D + TEXT_CODE_LENGTH_D;

    static final int TASK_CODE_LENGTH_D = 3;

    static final int LENGTH_BODY_D = 200;

    public static final int SEGMENT_INDEX_HEAD_DATE_D = 8;

    public static final int SEGMENT_INDEX_HEAD_TIME_D = 9;

    // -----------------------------------------------------------------------------------------------------------------
    static final int LENGTH_HEAD_F = 100;

    static final int TEXT_CODE_OFFSET_F = 24;

    static final int TEXT_CODE_LENGTH_F = 4;

    static final int TASK_CODE_OFFSET_F = TEXT_CODE_OFFSET_F + TEXT_CODE_LENGTH_F;

    static final int TASK_CODE_LENGTH_F = 3;

    static final int LENGTH_BODY_F = 1900;

    public static final int SEGMENT_INDEX_HEAD_DATE_F = 8;

    public static final int SEGMENT_INDEX_HEAD_TIME_F = 9;

    // -----------------------------------------------------------------------------------------------------------------
    public static final int SECTION_INDEX_HEAD = 1;

    public static final int SECTION_INDEX_BODY = 2;

    // -----------------------------------------------------------------------------------------------------------------
    private FullTextConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
