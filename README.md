# Hermes - Java


![Version](https://img.shields.io/badge/version-2.3.0-green.svg)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](http://opensource.org/licenses/MIT)

## Overview
- 基于spring-boot、Hermes中间件Java端SDK，需要JDK8及以上，2.3.0及以上版本需要hermes注册中心在2.1.0版本及以上
- Hermes (https://github.com/cwdtom/hermes)

## Configuration
- application中添加新配置
    ```properties
   # 自身服务ID
   spring.application.name=client
   # 注册中心地址，多个地址以逗号隔开
   hermes.center-zone=127.0.0.1:8080
    ```

## Usage

1. 启用hermes
    - 添加maven依赖
    ```xml
    <dependencies>
        <dependency>
            <groupId>com.github.cwdtom</groupId>
            <artifactId>hermes</artifactId>
            <version>2.3.0</version>
        </dependency>
    </dependencies>
    ```
    
    - 添加@EnableHermes注解
    ```java
    @SpringBootApplication
    @EnableHermes
    public class ApplicationMain {
        public static void main(String[] args) {
            // 启动Spring Boot项目的唯一入口
            SpringApplication.run(ApplicationMain.class, args);
        }
    }
    ```


1. 调用远程方法
    ```java
    /**
     * 测试调用
     */
    @RestController
    @RequestMapping(method = RequestMethod.GET)
    public class MainController {
        @Resource
        private TestService testService;
    
        @RequestMapping("/callAdd")
        public Integer callAdd(Integer num) {
            return testService.add(num);
        }
    
        @RequestMapping("/callSub")
        public Integer callSub(Integer num) {
            return testService.sub(num);
        }
    
        @RequestMapping("/callMul")
        public Integer callAdd(Integer num, Integer mul) {
            return testService.mul(num, mul);
        }
    }
    ```
    
    ```java
    /**
     * 定义调用接口
     */
    @HermesClient(value = "client", fallback = FallBackServiceImpl.class)
    public interface TestService {
        @HermesMapping("testAdd")
        Integer add(@HermesParam("num") Integer num);
    
        @HermesMapping("testSub")
        Integer sub(Integer num);
    
        @HermesMapping("testMul")
        Integer mul(@HermesParam("num") Integer num, @HermesParam("mul") Integer mul);
    }
    ```
    
    ```java
    /**
     * 失败回调
     */
    public class FallBackServiceImpl implements Service {
        @Override
        public Integer add(Integer num) {
            return -1;
        }
    
        @Override
        public Integer sub(Integer num) {
            return -1;
        }
    
        @Override
        public Integer mul(Integer num, Integer mul) {
            return -1;
        }
    
        @Override
        public Entity returnObject() {
            return null;
        }
    }

    ```

1. 创建远程调用方法
    ```java
    @HermesService
    public class TestServer {   
        @HermesMapping("testAdd")
        public Integer testAdd(@HermesParam("num") Integer num) {
            return num + 1;
        }
    
        @HermesMapping("testSub")
        public Integer testSub(String input) {
            return Integer.parseInt(input) - 1;
        }
    
        @HermesMapping("testMul")
        public Integer testMul(@HermesParam("num") Integer num, @HermesParam("mul") Integer mul) {
            return num * mul;
        }
    }
    ```
