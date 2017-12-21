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
    public Object getObject() {
        HermesClient hc = this.type.getAnnotation(HermesClient.class);
        InvocationHandler handler = new HermesClientInvocationHandler(hc.value());
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
