package com.zjtelcom.cpct;

import com.zjtelcom.cpct.dubbo.service.impl.MqProducerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:dubbo/dubbo-${spring.profiles.active}.xml")
public class DubboLogApplication {

    public static Logger logger = LoggerFactory.getLogger(DubboLogApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DubboLogApplication.class, args);
        logger.info("***********CPCT_DUBBO_LOG****启动**********");
    }
}