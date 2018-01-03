package com.github.cwdtom.hermes;

/**
 * 失败回调
 * @author chenweidong
 * @since 2.1.0
 */
public class FallBackServiceImpl implements Service {
    @Override
    public Integer add(Integer num) {
        return -1;
    }

    @Override
    public Integer sub(Integer num) {
        return -1;
    }

    @Override
    public Integer mul(Integer num, Integer mul) {
        return -1;
    }

    @Override
    public Entity returnObject() {
        return null;
    }
}
