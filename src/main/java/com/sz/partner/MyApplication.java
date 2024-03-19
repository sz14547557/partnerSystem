package com.sz.partner;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 *

 */
@SpringBootApplication
// todo sz※ 修改包名别忘了连带修改Mybatis的扫描路径
@MapperScan("com.sz.partner.mapper")
// 在SpringBoot中开启定时任务 会自动执行定义好的定时任务
@EnableScheduling
public class MyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }

}

