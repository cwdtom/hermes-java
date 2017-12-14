package com.qurong.hermes.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * 方法调用对象
 * @author chenweidong
 */
@Data
@AllArgsConstructor
public class ServerMethod {
    private Object object;
    private Method method;
}
