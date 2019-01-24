package com.zjtelcom.cpct.count.controller;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.count.base.enums.ResultEnum;
import com.zjtelcom.cpct.count.service.api.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2019/1/9
 * @Description:活动相关
 */
@RestController
@RequestMapping("${openPath}")
public class ActivityController {

    private Logger log = LoggerFactory.getLogger(ActivityController.class);

    @Autowired
    private ActivityService activityService;

    /**
     * 修改活动状态
     * @param params
     * @return
     */
    @PostMapping("changeStatus")
    @CrossOrigin
    public String singleEvent(@RequestBody Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        System.out.println("活动请求："+params);
        try {
            map=activityService.changeActivityStatus(params);
        } catch (Exception e) {
            map.put("resultCode", ResultEnum.FAILED.getStatus());
            map.put("resultMsg", e.getMessage());
        }
        return JSON.toJSONString(map);
    }

    /**
     * 得到活动列表，删除活动和需求函关系
     * @param params
     * @return
     */
    @PostMapping("getCampaignList")
    @CrossOrigin
    public String getCampaignList(@RequestBody Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        System.out.println("活动请求："+params);
        try {
            map=activityService.getCampaignList(params);
        } catch (Exception e) {
            map.put("resultCode", ResultEnum.FAILED.getStatus());
            map.put("resultMsg", e.getMessage());
        }
        return JSON.toJSONString(map);
    }


    /**
     * 子活动需求函
     * @param params
     * @return
     */
    @PostMapping("generateRequestInfo")
    @CrossOrigin
    public String generateRequestInfo(@RequestBody Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        System.out.println("子活动需求函："+params);
        try {
            map=activityService.generateRequestInfo(params);
        } catch (Exception e) {
            map.put("resultCode", ResultEnum.FAILED.getStatus());
            map.put("resultMsg", e.getMessage());
        }
        return JSON.toJSONString(map);
    }





    public static void main(String[] args) {


    }
}
