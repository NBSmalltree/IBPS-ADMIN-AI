package com.actuary.ibps;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.actuary.ibps.infrastructure.mapper")
@EnableAsync
public class IbpsAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(IbpsAdminApplication.class, args);
    }
}
