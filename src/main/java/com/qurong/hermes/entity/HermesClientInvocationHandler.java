package com.qurong.hermes.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.qurong.hermes.annotation.HermesMapping;
import com.qurong.hermes.annotation.HermesParam;
import lombok.AllArgsConstructor;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.*;
import java.util.List;

/**
 * 动态代理HermesClient接口
 *
 * @author chenweidong
 * @since 2.0.0
 */
@AllArgsConstructor
public class HermesClientInvocationHandler implements InvocationHandler {
    /**
     * 调用服务serverId
     */
    private String serverId;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws UnsupportedEncodingException, InstantiationException, IllegalAccessException {
        HermesMapping hm = method.getAnnotation(HermesMapping.class);
        String name = hm == null ? "" : hm.value();
        Parameter[] params = method.getParameters();
        int len = args.length;

        if (len == 0) {
            // 无参数情况下调用空json
            return castType(method.getReturnType(), call(this.serverId, name, "{}"));
        }
        JSONObject jo = new JSONObject();
        for (int i = 0; i < len; i++) {
            HermesParam hp = params[i].getAnnotation(HermesParam.class);
            if (hp == null) {
                return castType(method.getReturnType(), call(this.serverId, name, JSON.toJSONString(args[0])));
            }
            jo.put(hp.value(), args[i]);
        }
        return castType(method.getReturnType(), call(this.serverId, name, jo.toJSONString()));
    }

    /**
     * 调用服务
     *
     * @param serverId 服务ID
     * @param name     方法名
     * @param data     数据
     * @return 响应结果
     */
    private String call(String serverId, String name, String data) {
        // 筛选可用center
        List<Center> tmp = ApplicationContextHelper.getBean(Constant.CENTERS_BEAN_NAME, Centers.class).getAbleCenter();
        int size = tmp.size();
        if (size == 0) {
            return null;
        }
        int index = (int) System.currentTimeMillis() % size;
        String resp = null;
        try {
            resp = tmp.get(index).call(serverId, name, data);
        } catch (Exception ignored) {
            tmp.get(index).setStatus(false);
        }
        return resp;
    }

    /**
     * 类型转换
     *
     * @param clz 返回类
     * @param str 字符串结果
     * @return 转换结果
     */
    private Object castType(Class<?> clz, String str)
            throws UnsupportedEncodingException, IllegalAccessException, InstantiationException {
        // 判断结果是否为空
        if (str == null) {
            return null;
        }
        // 判断是否是实体类
        try {
            JSONObject jo = JSON.parseObject(str);
            Object object = clz.newInstance();
            for (Field f : clz.getFields()) {
                f.setAccessible(true);
                f.set(object, jo.get(f.getName()));
            }
            return object;
        } catch (JSONException ignored) {
            // 无法解析成json时，向下匹配基础类型
        }
        // 判断是否是二进制数组
        if (clz.equals(Byte[].class) || clz.isAssignableFrom(byte[].class)) {
            return str.getBytes("utf-8");
        }
        // 调用基础方法的构造器
        try {
            Constructor c = clz.getConstructor(String.class);
            c.setAccessible(true);
            return c.newInstance(str);
        } catch (NoSuchMethodException | InvocationTargetException ignored) {
            // 无法带参实例化直接返回字符串
        }
        return str;
    }
}
