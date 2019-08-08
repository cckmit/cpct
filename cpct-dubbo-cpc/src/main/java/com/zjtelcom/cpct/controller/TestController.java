package com.zjtelcom.cpct.controller;

import com.zjtelcom.cpct.service.MqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class TestController implements ApplicationRunner {

    @Autowired
    private MqService mqService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        mqService.pushEsLogConsumer();
        System.out.println("cpctEsLog添加日志中转队列消费启动正常~~!!");
    }
}
