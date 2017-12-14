package com.qurong.hermes.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * HTTP工具
 *
 * @author tom
 */
public class HttpUtils {
    /**
     * 发送GET请求
     *
     * @param url   目标URL
     * @param param 参数，格式：name1=value1&name2=value2
     * @return 响应结果
     */
    public static String sendGet(String url, String param) throws IOException {
        StringBuilder result = new StringBuilder();
        String urlNameString = url + "?" + param;
        URL realUrl = new URL(urlNameString);
        // 打开URL连接
        URLConnection connection = realUrl.openConnection();
        connection.setDoOutput(false);
        // 打开连接
        connection.connect();
        try(BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        }
        return result.toString();
    }
}
