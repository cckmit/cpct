package com.zjtelcom.cpct.controller.api;


import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.api.EventApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("${adminPath}/api")
public class EventApiController extends BaseController {


    @Autowired
    private EventApiService eventApiService;

    /**
     * 事件触发入口
     */
    @RequestMapping("/eventInput")
    @CrossOrigin
    public String eventInput() {





        return initSuccRespInfo(null);
    }
}
