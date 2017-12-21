package com.github.cwdtom.hermes.annotation;

import java.lang.annotation.*;

/**
 * 服务名注册
 *
 * @author chenweidong
 * @since 1.2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface HermesMapping {
    /**
     * @return 需要调用的服务名
     */
    String value();
}
