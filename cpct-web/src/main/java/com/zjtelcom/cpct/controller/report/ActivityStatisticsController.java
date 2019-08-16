package com.zjtelcom.cpct.controller.report;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.report.ActivityStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}/report")
public class ActivityStatisticsController extends BaseController {


    @Autowired
    ActivityStatisticsService activityStatisticsService;

    /**
     * 递归 获取 门店信息
     */
    @RequestMapping("/getStoreForUser")
    @CrossOrigin
    public String getStoreForUser(@RequestBody Map<String, Object> params) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = activityStatisticsService.getStoreForUser(params);
        } catch (Exception e) {
            logger.error("[op:ActivityStatisticsController] fail to listEvents for getStoreForUser = {}! Exception: ", JSONArray.toJSON(params), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

//    @RequestMapping("/getStoreForTwo")
//    @CrossOrigin
//    public String getStoreForTwo(@RequestBody Map<String, Object> params) {
//        Map<String, Object> maps = new HashMap<>();
//        try {
//            maps = activityStatisticsService.getStoreForTwo(params);
//        } catch (Exception e) {
//            logger.error("[op:ActivityStatisticsController] fail to listEvents for getStoreForThree = {}! Exception: ", JSONArray.toJSON(params), e);
//            return JSON.toJSONString(maps);
//        }
//        return JSON.toJSONString(maps);
//    }
}
