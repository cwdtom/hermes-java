package com.github.cwdtom.hermes.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.cwdtom.hermes.utils.CoderUtils;
import com.github.cwdtom.hermes.utils.RsaUtils;
import com.github.cwdtom.hermes.utils.HttpUtils;
import lombok.Data;

import java.io.IOException;
import java.util.Random;

/**
 * 注册中心
 *
 * @author chenweidong
 * @since 1.0.0
 */
@Data
public class Center {
    /**
     * 本服务地址
     */
    private String host;
    /**
     * 公钥
     */
    private String publicKey;
    /**
     * RSA长度，用于判断密文是否过长
     */
    private Integer length;
    /**
     * 服务超时时间
     */
    private Integer timeout;
    /**
     * 服务对注册中心唯一ID
     */
    private String sessionId;
    /**
     * 状态
     */
    private Boolean status;
    /**
     * 注册服务ID
     */
    private String serverId;
    /**
     * 注册中心地址
     */
    private String address;

    public Center(String host) {
        this.host = host;
        this.status = false;
    }

    /**
     * 注册
     *
     * @param serverId 服务ID
     * @param address  服务host
     * @throws IOException 请求异常
     */
    public void register(String serverId, String address) throws IOException {
        String sessionId = getRandomString();
        this.status = true;
        this.sessionId = sessionId;
        this.serverId = serverId;
        this.address = address;
        // 发送注册请求
        String resp = HttpUtils.sendGet(String.format("http://%s/register?id=%s&sessionId=%s&host=%s",
                this.host, serverId, sessionId, address));
        JSONObject data = JSON.parseObject(resp).getJSONObject(Constant.DATA);
        String[] tmp = data.getString("PublicKey").split("\n");
        this.publicKey = String.format("%s%s%s%s", tmp[1], tmp[2], tmp[3], tmp[4]);
        this.length = data.getInteger("Length");
        this.timeout = data.getInteger("Timeout");
    }

    /**
     * 心跳检测
     */
    public void heartBeat() {
        String resp;
        try {

            resp = HttpUtils.sendGet(String.format("http://%s/heartBeat?sessionId=%s", this.host, this.sessionId));
        } catch (IOException e) {
            // 心跳失败
            this.status = false;
            return;
        }
        JSONObject obj = JSON.parseObject(resp);
        this.status = obj.getInteger(Constant.CODE) == 0;
        if (!this.status) {
            // 尝试重新注册
            try {
                this.register(this.serverId, this.address);
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
     * @throws Exception 加解密以及io错误
     */
    public String call(String serverId, String name, String data) throws Exception {
        // 加密请求数据
        byte[] in = data.getBytes("utf-8");
        if (in.length > this.length - Constant.RSA_RESERVED_LENGTH) {
            throw new Exception("data is too big");
        }
        byte[] bytes = RsaUtils.encryptByPublicKey(in, this.publicKey);
        // bytes转16进制发送
        String resp = HttpUtils.sendGet(String.format("http://%s/server?sessionId=%s&serverId=%s&name=%s&data=%s",
                this.host, this.sessionId, serverId, name, CoderUtils.bytesToHex(bytes)));
        JSONObject obj = JSON.parseObject(resp);
        Integer code = obj.getInteger(Constant.CODE);
        if (code != 0) {
            throw new Exception(String.format("call server fail, fail code: %d", code));
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
    private String getRandomString() {
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