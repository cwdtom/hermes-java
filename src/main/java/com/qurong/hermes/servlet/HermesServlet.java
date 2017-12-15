package com.qurong.hermes.servlet;

import com.alibaba.fastjson.JSON;
import com.qurong.hermes.Hermes;
import com.qurong.hermes.entity.ApplicationContextHelper;
import com.qurong.hermes.entity.Center;
import com.qurong.hermes.entity.Constant;
import com.qurong.hermes.entity.ServerMethod;
import com.qurong.hermes.utils.CoderUtils;
import com.qurong.hermes.utils.RsaUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

/**
 * 调用服务
 *
 * @author chenweidong
 */
public class HermesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String sessionId = req.getParameter("sessionId");
        String data = req.getParameter("data");
        String name = req.getParameter("name");

        Center center = getCenterBySessionId(sessionId);
        if (center == null) {
            PrintWriter out = resp.getWriter();
            out.write(404);
            return;
        }
        // hex to bytes
        byte[] bytes = CoderUtils.hexStringToByteArray(data);
        String text;
        try {
            // 解密
            text = new String(RsaUtils.decryptByPublicKey(bytes, center.getPublicKey()));
        } catch (Exception e) {
            PrintWriter out = resp.getWriter();
            out.write(501);
            return;
        }
        // 通过name选取方法并调用
        Object result;
        try {
            result = invokeMethodByName(name, text);
        } catch (InvocationTargetException | IllegalAccessException e) {
            PrintWriter out = resp.getWriter();
            out.write(504);
            return;
        }
        byte[] respData = JSON.toJSONString(result).getBytes("utf-8");
        if (respData.length > center.getLength() - Constant.RSA_RESERVED_LENGTH) {
            PrintWriter out = resp.getWriter();
            out.write(503);
            return;
        }
        try {
            // 返回加密
            respData = RsaUtils.encryptByPublicKey(respData, center.getPublicKey());
        } catch (Exception e) {
            PrintWriter out = resp.getWriter();
            out.write(502);
            return;
        }
        ServletOutputStream out = resp.getOutputStream();
        out.write(respData);
    }

    /**
     * 获取对应注册中心
     *
     * @param sessionId sessionId
     * @return 注册中心
     */
    private Center getCenterBySessionId(String sessionId) {
        Hermes hermes = (Hermes) ApplicationContextHelper.getBean("hermes");
        for (Center c : hermes.getCenters()) {
            if (sessionId.equals(c.getSessionId())) {
                return c;
            }
        }
        return null;
    }

    /**
     * 调用注册方法
     * @param name 方法注册名
     * @param argv 参数
     * @return 处理结果
     */
    private Object invokeMethodByName(String name, String argv)
            throws InvocationTargetException, IllegalAccessException {
        ServerMethod target = Constant.methodMap.get(name);
        return target.getMethod().invoke(target.getObject(), argv);
    }
}
