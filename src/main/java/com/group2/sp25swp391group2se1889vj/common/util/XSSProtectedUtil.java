package com.group2.sp25swp391group2se1889vj.common.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.owasp.encoder.Encode;
import org.springframework.stereotype.Component;

@Component
public class XSSProtectedUtil {
    /**
     * Đây là hàm mã hóa các ký tự đặc biệt trong chuỗi người dùng nhập vào
     * <p>
     *     Ví dụ: nếu người dùng nhập vào chuỗi sau:
     *     <br>&lt;script&gt;alert('XSS')&lt;/script&gt;
     *     <br>thì hàm này sẽ trả về chuỗi sau:
     *     <br>&amp;lt;script&amp;gt;alert(&amp;#39;XSS&amp;#39;)&amp;lt;/script&amp;gt;
     * </p>
     * @param input chuỗi cần mã hóa
     * @return chuỗi sau khi đã mã hóa
     */
    public String encodeAllHTMLElement(String input) {
        return Encode.forHtml(input);
    }

    /**
     * Đây là hàm loại bỏ những thẻ html không an toàn khỏi đoạn mã người dùng nhập vào
     * <p>
     *     Ví dụ: nếu người dùng nhập vào đoạn mã sau:
     *     <br>&lt;script&gt;alert('XSS')&lt;/script&gt;
     *     <br>thì hàm này sẽ loại bỏ đoạn mã đó và trả về một chuỗi rỗng
     *     <br>ngược lại, nếu người dùng nhập vào đoạn mã sau:
     *     <br>&lt;h1&gt;Hello World&lt;/h1&gt;
     *     <br>thì hàm này sẽ trả về chuỗi không thay đổi
     * </p>
     * @param input chuỗi cần loại bỏ các thẻ html không an toàn
     * @return chuỗi sau khi đã loại bỏ các thẻ html không an toàn
     */
    public String sanitize(String input) {
        return Jsoup.clean(input, Safelist.relaxed());
    }


    public boolean isValid(String input) {
        return Jsoup.isValid(input, Safelist.relaxed());
    }

    public String htmlToPlainText(String html) {
        return Jsoup.parse(html).text();
    }

}
