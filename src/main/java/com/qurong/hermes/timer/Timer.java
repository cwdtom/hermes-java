package com.qurong.hermes.timer;

import com.qurong.hermes.Hermes;
import com.qurong.hermes.entity.Center;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

/**
 * 定时器
 * @author chenweidong
 */
public class Timer {
    @Resource
    private Hermes hermes;

    /**
     * 心跳 - 每1分钟同步一次 首次延迟1分钟
     */
    @Scheduled(initialDelay = 60000, fixedRate = 60000)
    public void heartBeat() {
        for (Center c : hermes.getCenters()) {
            c.heartBeat();
        }
    }
}
