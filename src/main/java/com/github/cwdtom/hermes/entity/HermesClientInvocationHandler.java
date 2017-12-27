package com.github.cwdtom.hermes.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.github.cwdtom.hermes.annotation.HermesMapping;
import com.github.cwdtom.hermes.annotation.HermesParam;
import lombok.AllArgsConstructor;

import java.io.IOException;
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
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        HermesMapping hm = method.getAnnotation(HermesMapping.class);
        String name = hm == null ? "" : hm.value();
        Parameter[] params = method.getParameters();

        if (args == null || args.length == 0) {
            // 无参数情况下调用空json
            return castType(method.getReturnType(), call(this.serverId, name, "{}"));
        }
        JSONObject jo = new JSONObject();
        int len = args.length;
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
    private String call(String serverId, String name, String data) throws Exception {
        // 筛选可用center
        List<Center> tmp = ApplicationContextHelper.getBean(Constant.CENTERS_BEAN_NAME, Centers.class).getAbleCenter();
        int size = tmp.size();
        if (size == 0) {
            return null;
        } else if (size == 1) {
            try {
                return tmp.get(0).call(serverId, name, data);
            } catch (IOException ignored) {
                tmp.get(0).setStatus(false);
                return null;
            }
        } else {
            int index = (int) System.currentTimeMillis() % size;
            try {
                return tmp.get(index).call(serverId, name, data);
            } catch (IOException ignored) {
                // 只catch io错误，其他错误向上抛
                tmp.get(index).setStatus(false);
                // 重试
                return call(serverId, name, data);
            }
        }
    }

    /**
     * 类型转换
     *
     * @param clz 返回类
     * @param str 字符串结果
     * @return 转换结果
     */
    private Object castType(Class<?> clz, String str) {
        // 判断结果是否为空
        if (str == null) {
            return null;
        }
        // 判断是否是实体类
        try {
            JSONObject jo = JSON.parseObject(str);
            Object object = clz.newInstance();
            for (Field f : clz.getDeclaredFields()) {
                f.setAccessible(true);
                f.set(object, jo.get(f.getName()));
            }
            return object;
        } catch (JSONException | IllegalAccessException | InstantiationException ignored) {
            // 无法解析成json时，向下匹配基础类型
        }
        try {
            // 判断是否是二进制数组
            if (clz.equals(Byte[].class) || clz.isAssignableFrom(byte[].class)) {
                return str.getBytes("utf-8");
            }
            // 调用基础方法的构造器
            Constructor c = clz.getConstructor(String.class);
            c.setAccessible(true);
            return c.newInstance(str);
        } catch (NoSuchMethodException | InvocationTargetException |
                IllegalAccessException | InstantiationException | UnsupportedEncodingException ignored) {
            // 无法带参实例化直接返回字符串
        }
        return str;
    }
}
