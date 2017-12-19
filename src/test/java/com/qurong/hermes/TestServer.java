package com.qurong.hermes;

import com.qurong.hermes.annotation.HermesParam;
import com.qurong.hermes.annotation.HermesService;
import com.qurong.hermes.annotation.HermesMapping;

/**
 * 调用测试
 *
 * @author chenweidong
 */
@HermesService
public class TestServer {
    @HermesMapping("testAdd")
    public Integer testAdd(@HermesParam("num") Integer num) {
        return num + 1;
    }

    @HermesMapping("testSub")
    public Integer testSub(String input) {
        return Integer.parseInt(input) - 1;
    }

    @HermesMapping("testMul")
    public Integer testMul(@HermesParam("num") Integer num, @HermesParam("mul") Integer mul) {
        return num * mul;
    }
}
