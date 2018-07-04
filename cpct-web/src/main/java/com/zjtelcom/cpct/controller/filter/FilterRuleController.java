package com.zjtelcom.cpct.controller.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.request.filter.FilterRuleReq;
import com.zjtelcom.cpct.service.filter.FilterRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 规律规则controller
 * @Author pengy
 * @Date 2018/7/3 10:27
 */
@RestController
@RequestMapping("${adminPath}/filter")
public class FilterRuleController extends BaseController {

    @Autowired
    private FilterRuleService filterRuleService;

    /**
     * 查询过滤规则列表
     */
    @RequestMapping("/qryFilterRule")
    @CrossOrigin
    public String qryFilterRule(@RequestBody FilterRuleReq filterRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = filterRuleService.qryFilterRule(filterRuleReq);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(filterRuleReq), e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 删除过滤规则
     */
        @RequestMapping("/delFilterRule")
    @CrossOrigin
    public String delFilterRule(@RequestBody FilterRule filterRule) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = filterRuleService.delFilterRule(filterRule);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(filterRule), e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 查询单个过滤规则
     */
    @RequestMapping("/getFilterRule")
    @CrossOrigin
    public String getFilterRule(@RequestBody FilterRule filterRule) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = filterRuleService.getFilterRule(filterRule);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(filterRule), e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 新增过滤规则
     */
    @RequestMapping("/createFilterRule")
    @CrossOrigin
    public String createFilterRule(@RequestBody FilterRule filterRule) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = filterRuleService.createFilterRule(filterRule);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(filterRule), e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 修改过滤规则
     */
    @RequestMapping("/modFilterRule")
    @CrossOrigin
    public String modFilterRule(@RequestBody FilterRule filterRule) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = filterRuleService.modFilterRule(filterRule);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(filterRule), e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSONString(maps);
    }

}
