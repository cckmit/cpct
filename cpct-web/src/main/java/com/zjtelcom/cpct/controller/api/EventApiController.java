package com.zjtelcom.cpct.controller.api;


import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.api.EventApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("${adminPath}/api")
public class EventApiController extends BaseController {


    @Autowired(required = false)
    private EventApiService eventApiService;

    /**
     * 事件触发入口
     */
    @RequestMapping("/CalculateCPC")
    @CrossOrigin
    public String eventInput(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> map) {
        Map result = new HashMap();

        Map<String, Object> params = new HashMap<>();
        try {
            result = eventApiService.deal(params);
//            eventApiService.cpc();
        } catch (Exception e) {
            e.printStackTrace();
            return initFailRespInfo(e.getMessage(),"");
        }
        return initSuccRespInfo(result);
    }
}
