package com.zjtelcom.cpct.dubbo.controller;


//import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.dubbo.service.EventApiService;
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
@RequestMapping("/eventTest")
public class EventApiTestController {


    @Autowired(required = false)
    private EventApiService eventApiService;

//    @Autowired(required = false)
//    private YzServ yzServ;

    @RequestMapping("/cpc")
    @CrossOrigin
    public String CalculateCPC(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) {
        Map result = new HashMap();
        try {
            result = eventApiService.CalculateCPC(params);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return result.toString();
    }


    @RequestMapping("/cpcSync")
    @CrossOrigin
    public String CalculateCPCSync(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) {
        Map result = new HashMap();
        try {
            result = eventApiService.CalculateCPCSync(params);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return result.toString();
    }


    @RequestMapping("/label")
    @CrossOrigin
    public String label(HttpServletRequest request, HttpServletResponse response, @RequestBody String params) {
        Map result = new HashMap();
        try {
//            result = yzServ.queryYz(params);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return result.toString();
    }




}
