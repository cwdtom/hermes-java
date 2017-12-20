package com.qurong.hermes.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * 方法调用对象
 *
 * @author chenweidong
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
public class ServerMethod {
    /**
     * 单例实体
     */
    private Object object;
    /**
     * 服务方法
     */
    private Method method;
}
