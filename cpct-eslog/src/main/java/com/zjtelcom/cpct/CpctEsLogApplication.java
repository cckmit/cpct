package com.zjtelcom.cpct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:dubbo/dubbo-${spring.profiles.active}.xml")
public class CpctEsLogApplication {

    public static Logger logger = LoggerFactory.getLogger(CpctEsLogApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CpctEsLogApplication.class, args);
        logger.info("***********CPCT_ESLOG****启动**********");
    }
}