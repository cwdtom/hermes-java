package com.github.cwdtom.hermes;

import com.github.cwdtom.hermes.annotation.HermesClient;
import com.github.cwdtom.hermes.annotation.HermesMapping;
import com.github.cwdtom.hermes.annotation.HermesParam;

/**
 * 测试服务调用service
 *
 * @author chenweidong
 * @since 2.0.0
 */
@HermesClient(value = "client", fallback = FallBackServiceImpl.class)
public interface Service {
    /**
     * 加1
     */
    @HermesMapping("testAdd")
    Integer add(@HermesParam("num") Integer num);

    /**
     * 减1
     */
    @HermesMapping("testSub")
    Integer sub(Integer num);

    /**
     * 相乘
     */
    @HermesMapping("testMul")
    int mul(@HermesParam("num") Integer num, @HermesParam("mul") Integer mul);

    /**
     * 返回实体类
     */
    @HermesMapping("returnObject")
    Entity returnObject();
}
