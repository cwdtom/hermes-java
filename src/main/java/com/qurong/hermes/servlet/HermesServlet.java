package com.qurong.hermes.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.qurong.hermes.annotation.HermesParam;
import com.qurong.hermes.entity.*;
import com.qurong.hermes.utils.CoderUtils;
import com.qurong.hermes.utils.RsaUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * 调用服务
 *
 * @author chenweidong
 * @since 1.0.0
 */
@AllArgsConstructor
public class HermesServlet extends HttpServlet {
    /**
     * 方法路径映射
     */
    private Map<String, ServerMethod> methodMap;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String sessionId = req.getParameter("sessionId");
        String data = req.getParameter("data");
        String name = req.getParameter("name");

        Center center = ApplicationContextHelper.getBean(Constant.CENTERS_BEAN_NAME, Centers.class)
                .getCenterBySessionId(sessionId);
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
     * 调用注册方法
     *
     * @param name 方法注册名
     * @param argv 参数
     * @return 处理结果
     */
    private Object invokeMethodByName(String name, String argv)
            throws InvocationTargetException, IllegalAccessException {
        ServerMethod target = this.methodMap.get(name);
        Parameter[] params = target.getMethod().getParameters();
        try {
            JSONObject jo = JSON.parseObject(argv);
            int len = params.length;
            Object[] args = new Object[len];
            for (int i = 0; i < len; i++) {
                HermesParam hp = params[i].getAnnotation(HermesParam.class);
                Object arg = jo.get(hp.value());
                args[i] = arg;
            }
            return target.getMethod().invoke(target.getObject(), args);
        } catch (JSONException ignored) {
            return target.getMethod().invoke(target.getObject(), argv);
        }
    }
}
