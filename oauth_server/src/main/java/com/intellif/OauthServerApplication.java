package com.intellif;

import com.intellif.config.DBUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by YangGuanRong
 * date: 2023/1/3
 */

@SpringBootApplication
@MapperScan("com.intellif.mapper")
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
