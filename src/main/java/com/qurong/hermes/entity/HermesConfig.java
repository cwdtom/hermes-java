package com.qurong.hermes.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 配置
 * @author chenweidong
 */
@Data
@AllArgsConstructor
public class HermesConfig {
    private String host;
    private String serverId;
}
