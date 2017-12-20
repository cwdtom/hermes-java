package com.qurong.hermes.entity;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 帮助获取spring上下文
 *
 * @author chenweidong
 * @since 2.0.1
 */
public class ApplicationContextHelper implements ApplicationContextAware {
    /**
     * spring上下文
     */
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHelper.applicationContext = applicationContext;
    }

    /**
     * 获取bean
     *
     * @param name bean name
     * @param clz  bean type
     * @param <T>  bean type
     * @return bean
     */
    public static <T> T getBean(String name, Class<T> clz) {
        return ApplicationContextHelper.applicationContext.getBean(name, clz);
    }
}
