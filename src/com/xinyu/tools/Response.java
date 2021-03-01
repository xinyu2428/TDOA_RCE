package com.xinyu.tools;


/**
 * 请求完成后返回一个Response对象
 */
public class Response {
    private int code; //状态码
    private String head; //响应头信息
    private String text; //响应包主体

    public Response() {
    }

    public Response(int code, String head, String text) {
        this.code = code;
        this.head = head;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
