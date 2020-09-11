package com.zjtelcom.cpct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
//@ImportResource("classpath:dubbo/dubbo-${spring.profiles.active}.xml")
public class DubboEventApiApplication {

    public static Logger logger = LoggerFactory.getLogger(DubboEventApiApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DubboEventApiApplication.class, args);
        logger.info("**********CPCT_DUBBO_EVENTAPI****启动**********");
    }
}