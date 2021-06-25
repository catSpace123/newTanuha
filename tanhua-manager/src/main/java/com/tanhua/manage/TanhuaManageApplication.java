package com.tanhua.manage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 探花后台启动类
 */
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@MapperScan("com.tanhua.manage.mapper")
@EnableScheduling   //开启定时任务
public class TanhuaManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(TanhuaManageApplication.class,args);
    }
}
