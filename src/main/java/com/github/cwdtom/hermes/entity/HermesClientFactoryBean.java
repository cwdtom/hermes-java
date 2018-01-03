package com.github.cwdtom.hermes.entity;

import com.github.cwdtom.hermes.annotation.HermesClient;
import lombok.Data;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * client bean工厂
 *
 * @author chenweidong
 * @since 2.0.0
 */
@Data
public class HermesClientFactoryBean implements FactoryBean<Object> {
    private Class<?> type;

    @Override
    public Object getObject() throws IllegalAccessException, InstantiationException {
        HermesClient hc = this.type.getAnnotation(HermesClient.class);
        InvocationHandler handler;
        Class<?> fallback = hc.fallback();
        if (fallback.equals(void.class)) {
            handler = new HermesClientInvocationHandler(hc.value(), null);
        } else if (type.isAssignableFrom(fallback)) {
            handler = new HermesClientInvocationHandler(hc.value(), fallback.newInstance());
        } else {
            throw new ExceptionInInitializerError("fallback class is not implement " + type.getName());
        }
        return Proxy.newProxyInstance(this.type.getClassLoader(), new Class[]{this.type}, handler);
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
