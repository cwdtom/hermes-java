package com.github.cwdtom.hermes.annotation;

import java.lang.annotation.*;

/**
 * 服务类注册
 *
 * @author chenweidong
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface HermesService {
}
