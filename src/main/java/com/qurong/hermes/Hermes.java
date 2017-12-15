package com.qurong.hermes;

import com.alibaba.fastjson.JSON;
import com.qurong.hermes.entity.Center;
import lombok.Data;

/**
 * 服务方法
 *
 * @author chenweidong
 */
@Data
public class Hermes {
    private Center[] centers;

    /**
     * 调用服务
     *
     * @param serverId 服务ID
     * @param name     方法名
     * @param data     数据
     * @return 响应结果
     */
    public String call(String serverId, String name, Object data) throws Exception {
        String send = JSON.toJSONString(data);
        Integer index = (int) (Math.random() * this.centers.length);
        return this.centers[index].call(serverId, name, send);
    }
}
