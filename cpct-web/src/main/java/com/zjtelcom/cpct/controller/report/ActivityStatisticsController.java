package com.zjtelcom.cpct.controller.report;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ctzj.smt.bss.cooperate.service.dubbo.IReportService;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.report.ActivityStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}/report")
public class ActivityStatisticsController extends BaseController {


    @Autowired
    ActivityStatisticsService activityStatisticsService;

    /**
     *
     * 根据用户登入信息 权限定位 C2 C3 C4 C5
     * 根据父节点查询字节点下所有节点返回
     */
    @PostMapping("/getStoreForUser")
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

    /**
     * 根据选中的父节点 获取父节点下所有子节点下的门店信息
     * A_ORG_ID Z_ORG_ID（关联关系）  org_rel （表）
     * 递归 获取 门店信息
     */
    @PostMapping("/getStore")
    @CrossOrigin
    public String getStore(@RequestBody Map<String, Object> params) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = activityStatisticsService.getStore(params);
        } catch (Exception e) {
            logger.error("[op:ActivityStatisticsController] fail to listEvents for getStore = {}! Exception: ", JSONArray.toJSON(params), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     *  营销活动获取渠道信息
     *  //实时（随销） 5,6 问正义
     *  // 批量（派单） 4,5 问正义
     * @param params
     * @return
     */
    @PostMapping("/getChannel")
    @CrossOrigin
    public Map<String,Object> getChannel(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = activityStatisticsService.getChannel(params);
        } catch (Exception e) {
            logger.error("[op:ActivityStatisticsController] fail to listEvents for getChannel = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }


    //销报表查询接口
    @PostMapping("/getRptEventOrder")
    @CrossOrigin
    public Map<String,Object> getRptEventOrder(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = activityStatisticsService.getRptEventOrder(params);
        } catch (Exception e) {
            logger.error("[op:ActivityStatisticsController] fail to listEvents for getRptEventOrder = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }


    //活动报表查询接口
    @PostMapping("/getRptBatchOrder")
    @CrossOrigin
    public Map<String,Object> getRptBatchOrder(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = activityStatisticsService.getRptBatchOrder(params);
        } catch (Exception e) {
            logger.error("[op:ActivityStatisticsController] fail to listEvents for getRptBatchOrder = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }

    @PostMapping("/queryRptBatchOrderTest")
    @CrossOrigin
    public Map<String,Object> queryRptBatchOrderTest(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = activityStatisticsService.queryRptBatchOrderTest(params);
        } catch (Exception e) {
            logger.error("[op:ActivityStatisticsController] fail to listEvents for queryRptBatchOrder = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }


    @PostMapping("/getMktCampaignDetails")
    @CrossOrigin
    public Map<String,Object> getMktCampaignDetails(@RequestBody Map<String, Object> params){
        return activityStatisticsService.getMktCampaignDetails(params);
    }

}
