package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalTime;

// -----------------------------------------------------------------------------------------------------------------
public enum FullTextCategory {

    /**
     * The value for the {@code 실시간펌뱅킹}
     */
    D(FullTextConstants.LENGTH_HEAD_D, FullTextConstants.LENGTH_BODY_D,
      _Range.from(FullTextConstants.TEXT_CODE_OFFSET_D, FullTextConstants.TEXT_CODE_LENGTH_D),
      _Range.from(FullTextConstants.TASK_CODE_OFFSET_D, FullTextConstants.TASK_CODE_LENGTH_D)) { // @formatter:off
        @Override
        LocalDate getHeadDate(final FullTextSection section) {
            return section.date_(FullTextConstants.SEGMENT_INDEX_HEAD_DATE_D);
        }
        @Override
        void setHeadDate(final FullTextSection section, final LocalDate headDate) {
            section.date_(FullTextConstants.SEGMENT_INDEX_HEAD_DATE_D, headDate);
        }
        @Override
        LocalTime getHeadTime(final FullTextSection section) {
            return section.time_(FullTextConstants.SEGMENT_INDEX_HEAD_TIME_D);
        }
        @Override
        void setHeadTime(final FullTextSection section, final LocalTime headTime) {
            section.time_(FullTextConstants.SEGMENT_INDEX_HEAD_TIME_D, headTime);
        } // @formatter:on
    },

    /**
     * The value for the {@code 실시간펌뱅킹(외화)}
     */
    F(FullTextConstants.LENGTH_HEAD_F, FullTextConstants.LENGTH_BODY_F,
      _Range.from(FullTextConstants.TEXT_CODE_OFFSET_F, FullTextConstants.TEXT_CODE_LENGTH_F),
      _Range.from(FullTextConstants.TASK_CODE_OFFSET_F, FullTextConstants.TASK_CODE_LENGTH_F)) { // @formatter:off
        @Override
        LocalDate getHeadDate(final FullTextSection section) {
            return section.date_(FullTextConstants.SEGMENT_INDEX_HEAD_DATE_F);
        }
        @Override
        void setHeadDate(final FullTextSection section, final LocalDate headDate) {
            section.date_(FullTextConstants.SEGMENT_INDEX_HEAD_DATE_F, headDate);
        }
        @Override
        LocalTime getHeadTime(final FullTextSection section) {
            return section.time_(FullTextConstants.SEGMENT_INDEX_HEAD_TIME_F);
        }
        @Override
        void setHeadTime(final FullTextSection section, final LocalTime headTime) {
            section.time_(FullTextConstants.SEGMENT_INDEX_HEAD_TIME_F, headTime);
        } // @formatter:on
    };

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    FullTextCategory(final int headLength, final int bodyLength, final _Range textCodeRange,
                     final _Range taskCodeRange) {
        this.headLength = headLength;
        this.bodyLength = bodyLength;
        textLength = this.headLength + this.bodyLength;
        textCodeSegment = FullTextSegment.newInstanceOfX(textCodeRange.length()).offset(textCodeRange.offset());
        taskCodeSegment = FullTextSegment.newInstanceOfX(taskCodeRange.length()).offset(taskCodeRange.offset());
    }

    // -----------------------------------------------------------------------------------------------------------------
    abstract LocalDate getHeadDate(final FullTextSection section);

    abstract void setHeadDate(final FullTextSection section, final LocalDate headDate);

    abstract LocalTime getHeadTime(final FullTextSection section);

    abstract void setHeadTime(final FullTextSection section, final LocalTime headTime);

    // ------------------------------------------------------------------------------------------------- textCodeSegment
    String getTextCode(final ByteBuffer buffer) {
        return textCodeSegment.getValue(buffer);
    }

    void setTextCode(final ByteBuffer buffer, final String textCode) {
        textCodeSegment.setValue(buffer, textCode);
    }

    // ------------------------------------------------------------------------------------------------- taskCodeSegment
    String getTaskCode(final ByteBuffer buffer) {
        return taskCodeSegment.getValue(buffer);
    }

    void setTaskCode(final ByteBuffer buffer, final String taskCode) {
        taskCodeSegment.setValue(buffer, taskCode);
    }

    // -----------------------------------------------------------------------------------------------------------------
    final int headLength;

    final int bodyLength;

    final int textLength;

    private final FullTextSegment textCodeSegment;

    private final FullTextSegment taskCodeSegment;
}
