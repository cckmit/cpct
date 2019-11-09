package com.zjtelcom.cpct.controller.report;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.report.MktCampaingReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
            result.put("code","0000");
            result.put("message","成功");
        } catch (Exception e) {
            logger.error("Exception: ", JSONArray.toJSON(params), e);
            result.put("code","0002");
            result.put("message","服务逻辑异常");
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
            result.put("code","0000");
            result.put("message","成功");
        } catch (Exception e) {
            logger.error("Exception: ", JSONArray.toJSON(params), e);
            result.put("code","0002");
            result.put("message","服务逻辑异常");
            return JSON.toJSONString(result);
        }
        return JSON.toJSONString(result);
    }


    /**
     * 类型
     *
     * @param params
     * @return
     */
    @PostMapping("/typeInfo")
    @CrossOrigin
    public String getTypeInfo(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = mktCampaingReportService.getTypeInfo(params);
            result.put("code","0000");
            result.put("message","成功");
        } catch (Exception e) {
            logger.error("Exception: ", JSONArray.toJSON(params), e);
            result.put("code","0002");
            result.put("message","服务逻辑异常");
            return JSON.toJSONString(result);
        }
        return JSON.toJSONString(result);
    }


    @PostMapping("/operationInfo")
    @CrossOrigin
    public String getOperationInfo(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = mktCampaingReportService.getOperationInfo(params);
            result.put("code","0000");
            result.put("message","成功");
        } catch (Exception e) {
            logger.error("Exception: ", JSONArray.toJSON(params), e);
            result.put("code","0002");
            result.put("message","服务逻辑异常");
            return JSON.toJSONString(result);
        }
        return JSON.toJSONString(result);
    }

    @PostMapping("/timeInfo")
    @CrossOrigin
    public String getTimeInfo(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = mktCampaingReportService.getTimeInfo(params);
            result.put("code","0000");
            result.put("message","成功");
        } catch (Exception e) {
            logger.error("Exception: ", JSONArray.toJSON(params), e);
            result.put("code","0002");
            result.put("message","服务逻辑异常");
            return JSON.toJSONString(result);
        }
        return JSON.toJSONString(result);
    }

    @PostMapping("/regionInfo")
    @CrossOrigin
    public String getRegionInfo(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = mktCampaingReportService.getRegionInfo(params);
            result.put("code","0000");
            result.put("message","成功");
        } catch (Exception e) {
            logger.error("Exception: ", JSONArray.toJSON(params), e);
            result.put("code","0002");
            result.put("message","服务逻辑异常");
            return JSON.toJSONString(result);
        }
        return JSON.toJSONString(result);
    }
}