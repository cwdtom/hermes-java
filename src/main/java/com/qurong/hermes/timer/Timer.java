package com.qurong.hermes.timer;

import com.qurong.hermes.entity.Center;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时器
 * @author chenweidong
 */
@Component
public class Timer {
    @Resource
    private Center[] centers;

    /**
     * 心跳 - 每1分钟同步一次 首次延迟1分钟
     */
    @Scheduled(initialDelay = 60000, fixedRate = 60000)
    public void heartBeat() {
        for (Center c : centers) {
            c.heartBeat();
        }
    }
}
