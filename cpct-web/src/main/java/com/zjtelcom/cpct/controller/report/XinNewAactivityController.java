package com.zjtelcom.cpct.controller.report;

import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.report.XinNewAactivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}/xinNewReport")
public class XinNewAactivityController  extends BaseController {


    @Autowired
    private XinNewAactivityService xinNewAactivityService;

    //主题活动
    @PostMapping("/activityTheme")
    @CrossOrigin
    public Map<String,Object> activityTheme(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = xinNewAactivityService.activityTheme(params);
        } catch (Exception e) {
            logger.error("[XinNewAactivityController 主题活动  activityTheme] fail to listEvents for getRptEventOrder = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }


    //客触数
    @PostMapping("/contactNumber")
    @CrossOrigin
    public Map<String,Object> contactNumber(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = xinNewAactivityService.contactNumber(params);
        } catch (Exception e) {
            logger.error("[XinNewAactivityController 客触数  contactNumber] fail to listEvents for getRptEventOrder = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }


    //转换率
    @PostMapping("/orderSuccessRate")
    @CrossOrigin
    public Map<String,Object> orderSuccessRate(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = xinNewAactivityService.orderSuccessRate(params);
        } catch (Exception e) {
            logger.error("[XinNewAactivityController 转换率  orderSuccessRate] fail to listEvents for getRptEventOrder = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }


    //收入拉动
    @PostMapping("/incomePull")
    @CrossOrigin
    public Map<String,Object> incomePull(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = xinNewAactivityService.incomePull(params);
        } catch (Exception e) {
            logger.error("[XinNewAactivityController 收入拉动  incomePull] fail to listEvents for getRptEventOrder = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }


    //主题活动渠道和省地市选择
    @PostMapping("/activityThemeLevelAndChannel")
    @CrossOrigin
    public Map<String,Object> activityThemeLevelAndChannel(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = xinNewAactivityService.activityThemeLevelAndChannel(params);
        } catch (Exception e) {
            logger.error("[XinNewAactivityController 主题活动分类和数量  activityThemeLevelAndChannel] fail to listEvents for getRptEventOrder = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }

    //主题活动分类和数量
    @PostMapping("/activityThemeCount")
    @CrossOrigin
    public Map<String,Object> activityThemeCount(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = xinNewAactivityService.activityThemeCount(params);
        } catch (Exception e) {
            logger.error("[XinNewAactivityController 主题活动分类和数量  activityThemeCount] fail to listEvents for getRptEventOrder = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }


    //季度营销活动
    @PostMapping("/quarterActivities")
    @CrossOrigin
    public Map<String,Object> quarterActivities(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = xinNewAactivityService.quarterActivities(params);
        } catch (Exception e) {
            logger.error("[XinNewAactivityController 季度营销活动  quarterActivities] fail to listEvents for getRptEventOrder = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }


}
