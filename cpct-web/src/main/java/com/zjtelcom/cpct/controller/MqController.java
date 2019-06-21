package com.zjtelcom.cpct.controller;

import com.zjtelcom.cpct.service.MqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class MqController extends BaseController implements ApplicationRunner {

    @Autowired
    private MqService mqService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        mqService.initProducer();
    }

}
