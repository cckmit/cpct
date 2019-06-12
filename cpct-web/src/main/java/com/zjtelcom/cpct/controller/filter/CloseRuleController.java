package com.zjtelcom.cpct.controller.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.filter.FilterRuleAddVO;
import com.zjtelcom.cpct.request.filter.FilterRuleReq;
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
        Map<String, Object> filterRuleListMap = new HashMap<>();
        List<Integer> filterRuleIdList = (List<Integer>) params.get("filterRuleIdList");
        try {
            filterRuleListMap = closeRuleService.getFilterRule(filterRuleIdList);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to get filterRuleIdList by filterRuleIdList = {}! Exception: ", JSONArray.toJSON(filterRuleIdList), e);
            return JSON.toJSONString(filterRuleListMap);
        }
        return JSON.toJSONString(filterRuleListMap);
    }

    /**;
     * 查询过滤规则列表(含分页)
     */
    @RequestMapping("/getQryFilterRule")
    @CrossOrigin
    public String qryFilterRule(@RequestBody FilterRuleReq filterRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = closeRuleService.qryFilterRule(filterRuleReq);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(filterRuleReq), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 查询过滤规则列表(不含分页)
     */
    @RequestMapping("/getQryFilterRules")
    @CrossOrigin
    public String qryFilterRules(@RequestBody FilterRuleReq filterRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = closeRuleService.qryFilterRules(filterRuleReq);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(filterRuleReq), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 删除过滤规则
     */
    @RequestMapping("/delByFilterRule")
    @CrossOrigin
    public String delFilterRule(@RequestBody FilterRule filterRule) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = closeRuleService.delFilterRule(filterRule);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(filterRule), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 查询单个过滤规则
     */
    @RequestMapping("/getByFilterRule")
    @CrossOrigin
    public String getFilterRule(@RequestBody FilterRule filterRule) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = closeRuleService.getFilterRule(filterRule.getRuleId());
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(filterRule), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 新增过滤规则
     */
    @RequestMapping("/insertFilterRule")
    @CrossOrigin
    public String createFilterRule(@RequestBody FilterRuleAddVO filterRule) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = closeRuleService.createFilterRule(filterRule);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(filterRule), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 修改过滤规则
     */
    @RequestMapping("/modByFilterRule")
    @CrossOrigin
    public String modFilterRule(@RequestBody FilterRuleAddVO filterRule) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = closeRuleService.modFilterRule(filterRule);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(filterRule), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }
}
