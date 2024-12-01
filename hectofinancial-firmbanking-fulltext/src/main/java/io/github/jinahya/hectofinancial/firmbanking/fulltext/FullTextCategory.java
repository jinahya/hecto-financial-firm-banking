package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.nio.ByteBuffer;

// -----------------------------------------------------------------------------------------------------------------
public enum FullTextCategory {

    /**
     * Value for the {@code 실시간펌뱅킹}
     */
    D(FullTextConstants.LENGTH_HEAD_D, FullTextConstants.LENGTH_BODY_D, FullTextConstants.TEXT_CODE_OFFSET_D,
      FullTextConstants.TEXT_CODE_LENGTH_D, FullTextConstants.TASK_CODE_OFFSET_D, FullTextConstants.TASK_CODE_LENGTH_D),

    /**
     * Value for the {@code 실시간펌뱅킹(외화)}
     */
    F(FullTextConstants.LENGTH_HEAD_F, FullTextConstants.LENGTH_BODY_F, FullTextConstants.TEXT_CODE_OFFSET_F,
      FullTextConstants.TEXT_CODE_LENGTH_F, FullTextConstants.TASK_CODE_OFFSET_F, FullTextConstants.TASK_CODE_LENGTH_F);

    FullTextCategory(final int headLength, final int bodyLength, final int textCodeOffset, final int textCodeLength,
                     final int taskCodeOffset, final int taskCodeLength) {
        this.headLength = headLength;
        this.bodyLength = bodyLength;
        textLength = this.headLength + this.bodyLength;
        textCodeSegment = FullTextSegment.newInstanceOfX(textCodeLength).offset(textCodeOffset);
        taskCodeSegment = FullTextSegment.newInstanceOfX(taskCodeLength).offset(taskCodeOffset);
    }

    String getTextCode(final ByteBuffer buffer) {
        return textCodeSegment.getValue(buffer);
    }

    void setTextCode(final ByteBuffer buffer, final String textCode) {
        textCodeSegment.setValue(buffer, textCode);
    }

    String getTaskCode(final ByteBuffer buffer) {
        return taskCodeSegment.getValue(buffer);
    }

    void setTaskCode(final ByteBuffer buffer, final String taskCode) {
        taskCodeSegment.setValue(buffer, taskCode);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final int headLength;

    private final int bodyLength;

    public final int textLength;

    private final FullTextSegment textCodeSegment;

    private final FullTextSegment taskCodeSegment;
}
