package com.github.cwdtom.hermes.annotation;

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
     * @return 参数名称
     */
    String value();
}
