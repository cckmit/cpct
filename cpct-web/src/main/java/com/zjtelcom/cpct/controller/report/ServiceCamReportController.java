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

    //selectOrgIdByStaffId
    @PostMapping("/selectOrgIdByStaffId")
    @CrossOrigin
    public Map<String,Object> selectOrgIdByStaffId(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = serviceCamReportService.selectOrgIdByStaffId(params);
        } catch (Exception e) {
            logger.error("[selectOrgIdByStaffId 主题活动  selectOrgIdByStaffId] fail to listEvents for selectOrgIdByStaffId = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }
}
