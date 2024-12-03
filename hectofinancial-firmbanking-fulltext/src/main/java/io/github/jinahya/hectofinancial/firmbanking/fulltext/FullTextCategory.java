package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalTime;

public enum FullTextCategory {

    /**
     * The value for the {@code 실시간펌뱅킹}
     */
    D(FullTextConstants.SEGMENT_OFFSET_TEXT_CODE_D, FullTextConstants.SEGMENT_LENGTH_TEXT_CODE_D,
      FullTextConstants.SEGMENT_OFFSET_TASK_CODE_D, FullTextConstants.SEGMENT_LENGTH_TASK_CODE_D) { // @formatter:off
        @Override LocalDate getHeadDate(final FullTextSection headSection) {
            return headSection.date_(FullTextConstants.SEGMENT_INDEX_HEAD_DATE_D);
        }
        @Override void setHeadDate(final FullTextSection headSection, final LocalDate headDate) {
            headSection.date_(FullTextConstants.SEGMENT_INDEX_HEAD_DATE_D, headDate);
        }
        @Override LocalTime getHeadTime(final FullTextSection headSection) {
            return headSection.time_(FullTextConstants.SEGMENT_INDEX_HEAD_TIME_D);
        }
        @Override void setHeadTime(final FullTextSection headSection, final LocalTime headTime) {
            headSection.time_(FullTextConstants.SEGMENT_INDEX_HEAD_TIME_D, headTime);
        } // @formatter:on
    },

    /**
     * The value for the {@code 실시간펌뱅킹(외화)}
     */
    F(FullTextConstants.SEGMENT_OFFSET_TEXT_CODE_F, FullTextConstants.SEGMENT_LENGTH_TEXT_CODE_F,
      FullTextConstants.SEGMENT_OFFSET_TASK_CODE_F, FullTextConstants.SEGMENT_LENGTH_TASK_CODE_F) { // @formatter:off
        @Override LocalDate getHeadDate(final FullTextSection headSection) {
            return headSection.date_(FullTextConstants.SEGMENT_INDEX_HEAD_DATE_F);
        }
        @Override void setHeadDate(final FullTextSection headSection, final LocalDate headDate) {
            headSection.date_(FullTextConstants.SEGMENT_INDEX_HEAD_DATE_F, headDate);
        }
        @Override LocalTime getHeadTime(final FullTextSection headSection) {
            return headSection.time_(FullTextConstants.SEGMENT_INDEX_HEAD_TIME_F);
        }
        @Override void setHeadTime(final FullTextSection headSection, final LocalTime headTime) {
            headSection.time_(FullTextConstants.SEGMENT_INDEX_HEAD_TIME_F, headTime);
        } // @formatter:on
    };

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    FullTextCategory(final int headTextCodeOffset, final int headTextCodeLength, final int headTaskCodeOffset,
                     final int headTaskCodeLength) {
        headTextCodeSegment = FullTextSegment.newInstanceOfX(headTextCodeOffset, headTextCodeLength, "headTextCode");
        headTaskCodeSegment = FullTextSegment.newInstanceOfX(headTaskCodeOffset, headTaskCodeLength, "headTaskCode");
    }

    // -----------------------------------------------------------------------------------------------------------------
    abstract LocalDate getHeadDate(final FullTextSection headSection);

    abstract void setHeadDate(final FullTextSection headSection, final LocalDate headDate);

    abstract LocalTime getHeadTime(final FullTextSection headSection);

    abstract void setHeadTime(final FullTextSection headSection, final LocalTime headTime);

    // ------------------------------------------------------------------------------------------------- textCodeSegment
    String getHeadTextCode(final ByteBuffer buffer) {
        return headTextCodeSegment.getValue(buffer);
    }

    void setHeadTextCode(final ByteBuffer buffer, final String textCode) {
        headTextCodeSegment.setValue(buffer, textCode);
    }

    // ------------------------------------------------------------------------------------------------- taskCodeSegment
    String getHeadTaskCode(final ByteBuffer buffer) {
        return headTaskCodeSegment.getValue(buffer);
    }

    void setHeadTaskCode(final ByteBuffer buffer, final String taskCode) {
        headTaskCodeSegment.setValue(buffer, taskCode);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final FullTextSegment headTextCodeSegment;

    private final FullTextSegment headTaskCodeSegment;
}
