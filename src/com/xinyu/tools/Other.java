package com.xinyu.tools;

import java.util.Random;
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
        }else{
            str = null;
        }
        return str;
    }


    /**
     * 生成随机文件名
     *
     * @return 返回一个7位数的随机字符串
     */
    public static String getRandomFileName() {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 7; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
