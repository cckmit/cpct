package com.zjtelcom.cpct.controller.api;


import com.alibaba.fastjson.JSON;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.controller.BaseController;

import com.zjtelcom.cpct.dubbo.service.EventApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public String eventInputSync(@RequestBody Map<String, Object> params) {

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String date = df.format(new Date());

        params.put("reqId","EVT" + date + getRandNum(1,999999));
        Map result = new HashMap();
        try {
            result = eventApiService.CalculateCPCSync(params);
        } catch (Exception e) {
            e.printStackTrace();
            return initFailRespInfo(e.getMessage(), "");
        }
        return initSuccRespInfo(result);
    }

    public static int getRandNum(int min, int max) {
        int randNum = min + (int)(Math.random() * ((max - min) + 1));
        return randNum;
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
