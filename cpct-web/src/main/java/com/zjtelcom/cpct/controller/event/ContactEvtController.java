package com.zjtelcom.cpct.controller.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.request.event.ContactEvtReq;
import com.zjtelcom.cpct.request.event.CreateContactEvtJtReq;
import com.zjtelcom.cpct.service.event.ContactEvtService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 事件controller
 * @Author pengy
 * @Date 2018/6/20 17:55
 */
@RestController
@RequestMapping("${adminPath}/contactEvt")
public class ContactEvtController extends BaseController {

    @Autowired
    private ContactEvtService contactEvtService;

    /**
     * 查询事件列表
     */
    @RequestMapping("/listEvents")
    @CrossOrigin
    public String listEvents(@RequestBody ContactEvtReq contactEvtReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtService.listEvents(contactEvtReq.getContactEvt(), contactEvtReq.getPage());
        } catch (Exception e) {
            logger.error("[op:EventController] fail to listEvents for contactEvtReq = {}! Exception: ", JSONArray.toJSON(contactEvtReq), e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 删除事件
     */
    @RequestMapping("/delEvent")
    @CrossOrigin
    public String delEvent(@RequestBody ContactEvtReq contactEvtReq) {
        try {
            contactEvtService.delEvent(contactEvtReq.getContactEvt().getContactEvtId());
        } catch (Exception e) {
            logger.error("[op:EventController] fail to delEvent for contactEvtReq = {}! Exception: ", JSONArray.toJSON(contactEvtReq), e);
            return initFailRespInfo(ErrorCode.DEL_EVENT_FAILURE.getErrorMsg(), ErrorCode.DEL_EVENT_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(null);
    }

    /**
     * 新增事件
     */
    @RequestMapping("/createContactEvtJt")
    @CrossOrigin
    public String createContactEvtJt(@RequestBody CreateContactEvtJtReq createContactEvtJtReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            contactEvtService.createContactEvtJt(createContactEvtJtReq);
        } catch (Exception e) {
            logger.error("[op:EventController] fail to createContactEvtJt for createContactEvtJtReq = {}! Exception: ", JSONArray.toJSON(createContactEvtJtReq), e);
            return initFailRespInfo(ErrorCode.SAVE_EVENT_FAILURE.getErrorMsg(), ErrorCode.SAVE_EVENT_FAILURE.getErrorCode());
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 开启/关闭事件
     */
    @RequestMapping("/closeEvent")
    @CrossOrigin
    public String closeEvent(@RequestBody ContactEvtReq contactEvtReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtService.closeEvent(contactEvtReq.getContactEvt().getContactEvtId(), contactEvtReq.getContactEvt().getStatusCd());
        } catch (Exception e) {
            logger.error("[op:EventController] fail to closeEvent for contactEvtReq = {}! Exception: ", JSONArray.toJSON(contactEvtReq), e);
            return initFailRespInfo(ErrorCode.CLOSE_EVENT_FAILURE.getErrorMsg(), ErrorCode.CLOSE_EVENT_FAILURE.getErrorCode());
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 查看事件
     */
    @RequestMapping("/editEvent")
    @CrossOrigin
    public String editEvent(@RequestBody ContactEvtReq contactEvtReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtService.editEvent(contactEvtReq.getContactEvt().getContactEvtId());
        } catch (Exception e) {
            logger.error("[op:EventController] fail to editEvent for contactEvtReq = {}! Exception: ",  JSONArray.toJSON(contactEvtReq), e);
            return initFailRespInfo(ErrorCode.EDIT_EVENT_FAILURE.getErrorMsg(), ErrorCode.EDIT_EVENT_FAILURE.getErrorCode());
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 修改事件
     */
    @RequestMapping("/modContactEvtJt")
    @CrossOrigin
    public String modContactEvtJt(@RequestBody CreateContactEvtJtReq createContactEvtJtReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtService.modContactEvtJt(createContactEvtJtReq);
        } catch (Exception e) {
            logger.error("[op:EventController] fail to updateEvent for createContactEvtJtReq = {}! Exception: ", JSONArray.toJSON(createContactEvtJtReq), e);
            return initFailRespInfo(ErrorCode.UPDATE_EVENT_FAILURE.getErrorMsg(), ErrorCode.UPDATE_EVENT_FAILURE.getErrorCode());
        }
        return JSON.toJSONString(maps);
    }

}
