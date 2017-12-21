package com.github.cwdtom.hermes.utils;

import java.util.Base64;

/**
 * 基础加密
 *
 * @author chenweidong
 * @since 1.0.0
 */
public class CoderUtils {
    /**
     * 16进制映射
     */
    private final static char[] HEX_ARRAY = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * BASE64解密
     *
     * @param key 密钥
     * @return 密钥解密结果
     */
    static byte[] decryptBASE64(String key) {
        return Base64.getDecoder().decode(key);
    }

    /**
     * bytes to hex string
     *
     * @param bytes 二进制数据
     * @return 16进制字符串
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * hex string to bytes
     *
     * @param s 16进制字符串
     * @return 二进制数据
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        int period = 2;
        for (int i = 0; i < len; i += period) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}  