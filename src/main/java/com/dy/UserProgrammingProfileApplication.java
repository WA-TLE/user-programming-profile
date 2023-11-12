package com.dy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dy.mapper")
public class UserProgrammingProfileApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserProgrammingProfileApplication.class, args);
    }

}
