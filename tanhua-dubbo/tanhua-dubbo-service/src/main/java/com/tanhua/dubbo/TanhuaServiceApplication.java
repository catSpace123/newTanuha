package com.tanhua.dubbo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 服务提供者启动类
 */
@SpringBootApplication
@MapperScan("com.tanhua.dubbo.mapper")
public class TanhuaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TanhuaServiceApplication.class,args);
    }
}
