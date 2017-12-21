package com.github.cwdtom.hermes;

import com.github.cwdtom.hermes.annotation.HermesMapping;
import com.github.cwdtom.hermes.annotation.HermesParam;
import com.github.cwdtom.hermes.annotation.HermesService;

/**
 * 调用测试服务
 *
 * @author chenweidong
 * @since 1.0.0
 */
@HermesService
public class TestServer {
    /**
     * 加1
     */
    @HermesMapping("testAdd")
    public Integer testAdd(@HermesParam("num") Integer num) {
        return num + 1;
    }

    /**
     * 减1
     */
    @HermesMapping("testSub")
    public Integer testSub(String input) {
        return Integer.parseInt(input) - 1;
    }

    /**
     * 相乘
     */
    @HermesMapping("testMul")
    public Integer testMul(@HermesParam("num") Integer num, @HermesParam("mul") Integer mul) {
        return num * mul;
    }
}
