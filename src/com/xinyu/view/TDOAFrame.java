package com.xinyu.view;

import javax.swing.*;
import java.awt.*;

public class TDOAFrame extends JFrame {

    public TDOAFrame(String title) {
        this.setTitle(title);
        this.setBounds(300, 300, 720, 520);
        this.setResizable(false); //窗体大小固定
        this.setLayout(null); //绝对定位

        JPanel p1 = new JPanel(new GridLayout(2, 1, 0, 2));
        p1.setSize(720, 100);
        p1.setBackground(new Color(210, 70, 170));
        JPanel p2 = new JPanel(new GridLayout(1, 1));
        p2.setSize(720, 350);
        p2.setLocation(0, 100);
        TextArea textArea = new TextArea();
        textArea.setEditable(false); //设置其状态为不可编辑
        p2.add(textArea);

        JPanel p5 = new JPanel(new GridLayout(1, 1));
        p5.setSize(720, 200);
        p5.setLocation(0, 370);
        JLabel instructions = new JLabel("   本工具仅供安全测试人员运用于授权测试, 禁止用于未授权测试, 违者责任自负.");
        p5.add(instructions);


        JPanel p3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        JPanel p4 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        p1.add(p3);
        p1.add(p4);


        JLabel jLabel = new JLabel("目标地址: ");
        JTextField field_url = new JTextField("http://192.168.238.141", 45);
        JButton button = new JButton("获取Cookie");
        p3.add(jLabel);
        p3.add(field_url);
        p3.add(button);


        JLabel jLabel2 = new JLabel("Cookie: ");
        JTextField field_cookie = new JTextField("", 35);
        JComboBox jcomboBox = new JComboBox();
        jcomboBox.addItem("文件包含GetShell");
        jcomboBox.addItem("后台上传GetShell");
        jcomboBox.addItem("文件删除GetShell");
        jcomboBox.addItem("V11.7_GetShell");
        JButton button2 = new JButton("一键利用");
        p4.add(jLabel2);
        p4.add(field_cookie);
        p4.add(jcomboBox);
        p4.add(button2);

        this.add(p1);
        this.add(p2);
        this.add(p5);

        button.addActionListener(new GetCookieActionListener(field_url, field_cookie, textArea)); //给"获取Cookie"按钮绑定事件
        button2.addActionListener(new GetShellActionListener(field_url, field_cookie, jcomboBox, textArea)); //给"一键利用"按钮绑定事件

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //窗体关闭事件

        this.setVisible(true); //窗体可见
    }

}
