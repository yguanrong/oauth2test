package com.example;

import com.example.config.DBUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * Created by YangGuanRong
 * date: 2023/1/3
 */

@SpringBootApplication
@MapperScan("com.example.mapper")
@EnableOpenApi
public class OauthServerApplication {
    public static void main(String[] args) {
        try {
            DBUtils.initDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SpringApplication.run(OauthServerApplication.class, args);
    }
}
