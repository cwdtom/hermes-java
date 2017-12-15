package com.qurong.hermes;

import com.qurong.hermes.annotation.HermesService;
import com.qurong.hermes.annotation.HermesMapping;

/**
 * 测试
 *
 * @author chenweidong
 */
@HermesService
public class TestServer {
    /**
     * 调用测试
     */
    @HermesMapping("testAdd")
    public Integer testAdd(String input) {
        return Integer.parseInt(input) + 1;
    }

    @HermesMapping("testSub")
    public Integer testSub(String input) {
        return Integer.parseInt(input) - 1;
    }
}
