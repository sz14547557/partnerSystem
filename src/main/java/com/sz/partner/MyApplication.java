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
@EnableScheduling
public class MyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }

}

