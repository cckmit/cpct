package com.zjtelcom.cpct.controller;

import com.zjtelcom.cpct.service.EngineTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 工具测试控制器
 */
@RestController
@RequestMapping("${adminPath}/test")
public class TestController extends BaseController {

    @Autowired
    private EngineTestService engineTestService;

    @RequestMapping("/engine")
    @CrossOrigin
    public String engine() {

        engineTestService.test();

        return initSuccRespInfo(null);
    }

}
