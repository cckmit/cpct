package com.zjtelcom.cpct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
//@ImportResource("classpath:dubbo/dubbo-${spring.profiles.active}.xml")
public class LabelStatisticApplication {

    public static Logger logger = LoggerFactory.getLogger(LabelStatisticApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LabelStatisticApplication.class, args);
        logger.info("***********CPCT*****LABEL_STATISTIC****启动**********");
    }
}
