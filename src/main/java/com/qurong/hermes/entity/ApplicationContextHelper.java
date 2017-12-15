package com.qurong.hermes.entity;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 上下文帮助
 * @author chenweidong
 */
public class ApplicationContextHelper implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHelper.applicationContext = applicationContext;
    }

    /**
     * 获取bean
     * @param beanName bean名字
     * @return bean
     */
    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }
}
