package com.qurong.hermes;

import com.qurong.hermes.annotation.EnableHermes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 测试启动类
 *
 * @author chenweidong
 */
@SpringBootApplication
@EnableHermes
public class ApplicationMain {
    public static void main(String[] args) {
        // 启动Spring Boot项目的唯一入口
        SpringApplication.run(ApplicationMain.class, args);
    }
}