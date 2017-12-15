package com.qurong.hermes.initialize;

import com.qurong.hermes.Hermes;
import com.qurong.hermes.annotation.HermesMapping;
import com.qurong.hermes.annotation.HermesService;
import com.qurong.hermes.entity.ApplicationContextHelper;
import com.qurong.hermes.entity.Center;
import com.qurong.hermes.entity.HermesConfig;
import com.qurong.hermes.entity.ServerMethod;
import com.qurong.hermes.servlet.HermesServlet;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 注入初始化数据
 * @author chenweidong
 */
@Component
@Configuration
public class InitializeData {
    @Value("${hermes.center-zone}")
    private String centerZone;
    @Value("${hermes.server-id}")
    private String serverId;
    @Value("${hermes.host}")
    private String host;

    @Bean(name = "centers")
    public Center[] register() {
        String[] zones = centerZone.split(",");
        Center[] centers = new Center[zones.length];
        for (int i=0; i<zones.length; i++) {
            Center center = new Center(zones[i]);
            try {
                center.register(serverId, host);
            } catch (IOException e) {
                // 注册失败
                continue;
            }
            centers[i] = center;
        }
        return centers;
    }

    @Bean(name = "config")
    public HermesConfig config() {
        return new HermesConfig(host, serverId);
    }

    @Bean
    public Hermes hermesServer() {
        return new Hermes();
    }

    @Bean
    public ServletRegistrationBean hermesServlet() {
        return new ServletRegistrationBean(new HermesServlet(), "/hermes");
    }

    @Bean
    public ApplicationContextHelper applicationContextHelper() {
        return new ApplicationContextHelper();
    }

    @Bean(name = "methodMap")
    public Map<String, ServerMethod> methodMap() {
        // 扫描所有类
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forClassLoader())
                .setExpandSuperTypes(false)
        );
        // 获取HermesService注解类
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(HermesService.class);
        Map<String, ServerMethod> map = new TreeMap<>();
        for (Class c : classes) {
            Method[] methods = c.getDeclaredMethods();
            for (Method method : methods) {
                Object object;
                try {
                    object = c.newInstance();
                } catch (InstantiationException | IllegalAccessException ignored) {
                    continue;
                }
                HermesMapping hermesMapping = method.getAnnotation(HermesMapping.class);
                if (hermesMapping != null) {
                    map.put(hermesMapping.value(), new ServerMethod(object, method));
                }
            }
        }
        return map;
    }
}
