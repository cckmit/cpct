package com.zjtelcom.cpct.controller.event;

import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.event.EventMatchRulCondition;
import com.zjtelcom.cpct.dto.event.EventMatchRulDTO;
import com.zjtelcom.cpct.dto.event.EventMatchRulDetail;
import com.zjtelcom.cpct.service.event.EventMatchRulService;
import com.zjtelcom.cpct.util.FastJsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}/EventMatchRul")
public class EventMatchRulController extends BaseController {

    @Autowired
    EventMatchRulService eventMatchRulService;

    /**
     * 新增事件规则
     */
    @RequestMapping("/createEventMatchRul")
    @CrossOrigin
    public String createEventMatchRul(@RequestBody EventMatchRulDetail eventMatchRulDetail) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = eventMatchRulService.createEventMatchRul(eventMatchRulDetail);
        } catch (Exception e) {
            logger.error("[op:EventMatchRulController] fail to createEventMatchRul for eventMatchRulDetail = {}!" +
                    " Exception: ", JSONArray.toJSON(eventMatchRulDetail), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }

    /**
     * 修改事件规则
     */
    @RequestMapping("/modEventMatchRul")
    @CrossOrigin
    public String modEventMatchRul(@RequestBody EventMatchRulDetail eventMatchRulDetail) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = eventMatchRulService.modEventMatchRul(eventMatchRulDetail);
        } catch (Exception e) {
            logger.error("[op:EventMatchRulController] fail to modEventMatchRul for eventMatchRulDetail = {}!" +
                    " Exception: ", JSONArray.toJSON(eventMatchRulDetail), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }

    /**
     * 删除事件规则
     */
    @RequestMapping("/delEventMatchRul")
    @CrossOrigin
    public String delEventMatchRul(@RequestBody EventMatchRulDetail eventMatchRulDetail) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = eventMatchRulService.delEventMatchRul(eventMatchRulDetail);
        } catch (Exception e) {
            logger.error("[op:EventMatchRulController] fail to delEventMatchRul for eventMatchRulDetail = {}!" +
                    " Exception: ", JSONArray.toJSON(eventMatchRulDetail), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }

    /**
     * 删除事件规则条件
     */
    @RequestMapping("/delEventMatchRulCondition")
    @CrossOrigin
    public String delEventMatchRulCondition(Long conditionId) {
        Map<String, Object> maps = new HashMap<>();
        EventMatchRulCondition eventMatchRulCondition = new EventMatchRulCondition();
        eventMatchRulCondition.setConditionId(conditionId);
        try {
            maps = eventMatchRulService.delEventMatchRulCondition(eventMatchRulCondition);
        } catch (Exception e) {
            logger.error("[op:EventMatchRulController] fail to delEventMatchRul for eventMatchRulDetail = {}!" +
                    " Exception: ", JSONArray.toJSON(eventMatchRulCondition), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }

    /**
     * 获取事件规则条件信息
     */
    @RequestMapping("/listEventMatchRulCondition")
    @CrossOrigin
    public String listEventMatchRulCondition(@RequestBody EventMatchRulDTO eventMatchRulDTO) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = eventMatchRulService.listEventMatchRulCondition(eventMatchRulDTO.getEvtMatchRulId());
        } catch (Exception e) {
            logger.error("[op:EventMatchRulController] fail to listEventMatchRulCondition for evtMatchRulId = {}!" +
                    " Exception: ", JSONArray.toJSON(eventMatchRulDTO), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }
}
