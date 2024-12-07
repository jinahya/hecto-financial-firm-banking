package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * An enum for discriminating {@code 실시간펌뱅킹} and {@code 실시간펌뱅킹(외화)}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public enum FullTextCategory {

    /**
     * The value for the {@code 실시간펌뱅킹}
     */
    D(FullTextConstants.SEGMENT_OFFSET_TEXT_CODE_D, FullTextConstants.SEGMENT_LENGTH_TEXT_CODE_D,
      FullTextConstants.SEGMENT_OFFSET_TASK_CODE_D, FullTextConstants.SEGMENT_LENGTH_TASK_CODE_D,
      FullTextConstants.SEGMENT_INDEX_HEAD_DATE_D, FullTextConstants.SEGMENT_INDEX_HEAD_TIME_D),

    /**
     * The value for the {@code 실시간펌뱅킹(외화)}
     */
    F(FullTextConstants.SEGMENT_OFFSET_TEXT_CODE_F, FullTextConstants.SEGMENT_LENGTH_TEXT_CODE_F,
      FullTextConstants.SEGMENT_OFFSET_TASK_CODE_F, FullTextConstants.SEGMENT_LENGTH_TASK_CODE_F,
      FullTextConstants.SEGMENT_INDEX_HEAD_DATE_F, FullTextConstants.SEGMENT_INDEX_HEAD_TIME_F);

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    FullTextCategory(final int headTextCodeOffset, final int headTextCodeLength,
                     final int headTaskCodeOffset, final int headTaskCodeLength,
                     final int headDateSegmentIndex, final int headTimeSegmentIndex) {
        headTextCodeSegment = FullTextSegment.newInstanceOfX(headTextCodeOffset, headTextCodeLength, "headTextCode");
        headTaskCodeSegment = FullTextSegment.newInstanceOfX(headTaskCodeOffset, headTaskCodeLength, "headTaskCode");
        this.headDateSegmentIndex = headDateSegmentIndex;
        this.headTimeSegmentIndex = headTimeSegmentIndex;
    }

    // -------------------------------------------------------------------------------------------------------- headDate
    LocalDate getHeadDate(final FullTextSection headSection) {
        return headSection.getDate(headDateSegmentIndex);
    }

    void setHeadDate(final FullTextSection headSection, final LocalDate headDate) {
        headSection.setDate(headDateSegmentIndex, headDate);
    }

    LocalTime getHeadTime(final FullTextSection headSection) {
        return headSection.getTime(headTimeSegmentIndex);
    }

    void setHeadTime(final FullTextSection headSection, final LocalTime headTime) {
        headSection.setTime(headTimeSegmentIndex, headTime);
    } // @formatter:on

    // --------------------------------------------------------------------------------------------- headTextCodeSegment
    FullTextSegment getHeadTextCodeSegment() {
        return headTextCodeSegment;
    }

    String getHeadTextCode(final ByteBuffer headData) {
        return headTextCodeSegment.getValue(headData);
    }

    void setHeadTextCode(final ByteBuffer headData, final String textCode) {
        headTextCodeSegment.setValue(headData, textCode);
    }

    // --------------------------------------------------------------------------------------------- headTaskCodeSegment
    FullTextSegment getHeadTaskCodeSegment() {
        return headTaskCodeSegment;
    }

    String getHeadTaskCode(final ByteBuffer headData) {
        return headTaskCodeSegment.getValue(headData);
    }

    void setHeadTaskCode(final ByteBuffer headData, final String taskCode) {
        headTaskCodeSegment.setValue(headData, taskCode);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * {@code 전문구분코드} segment.
     */
    final FullTextSegment headTextCodeSegment;

    /**
     * {@code 업무구분코드} segment.
     */
    final FullTextSegment headTaskCodeSegment;

    final int headDateSegmentIndex;

    final int headTimeSegmentIndex;
}
