package com.qurong.hermes;

import com.alibaba.fastjson.JSON;
import com.qurong.hermes.entity.Center;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
    public String call(String serverId, String name, Object data) {
        // 筛选可用center
        List<Center> tmp = new ArrayList<>();
        for (Center c : this.centers) {
            if (c.getStatus()) {
                tmp.add(c);
            }
        }
        int index = (int) System.currentTimeMillis() % tmp.size();
        String resp = null;
        try {
            resp = tmp.get(index).call(serverId, name, JSON.toJSONString(data));
        } catch (Exception ignored) {
            tmp.get(index).setStatus(false);
        }
        return resp;
    }
}
