/**
 * Defines classes and interfaces for {@code 실시간펌뱅킹} and {@code 실시간펌뱅킹(외회)}.
 *
 * <h2>Writing instances</h2>
 * {@snippet lang = "java":
 * var category = FullTextCategory.D;         // 실시간펌뱅킹
 *
 * var instance = FullText.newInstance(category, "1000", "500"); // @highlight
 *
 * assert instance.getTextCode().equals("1000");
 * assert instance.getTaskCode().equals("500");
 * final var now = LocalDateTime.now();
 * instance.acceptHeadSection(s -> {
 *     s.setDate(8, LocalDate.from(now));     // 8 전송일자
 * });
 * instance.setHeadTime(LocalTime.from(now)); // 9 전송시간
 *
 * instance.write(channel); // @highlight
 *}
 * <h2>Reading instances</h2>
 * {@snippet lang = "java":
 * var category = FullTextCategory.F;         // 실시간펌뱅킹(외화)
 *
 * var instance = FullText.readInstance(category, channel, null); // @highlight
 *
 * var textCode = instance.getTextCode();     // 전문구분코드
 * var taskCode = instance.getTaskCode();     // 업무구분코드
 * var headDate = instance.getHeadDate();     // 전송일자
 * var headTime = instance.getHeadTime();     // 전송시간
 *}
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://develop.sbsvc.online/27/onlineDocList.do">실시간펌뱅킹</a>
 * @see <a href="https://develop.sbsvc.online/31/onlineDocList.do">실시간펌뱅킹(외화)</a>
 */
package io.github.jinahya.hectofinancial.firmbanking.fulltext;