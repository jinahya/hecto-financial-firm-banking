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
            try {
                return headSection.getDate(FullTextConstants.SEGMENT_INDEX_HEAD_DATE_D);
            } catch (final NumberFormatException nfe) {
                return null;
            }
        }
        @Override void setHeadDate(final FullTextSection headSection, final LocalDate headDate) {
            headSection.date_(FullTextConstants.SEGMENT_INDEX_HEAD_DATE_D, headDate);
        }
        @Override LocalTime getHeadTime(final FullTextSection headSection) {
            try {
                return headSection.getTime(FullTextConstants.SEGMENT_INDEX_HEAD_TIME_D);
            } catch (final NumberFormatException nfe) {
                return null;
            }
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
            try {
                return headSection.getDate(FullTextConstants.SEGMENT_INDEX_HEAD_DATE_F);
            } catch (final NumberFormatException nfe) {
                return null;
            }
        }
        @Override void setHeadDate(final FullTextSection headSection, final LocalDate headDate) {
            headSection.date_(FullTextConstants.SEGMENT_INDEX_HEAD_DATE_F, headDate);
        }
        @Override LocalTime getHeadTime(final FullTextSection headSection) {
            try {
                return headSection.getTime(FullTextConstants.SEGMENT_INDEX_HEAD_TIME_F);
            } catch (final NumberFormatException nfe) {
                return null;
            }
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

    // -------------------------------------------------------------------------------------------------------- headDate

    /**
     * Returns the value of {@code 전송일자} from specified head section.
     *
     * @param headSection the head section.
     * @return the value of {@code 전송일자} from {@code headSection}; {@code null} when failed to parse.
     */
    abstract LocalDate getHeadDate(final FullTextSection headSection);

    abstract void setHeadDate(final FullTextSection headSection, final LocalDate headDate);

    // -------------------------------------------------------------------------------------------------------- headTime
    abstract LocalTime getHeadTime(final FullTextSection headSection);

    abstract void setHeadTime(final FullTextSection headSection, final LocalTime headTime);

    // --------------------------------------------------------------------------------------------- headTextCodeSegment
    String getHeadTextCode(final ByteBuffer data) {
        return headTextCodeSegment.getValue(data);
    }

    void setHeadTextCode(final ByteBuffer data, final String textCode) {
        headTextCodeSegment.setValue(data, textCode);
    }

    // --------------------------------------------------------------------------------------------- headTaskCodeSegment
    String getHeadTaskCode(final ByteBuffer data) {
        return headTaskCodeSegment.getValue(data);
    }

    void setHeadTaskCode(final ByteBuffer data, final String taskCode) {
        headTaskCodeSegment.setValue(data, taskCode);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * {@code 전문구분코드} segment.
     */
    private final FullTextSegment headTextCodeSegment;

    /**
     * {@code 업무구분코드} segment.
     */
    private final FullTextSegment headTaskCodeSegment;
}
