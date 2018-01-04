package com.github.cwdtom.hermes.entity;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 注册中心单例
 *
 * @author chenweidong
 * @since 2.0.1
 */
public class Centers {
    /**
     * 注册中心列表
     */
    private List<Center> centers;
    /**
     * 查找列表
     */
    private Map<String, Center> centerMap;

    /**
     * bean注入时反射调用
     *
     * @param centers 注册中心
     */
    public void setCenters(List<Center> centers) {
        this.centerMap = new TreeMap<>();
        centers.forEach(c -> this.centerMap.put(c.getSessionId(), c));
        this.centers = centers;
    }

    public List<Center> getCenters() {
        return this.centers;
    }

    /**
     * 获取对应注册中心
     *
     * @param sessionId sessionId
     * @return 注册中心
     */
    public Center getCenterBySessionId(String sessionId) {
        return this.centerMap.get(sessionId);
    }

    /**
     * 获取可用注册中心
     *
     * @return 可用列表
     */
    public List<Center> getAbleCenter() {
        return this.centers.parallelStream()
                .filter(Center::getStatus)
                .collect(Collectors.toList());
    }
}
