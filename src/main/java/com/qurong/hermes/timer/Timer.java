package com.qurong.hermes.timer;

import com.qurong.hermes.entity.Center;
import com.qurong.hermes.entity.Constant;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 定时器
 *
 * @author chenweidong
 */
public class Timer {
    /**
     * 心跳 - 每1分钟同步一次 首次延迟1分钟
     */
    @Scheduled(initialDelay = 60000, fixedRate = 60000)
    public void heartBeat() {
        for (Center c : Constant.centers) {
            c.heartBeat();
        }
    }
}
