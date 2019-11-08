package com.zjtelcom.cpct.controller.report;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.report.MktCampaingReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/11/07 21:54
 * @version: V1.0
 */
@RestController
@RequestMapping("${adminPath}/newReport")
public class MktCampaingReportController extends BaseController {

    @Autowired
    private MktCampaingReportService mktCampaingReportService;

    @PostMapping("/headInfo")
    @CrossOrigin
    public String getHeadInfo(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = mktCampaingReportService.getHeadInfo(params);
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","查询成功");
        } catch (Exception e) {
            logger.error("Exception: ", JSONArray.toJSON(params), e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","查询失败");
            return JSON.toJSONString(result);
        }
        return JSON.toJSONString(result);
    }


    @PostMapping("/channelInfo")
    @CrossOrigin
    public String getChannelInfo(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = mktCampaingReportService.getChannelInfo(params);
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","查询成功");
        } catch (Exception e) {
            logger.error("Exception: ", JSONArray.toJSON(params), e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","查询失败");
            return JSON.toJSONString(result);
        }
        return JSON.toJSONString(result);
    }


    @PostMapping("/typeInfo")
    @CrossOrigin
    public String getTypeInfo(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = mktCampaingReportService.getTypeInfo(params);
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","查询成功");
        } catch (Exception e) {
            logger.error("Exception: ", JSONArray.toJSON(params), e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","查询失败");
            return JSON.toJSONString(result);
        }
        return JSON.toJSONString(result);
    }
}