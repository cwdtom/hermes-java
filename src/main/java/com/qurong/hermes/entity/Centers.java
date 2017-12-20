package com.qurong.hermes.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 注册中心单例
 *
 * @author chenweidong
 * @since 2.0.1
 */
@Data
public class Centers {
    /**
     * 注册中心列表
     */
    private Center[] centers;

    /**
     * 获取对应注册中心
     *
     * @param sessionId sessionId
     * @return 注册中心
     */
    public Center getCenterBySessionId(String sessionId) {
        for (Center c : this.centers) {
            if (sessionId.equals(c.getSessionId())) {
                return c;
            }
        }
        return null;
    }

    /**
     * 获取可用注册中心
     *
     * @return 可用列表
     */
    public List<Center> getAbleCenter() {
        List<Center> ableList = new ArrayList<>(this.centers.length);
        for (Center c : this.centers) {
            if (c.getStatus()) {
                ableList.add(c);
            }
        }
        return ableList;
    }
}
