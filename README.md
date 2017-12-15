# Hermes - Java


![Version](https://img.shields.io/badge/version-1.2.0-green.svg)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](http://opensource.org/licenses/MIT)

## Overview
- 基于spring-boot Hermes中间件Java端SDK
- Hermes (https://github.com/cwdtom/hermes)

## Configuration
- application中添加新配置
    ```text
   # 注册中心地址，多个地址以逗号隔开
   hermes.center-zone=127.0.0.1:8080
   # 自身服务ID
   hermes.server-id=client
   # 本地服务地址
   hermes.host=127.0.0.1
    ```

## Usage

1. 启用hermes
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
    1. 添加@EnableHermes注解

1. 调用远程方法
    ```java
    public class Test {
        @Resource
        private Hermes hermes;
        
        public String callAdd(Integer num) {
            return hermes.call("serverId", "funcName", num);
        }
    }
    ```

1. 创建远程调用方法
    ```java
    @HermesService
    public class TestServer {   
        @HermesMapping("testAdd")
        public Integer testAdd(String input) {
            return Integer.parseInt(input) + 1;
        }
    }
    ```
