/**
 * @(#)EventSorceController.java, 2018/8/20.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.controller.event;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.event.EventSorce;
import com.zjtelcom.cpct.service.event.EventSorceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/08/20 17:28
 * version: V1.0
 */
@RestController
@RequestMapping("${adminPath}/eventSorce")
public class EventSorceController extends BaseController {

    @Autowired
    private EventSorceService eventSorceService;


    /**
     * 新增事件源
     *
     * @param eventSorce
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/saveEventSorce", method = RequestMethod.POST)
    public String saveEventSorce(@RequestBody EventSorce eventSorce) {
        Map<String, Object> eventSorceMap = eventSorceService.saveEventSorce(eventSorce);
        return JSON.toJSONString(eventSorceMap);
    }

    /**
     * 查询事件源
     *
     * @param params
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/getEventSorce", method = RequestMethod.POST)
    public String getEventSorce(@RequestBody Map<String, String> params) {
        Long evtSrcId = Long.valueOf( params.get("evtSrcId"));
        Map<String, Object> eventSorceMap = eventSorceService.getEventSorce(evtSrcId);
        return JSON.toJSONString(eventSorceMap);
    }


    /**
     * 更新事件源
     *
     * @param eventSorce
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/updateEventSorce", method = RequestMethod.POST)
    public String updateEventSorce(@RequestBody EventSorce eventSorce) {
        Map<String, Object> eventSorceMap = eventSorceService.updateEventSorce(eventSorce);
        return JSON.toJSONString(eventSorceMap);
    }

    /**
     * 删除事件源
     *
     * @param params
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/deleteEventSorce", method = RequestMethod.POST)
    public String deleteEventSorce(@RequestBody Map<String, String> params) {
        Long evtSrcId = Long.valueOf(params.get("evtSrcId"));
        Map<String, Object> eventSorceMap = eventSorceService.deleteEventSorce(evtSrcId);
        return JSON.toJSONString(eventSorceMap);
    }


    /**
     * 分页查询事件源列表
     *
     * @param params
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/listEventSorcePage", method = RequestMethod.POST)
    public String listEventSorcePage(@RequestBody Map<String, Object> params) {
            String evtSrcCode = (String) params.get("evtSrcCode");
        String evtSrcName = (String) params.get("evtSrcName");
        Integer page = (Integer) params.get("page");
        Integer pageSize = (Integer) params.get("pageSize");
        Map<String, Object> eventSorceMap = eventSorceService.listEventSorcePage(evtSrcCode, evtSrcName, page, pageSize);
        return JSON.toJSONString(eventSorceMap);
    }

    /**
     * 查询所有事件源列表
     *
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/listEventSorceAll", method = RequestMethod.POST)
    public String listEventSorceAll() {
        Map<String, Object> eventSorceMap = eventSorceService.listEventSorceAll();
        return JSON.toJSONString(eventSorceMap);
    }


}