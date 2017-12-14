package com.qurong.hermes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 * @author chenweidong
 */
@SpringBootApplication
@EnableScheduling
public class ApplicationMain {
    public static void main(String[] args) {
        // 启动Spring Boot项目的唯一入口
        SpringApplication.run(ApplicationMain.class, args);
    }
}