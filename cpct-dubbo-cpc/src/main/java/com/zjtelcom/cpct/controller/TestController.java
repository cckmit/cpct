//package com.zjtelcom.cpct.controller;
//
//
//import com.zjtelcom.cpct.dubbo.service.MqServiceOne;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Controller;
//
//@Controller
//public class TestController implements ApplicationRunner {
//
//    @Autowired
//    private MqServiceOne mqServiceone;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        mqServiceone.pushEsLogConsumer();
//        System.out.println("cpctEsLog添加日志中转队列消费启动正常~~!!");
//    }
//
//}
