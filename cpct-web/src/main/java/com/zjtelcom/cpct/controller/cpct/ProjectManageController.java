package com.zjtelcom.cpct.controller.cpct;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.controller.campaign.CampaignController;
import com.zjtelcom.cpct.dto.pojo.CpcGroupRequest;
import com.zjtelcom.cpct.dto.pojo.CpcGroupResponse;
import com.zjtelcom.cpct.dto.pojo.Result;
import com.zjtelcom.cpct.pojo.MktCampaignDetailReq;
import com.zjtelcom.cpct.service.cpct.MktCampaignJTService;
import com.zjtelcom.cpct.service.cpct.ProjectManageService;
import com.zjtelcom.cpct.util.CpcUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2019/12/09 15:15
 * version: V1.0
 */
@RestController
@RequestMapping("${adminPath}/projectManage")
public class ProjectManageController {
    private static final Logger LOG = Logger.getLogger(CampaignController.class);

    @Autowired
    private ProjectManageService projectManageService;

    @RequestMapping("/updateProjectStateTime")
    @CrossOrigin
    public String updateProjectStateTime(@RequestBody Map<String, Object> params) {
        Map<String, Object> resultMap = projectManageService.updateProjectStateTime(params);
        return JSON.toJSONString(resultMap);
    }


    @RequestMapping("/updateProjectPcState")
    @CrossOrigin
    public String updateProjectPcState(@RequestBody Map<String, Object> params) {
        Map<String, Object> resultMap = projectManageService.updateProjectStateTime(params);
        return JSON.toJSONString(resultMap);
    }

}