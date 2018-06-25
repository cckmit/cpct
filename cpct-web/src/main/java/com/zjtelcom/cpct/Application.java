package com.zjtelcom.cpct;

/**
 * Created by huanghua on 2017/5/23.
 */
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(value={"com.zjtelcom.cpct"})
@MapperScan("com.zjtelcom.cpct.dao")
@EnableTransactionManagement
public class Application {
    public static Logger logger = LoggerFactory.getLogger(Application.class);
    // for local development, debug in IDE
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
        logger.info("***********CPCT*********启动**********");
    }
}
