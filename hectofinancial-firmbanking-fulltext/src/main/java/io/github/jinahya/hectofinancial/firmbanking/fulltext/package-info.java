/**
 * Defines {@code 전문} for {@code 실시간펌뱅킹} and {@code 실시간펌뱅킹(외회)}.
 *
 * <h2>Creating new instances</h2>
 * {@snippet lang = "java":
 * var instance = FullText.newInstance(FullTextCategory.D, "1000", "500"); // TEST CALL
 * assert instance.getTextCode().equals("1000");
 * assert instance.getTaskCode().equals("500");
 *}
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://develop.sbsvc.online/27/onlineDocList.do">실시간펌뱅킹</a>
 * @see <a href="https://develop.sbsvc.online/31/onlineDocList.do">실시간펌뱅킹(외화)</a>
 */
package io.github.jinahya.hectofinancial.firmbanking.fulltext;