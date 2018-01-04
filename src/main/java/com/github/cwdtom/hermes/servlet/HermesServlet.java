package com.github.cwdtom.hermes.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.github.cwdtom.hermes.annotation.HermesParam;
import com.github.cwdtom.hermes.entity.*;
import com.github.cwdtom.hermes.utils.CoderUtils;
import com.github.cwdtom.hermes.utils.RsaUtils;
import lombok.AllArgsConstructor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        Center center = ApplicationContextHelper.getBean(Constant.CENTERS_BEAN_NAME, Centers.class)
                .getCenterBySessionId(req.getParameter("sessionId"));
        ServletOutputStream out = resp.getOutputStream();
        try {
            // hex to bytes
            byte[] bytes = CoderUtils.hexStringToByteArray(req.getParameter("data"));
            // 解密
            String text = new String(RsaUtils.decryptByPublicKey(bytes, center.getPublicKey()));
            // 通过name选取方法并调用
            Object result = invokeMethodByName(req.getParameter("name"), text);
            byte[] respData = JSON.toJSONString(result).getBytes("utf-8");
            if (respData.length > center.getLength() - Constant.RSA_RESERVED_LENGTH) {
                throw new Exception("response data is too big");
            }
            // 返回加密
            respData = RsaUtils.encryptByPublicKey(respData, center.getPublicKey());
            out.write(respData);
        } catch (Exception e) {
            e.printStackTrace();
            out.write(500);
        } finally {
            out.close();
        }
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
        } catch (JSONException e) {
            return target.getMethod().invoke(target.getObject(), argv);
        }
    }
}
