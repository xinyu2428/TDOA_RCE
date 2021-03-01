package com.xinyu.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Other {
    /**
     * 获取通达版本
     * poc: http://127.0.0.1/inc/expired.php
     *
     * @param url
     * @return
     */
    public static String getVersion(String url) {
        String version = null;
        url += "/inc/expired.php";
        Response response = Request.get(url);
        if (response != null) {
            version = dataCleaning(response.getText(), Pattern.compile("Office Anywhere ([\\d|.]+)"));
        }
        return version;
    }


    /**
     * 根据正则匹配相应的字符串并返回
     *
     * @param str
     * @param pattern
     * @return
     */
    public static String dataCleaning(String str, Pattern pattern) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            str = matcher.group(1);
        }
        return str;
    }
}
