package com.qurong.hermes.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qurong.hermes.utils.CoderUtils;
import com.qurong.hermes.utils.HttpUtils;
import com.qurong.hermes.utils.RsaUtils;
import lombok.Data;

import java.io.IOException;
import java.util.Random;

/**
 * 注册中心
 *
 * @author chenweidong
 */
@Data
public class Center {
    private String host;
    private String publicKey;
    private Integer length;
    private Integer timeout;
    private String sessionId;
    private Boolean status;

    public Center(String host) {
        this.host = host;
        this.status = false;
    }

    /**
     * 注册
     *
     * @param serverId 服务ID
     * @param ownHost  服务host
     * @throws IOException 请求异常
     */
    public void register(String serverId, String ownHost) throws IOException {
        String sessionId = getRandom();
        String resp = HttpUtils.sendGet("http://" + this.host + "/register",
                "id=" + serverId + "&sessionId=" + sessionId + "&host=" + ownHost);
        JSONObject obj = JSON.parseObject(resp);
        JSONObject data = obj.getJSONObject(Constant.DATA);
        String[] tmp = data.getString("PublicKey").split("\n");
        this.publicKey = tmp[1] + tmp[2] + tmp[3] + tmp[4];
        this.length = data.getInteger("Length");
        this.timeout = data.getInteger("Timeout");
        this.status = true;
        this.sessionId = sessionId;
    }

    /**
     * 心跳检测
     */
    public void heartBeat() {
        String resp;
        try {
            resp = HttpUtils.sendGet("http://" + this.host + "/heartBeat",
                    "sessionId=" + this.sessionId);
        } catch (IOException e) {
            // 心跳失败
            this.status = false;
            return;
        }
        JSONObject obj = JSON.parseObject(resp);
        this.status = obj.getInteger(Constant.CODE) == 0;
        if (!this.status) {
            // 尝试重新注册
            HermesConfig config = (HermesConfig) ApplicationContextHelper.getBean("config");
            try {
                this.register(config.getServerId(), config.getHost());
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * 请求服务
     *
     * @param serverId 服务ID
     * @param name     方法名
     * @param data     数据
     * @return 响应数据
     */
    public String call(String serverId, String name, String data) throws Exception {
        // 加密请求数据
        byte[] in = data.getBytes("utf-8");
        if (in.length > this.length - Constant.RSA_RESERVED_LENGTH) {
            throw new Exception("data is too big");
        }
        byte[] bytes = RsaUtils.encryptByPublicKey(in, this.publicKey);
        // send转16进制
        String send = CoderUtils.bytesToHex(bytes);
        String resp = HttpUtils.sendGet("http://" + this.host + "/server",
                "sessionId=" + this.sessionId + "&serverId=" + serverId +
                        "&name=" + name + "&data=" + send);
        JSONObject obj = JSON.parseObject(resp);
        Integer code = obj.getInteger(Constant.CODE);
        if (code != 0) {
            throw new Exception("call server fail, fail code: " + code);
        }
        // 解密响应
        byte[] receive = RsaUtils.decryptByPublicKey(obj.getBytes(Constant.DATA), this.publicKey);
        return new String(receive);
    }

    /**
     * 生产随机字符串
     *
     * @return 随机数
     */
    private String getRandom() {
        Random random = new Random();
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < Constant.SESSION_ID_LENGTH; i++) {
            boolean isChar = random.nextInt(2) % 2 == 0;
            if (isChar) {
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                ret.append((char) (choice + random.nextInt(26)));
            } else {
                ret.append(Integer.toString(random.nextInt(10)));
            }
        }
        return ret.toString();
    }
}
