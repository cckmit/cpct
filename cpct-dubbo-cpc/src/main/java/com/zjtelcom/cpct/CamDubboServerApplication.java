package com.zjtelcom.cpct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ImportResource("classpath:dubbo/dubbo-${spring.profiles.active}.xml")
public class CamDubboServerApplication {

    public static Logger logger = LoggerFactory.getLogger(CamDubboServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CamDubboServerApplication.class, args);
        logger.info("***********CPCT_CAM_DUBBO_SERVER****启动**********");
    }
}