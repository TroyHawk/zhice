package com.zhice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 智策平台后端启动类
 */
@SpringBootApplication
@MapperScan("com.zhice.**.mapper") // 扫描所有模块下的 mapper 接口
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("==================================================");
        System.out.println("====== 智策平台后端启动成功 (Java 20 环境) ======");
        System.out.println("====== Swagger 接口文档地址: http://localhost:8080/swagger-ui/index.html ======");
        System.out.println("==================================================");
    }
}