package com.qurong.hermes.entity;

import java.util.Map;

/**
 * 项目静态量
 *
 * @author chenweidong
 */
public class Constant {
    /**
     * 注册中心列表
     */
    public static Center[] centers;
    /**
     * 方法路径映射
     */
    public static Map<String, ServerMethod> methodMap;
    /**
     * 状态码字段
     */
    public static final String CODE = "Code";
    /**
     * 数据字段
     */
    public static final String DATA = "Data";
    /**
     * sessionId长度
     */
    public static final Integer SESSION_ID_LENGTH = 10;
    /**
     * RSA加密保留长度
     */
    public static final Integer RSA_RESERVED_LENGTH = 11;
}