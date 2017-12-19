package com.qurong.hermes.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.qurong.hermes.annotation.HermesMapping;
import com.qurong.hermes.annotation.HermesParam;
import lombok.AllArgsConstructor;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态代理HermesClient接口
 *
 * @author chenweidong
 */
@AllArgsConstructor
public class HermesClientInvocationHandler implements InvocationHandler {
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
        List<Center> tmp = new ArrayList<>(Constant.centers.length);
        for (Center c : Constant.centers) {
            if (c.getStatus()) {
                tmp.add(c);
            }
        }
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
     * @param clz 返回类
     * @param str 字符串结果
     * @return 转换结果
     */
    private Object castType(Class<?> clz, String str)
            throws UnsupportedEncodingException, IllegalAccessException, InstantiationException {
        // 封装class
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

        if (clz.equals(Integer.class) || clz.isAssignableFrom(int.class)) {
            return Integer.parseInt(str);
        } else if (clz.equals(Long.class) || clz.isAssignableFrom(long.class)) {
            return Long.parseLong(str);
        } else if (clz.equals(Byte[].class) || clz.isAssignableFrom(byte[].class)) {
            return str.getBytes("utf-8");
        } else if (clz.equals(Short.class) || clz.isAssignableFrom(short.class)) {
            return Short.parseShort(str);
        } else if (clz.equals(Float.class) || clz.isAssignableFrom(float.class)) {
            return Float.parseFloat(str);
        } else if (clz.equals(Double.class) || clz.isAssignableFrom(double.class)) {
            return Double.parseDouble(str);
        } else if (clz.equals(Boolean.class) || clz.isAssignableFrom(boolean.class)) {
            return Boolean.parseBoolean(str);
        }
        return str;
    }
}
