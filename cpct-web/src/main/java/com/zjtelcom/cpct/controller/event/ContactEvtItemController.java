package com.zjtelcom.cpct.controller.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.ContactEvtItem;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.request.event.ContactEvtReq;
import com.zjtelcom.cpct.service.event.ContactEvtItemService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 事件采集项controller
 * @Author pengy
 * @Date 2018/7/1 22:35
 **/
@RestController
@RequestMapping("${adminPath}/eventItem")
public class ContactEvtItemController extends BaseController {

    @Autowired
    private ContactEvtItemService contactEvtItemService;

    /**
     * 查询事件采集项
     */
    @RequestMapping("/listEventItem")
    @CrossOrigin
    public String listEventItem(@RequestBody ContactEvtReq contactEvtReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtItemService.listEventItem(contactEvtReq);
        } catch (Exception e) {
            logger.error("[op:ContactEvtItemController] fail to listEventItem for contactEvtReq = {}! Exception: ", JSONArray.toJSON(contactEvtReq), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 删除事件采集项
     */
    @RequestMapping("/delEventItem")
    @CrossOrigin
    public String delEventItem(@RequestBody ContactEvtItem contactEvtItem) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtItemService.delEventItem(contactEvtItem);
        } catch (Exception e) {
            logger.error("[op:ContactEvtItemController] fail to delEventItem for contactEvtItem = {}! Exception: ", JSONArray.toJSON(contactEvtItem), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 查看事件采集项
     */
    @RequestMapping("/viewEventItem")
    @CrossOrigin
    public String viewEventItem(@RequestBody ContactEvtItem contactEvtItem) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtItemService.viewEventItem(contactEvtItem);
        } catch (Exception e) {
            logger.error("[op:ContactEvtItemController] fail to viewEventItem for contactEvtItem = {}! Exception: ", JSONArray.toJSON(contactEvtItem), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 新增事件采集项
     */
    @RequestMapping("/createEventItem")
    @CrossOrigin
    public String createEventItem(@RequestBody ContactEvtItem contactEvtItem) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtItemService.createEventItem(contactEvtItem);
        } catch (Exception e) {
            logger.error("[op:ContactEvtItemController] fail to createEventItem for contactEvtItem = {}! Exception: ", JSONArray.toJSON(contactEvtItem), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 修改事件采集项
     */
    @RequestMapping("/modEventItem")
    @CrossOrigin
    public String modEventItem(@RequestBody ContactEvtItem contactEvtItem) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtItemService.modEventItem(contactEvtItem);
        } catch (Exception e) {
            logger.error("[op:ContactEvtItemController] fail to modEventItem for contactEvtItem = {}! Exception: ", JSONArray.toJSON(contactEvtItem), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

}
