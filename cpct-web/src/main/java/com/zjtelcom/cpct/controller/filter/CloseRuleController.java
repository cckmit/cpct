package com.zjtelcom.cpct.controller.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.filter.CloseRule;
import com.zjtelcom.cpct.dto.filter.CloseRuleAddVO;
import com.zjtelcom.cpct.request.filter.CloseRuleReq;
import com.zjtelcom.cpct.service.filter.CloseRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}/filter")
public class CloseRuleController extends BaseController {

    @Autowired
    private CloseRuleService closeRuleService;


    /**
     * 通过过滤标签集合获取标签列表
     *
     * @param params
     * @return
     */
    @RequestMapping("/getQryFilterRuleByIdList")
    @CrossOrigin
    public String qryFilterRuleByIdList(@RequestBody Map<String, Object> params) {
        Map<String, Object> closeRuleListMap = new HashMap<>();
        List<Integer> closeRuleIdList = (List<Integer>) params.get("filterRuleIdList");
        try {
            closeRuleListMap = closeRuleService.getFilterRule(closeRuleIdList);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to get filterRuleIdList by filterRuleIdList = {}! Exception: ", JSONArray.toJSON(closeRuleIdList), e);
            return JSON.toJSONString(closeRuleListMap);
        }
        return JSON.toJSONString(closeRuleListMap);
    }

    /**;
     * 查询过滤规则列表(含分页)
     */
    @RequestMapping("/getQryFilterRule")
    @CrossOrigin
    public String qryFilterRule(@RequestBody CloseRuleReq closeRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = closeRuleService.qryFilterRule(closeRuleReq);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(closeRuleReq), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 查询过滤规则列表(不含分页)
     */
    @RequestMapping("/getQryFilterRules")
    @CrossOrigin
    public String qryFilterRules(@RequestBody CloseRuleReq closeRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = closeRuleService.qryFilterRules(closeRuleReq);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(closeRuleReq), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 删除过滤规则
     */
    @RequestMapping("/delByFilterRule")
    @CrossOrigin
    public String delFilterRule(@RequestBody CloseRule closeRule) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = closeRuleService.delFilterRule(closeRule);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(closeRule), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 查询单个过滤规则
     */
    @RequestMapping("/getByFilterRule")
    @CrossOrigin
    public String getFilterRule(@RequestBody CloseRule closeRule) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = closeRuleService.getFilterRule(closeRule.getRuleId());
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(closeRule), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 新增过滤规则
     */
    @RequestMapping("/insertFilterRule")
    @CrossOrigin
    public String createFilterRule(@RequestBody CloseRuleAddVO closeRule) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = closeRuleService.createFilterRule(closeRule);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(closeRule), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 修改过滤规则
     */
    @RequestMapping("/modByFilterRule")
    @CrossOrigin
    public String modFilterRule(@RequestBody CloseRuleAddVO closeRule) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = closeRuleService.modFilterRule(closeRule);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(closeRule), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }
}
