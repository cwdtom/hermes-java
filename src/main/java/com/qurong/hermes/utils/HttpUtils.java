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
     * @return 响应结果
     */
    public static String sendGet(String url) throws IOException {
        StringBuilder result = new StringBuilder();
        URL realUrl = new URL(url);
        URLConnection connection = realUrl.openConnection();
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
