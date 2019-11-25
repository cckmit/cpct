package com.zjtelcom.cpct.statistic.controller;

import com.zjtelcom.cpct.statistic.service.MqLabelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${adminPath}/")
public class MqEsLogController implements ApplicationRunner {

    protected Logger logger = LoggerFactory.getLogger(MqEsLogController.class);

    @Autowired
    private MqLabelService mqLabelService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        mqLabelService.trialUserLabelConsumer();
        logger.info("试算用户标签储存队列消费启动!!");
    }
}
