package com.zjtelcom.cpct.controller.api;


import com.alibaba.fastjson.JSON;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.controller.BaseController;

import com.zjtelcom.cpct.dubbo.service.EventApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("${adminPath}/api")
public class EventApiController extends BaseController {


    @Autowired(required = false)
    private EventApiService eventApiService;

    @Autowired(required = false)
    private YzServ yzServ;

    /**
     * 事件触发入口
     */
//    @RequestMapping("/CalculateCPC")
//    @CrossOrigin
//    public String eventInput(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) {
//        Map result = new HashMap();
//        try {
//            result = eventApiService.CalculateCPC(params);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return initFailRespInfo(e.getMessage(), "");
//        }
//        return initSuccRespInfo(result);
//    }

    @RequestMapping(value = "/CalculateCPCSync", method = RequestMethod.POST)
    @CrossOrigin
    public String eventInputSync(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) {

//        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
//        response.setHeader("Access-Control-Allow-Credentials", "true");
//        response.setHeader("Access-Control-Allow-Methods", "POST, GET");
//        response.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token");

        Map result = new HashMap();
        try {
            result = eventApiService.CalculateCPCSync(params);
        } catch (Exception e) {
            e.printStackTrace();
            return initFailRespInfo(e.getMessage(), "");
        }
        return initSuccRespInfo(result);
    }


//    @RequestMapping("/SecondChannelSynergy")
//    @CrossOrigin
//    public String SecondChannelSynergy(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) {
//        Map result = new HashMap();
//        try {
//            result = eventApiService.secondChannelSynergy(params);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return initFailRespInfo(e.getMessage(), "");
//        }
//        return initSuccRespInfo(result);
//    }


    @RequestMapping(value = "/label", method = RequestMethod.POST)
    @CrossOrigin
    public String label(@RequestBody String params) {
        Map result = new HashMap();
        try {
            result = yzServ.queryYz(params);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return JSON.toJSONString(result);
    }


}
