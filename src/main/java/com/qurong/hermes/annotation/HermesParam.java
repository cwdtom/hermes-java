package com.qurong.hermes.annotation;

import java.lang.annotation.*;

/**
 * 参数名注册
 *
 * @author chenweidong
 * @since 2.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface HermesParam {
    /**
     * 参数名称
     */
    String value();
}
