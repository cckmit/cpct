package com.zjtelcom.cpct.controller.report;

import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.report.ServiceCamReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}/serviceCam")
public class ServiceCamReportController extends BaseController {

    @Autowired
    private ServiceCamReportService serviceCamReportService;

    //serviceCamInfo
    @PostMapping("/serviceCamInfo")
    @CrossOrigin
    public Map<String,Object> serviceCamInfo(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = serviceCamReportService.serviceCamInfo(params);
        } catch (Exception e) {
            logger.error("[ServiceCamReportController 主题活动  serviceCamInfo] fail to listEvents for serviceCamInfo = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }

    //主题活动
    @PostMapping("/activityTheme")
    @CrossOrigin
    public Map<String,Object> activityTheme(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = serviceCamReportService.activityTheme(params);
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
            map = serviceCamReportService.contactNumber(params);
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
            map = serviceCamReportService.orderSuccessRate(params);
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
            map = serviceCamReportService.incomePull(params);
        } catch (Exception e) {
            logger.error("[XinNewAactivityController 收入拉动  incomePull] fail to listEvents for getRptEventOrder = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }

}
