package com.qurong.hermes;

import com.qurong.hermes.annotation.HermesClient;
import com.qurong.hermes.annotation.HermesMapping;
import com.qurong.hermes.annotation.HermesParam;

/**
 * 测试service
 */
@HermesClient("client")
public interface TestService {
    @HermesMapping("testAdd")
    Integer add(@HermesParam("num") Integer num);

    @HermesMapping("testSub")
    Integer sub(Integer num);

    @HermesMapping("testMul")
    Integer mul(@HermesParam("num") Integer num, @HermesParam("mul") Integer mul);
}
