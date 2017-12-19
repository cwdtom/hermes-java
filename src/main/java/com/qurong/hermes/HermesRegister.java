package com.qurong.hermes;

import com.qurong.hermes.annotation.HermesClient;
import com.qurong.hermes.annotation.HermesMapping;
import com.qurong.hermes.annotation.HermesService;
import com.qurong.hermes.entity.Center;
import com.qurong.hermes.entity.Constant;
import com.qurong.hermes.entity.HermesClientFactoryBean;
import com.qurong.hermes.entity.ServerMethod;
import com.qurong.hermes.servlet.HermesServlet;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 注册服务
 *
 * @author chenweidong
 */
public class HermesRegister implements ImportBeanDefinitionRegistrar,
        ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware {
    private Environment environment;

    /**
     * 注册bean
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
        // 赋值全局注册中心
        Constant.centers = centers;
        // 注册监听
        Set<String> urlMappings = new HashSet<>(1);
        urlMappings.add("/hermes");
        BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(ServletRegistrationBean.class)
                .addPropertyValue("servlet", new HermesServlet())
                .addPropertyValue("urlMappings", urlMappings);
        registry.registerBeanDefinition("hermesServlet", bdb.getBeanDefinition());
        // 扫描所有类，forPackage放空字符串表示扫描主包
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(""))
                .setExpandSuperTypes(false)
        );
        // 赋值全局方法映射
        Constant.methodMap = this.getMethodMap(reflections);
        // 注册client
        registerClient(reflections, registry);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata,
                                        BeanDefinitionRegistry registry) {
        registerHermes(registry);
    }

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
