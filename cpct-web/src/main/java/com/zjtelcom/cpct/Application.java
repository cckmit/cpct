package com.zjtelcom.cpct;

/**
 * Created by huanghua on 2017/5/23.
 */
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(value={"com.zjtelcom.cpct"})
@MapperScan("com.zjtelcom.cpct.dao")
public class Application {

    // for local development, debug in IDE
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
        System.out.println("------cpct启动------");
    }
}
