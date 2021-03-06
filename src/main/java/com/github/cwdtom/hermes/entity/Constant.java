package com.github.cwdtom.hermes.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目静态量
 *
 * @author chenweidong
 * @since 1.0.0
 */
public class Constant {
    /**
     * 注册中心bean name
     */
    public static final String CENTERS_BEAN_NAME = "centers";
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

    public static final Map<String, Class<?>> CAST_TYPE_MAP = new HashMap<String, Class<?>>(){{
        put("int", Integer.class);
        put("float", Float.class);
        put("long", Long.class);
        put("double", Double.class);
        put("short", Short.class);
    }};
}