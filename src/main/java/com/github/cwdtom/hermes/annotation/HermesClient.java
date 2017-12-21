package com.github.cwdtom.hermes.annotation;

import java.lang.annotation.*;

/**
 * 注册调用服务接口类
 *
 * @author chenweidong
 * @since 2.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HermesClient {
    /**
     * @return 需要调用服务的serverId
     */
    String value();
}
