package com.github.cwdtom.hermes;

import com.github.cwdtom.hermes.entity.*;
import com.github.cwdtom.hermes.servlet.HermesServlet;
import com.github.cwdtom.hermes.annotation.HermesClient;
import com.github.cwdtom.hermes.annotation.HermesMapping;
import com.github.cwdtom.hermes.annotation.HermesService;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 注册Hermes服务
 *
 * @author chenweidong
 * @since 1.2.0
 */
public class HermesRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private Environment environment;

    /**
     * 注册Hermes
     *
     * @param registry spring bean注册器
     */
    private void registerHermes(BeanDefinitionRegistry registry) {
        String[] zones = this.environment.getProperty("hermes.center-zone").split(",");
        Center[] centers = new Center[zones.length];
        for (int i = 0; i < zones.length; i++) {
            Center center = new Center(zones[i]);
            try {
                center.register(this.environment.getProperty("spring.application.name"),
                        String.format("%s:%s", this.environment.getProperty("hermes.host"),
                                this.environment.getProperty("server.port")));
            } catch (IOException e) {
                // 注册失败
            }
            centers[i] = center;
        }
        // 扫描所有类，forPackage放空字符串表示扫描主包
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(""))
                .setExpandSuperTypes(false)
        );
        // 注册spring上下文帮助
        BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(ApplicationContextHelper.class);
        registry.registerBeanDefinition("applicationContextHelper", bdb.getBeanDefinition());
        // 注册注册中心bean
        bdb = BeanDefinitionBuilder.rootBeanDefinition(Centers.class)
                .addPropertyValue("centers", centers);
        registry.registerBeanDefinition(Constant.CENTERS_BEAN_NAME, bdb.getBeanDefinition());
        // 注册监听
        Set<String> urlMappings = new HashSet<>(1);
        urlMappings.add("/hermes");
        bdb = BeanDefinitionBuilder.rootBeanDefinition(ServletRegistrationBean.class)
                .addPropertyValue("servlet", new HermesServlet(this.getMethodMap(reflections)))
                .addPropertyValue("urlMappings", urlMappings);
        registry.registerBeanDefinition("hermesServlet", bdb.getBeanDefinition());
        // 注册client
        registerClient(reflections, registry);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata,
                                        BeanDefinitionRegistry registry) {
        registerHermes(registry);
    }

    /**
     * 获取方法映射map
     *
     * @param reflections 反射类列表
     * @return 方法映射map
     */
    private Map<String, ServerMethod> getMethodMap(Reflections reflections) {
        // 获取HermesService注解类
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(HermesService.class);
        Map<String, ServerMethod> map = new TreeMap<>();
        for (Class<?> c : classes) {
            for (Method method : c.getDeclaredMethods()) {
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

    /**
     * 动态代理注册调用方法接口
     *
     * @param reflections 反射类列表
     * @param registry    spring bean注册器
     */
    private void registerClient(Reflections reflections, BeanDefinitionRegistry registry) {
        // 获取HermesClient注解类
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(HermesClient.class);
        for (Class<?> c : classes) {
            BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(HermesClientFactoryBean.class)
                    .addPropertyValue("type", c);
            registry.registerBeanDefinition(c.getName(), bdb.getBeanDefinition());
        }
    }
}
