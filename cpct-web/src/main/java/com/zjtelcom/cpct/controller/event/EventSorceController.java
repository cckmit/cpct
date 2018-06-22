package com.zjtelcom.cpct.controller.event;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.event.EventSorce;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.event.EventSorceService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description EventSorceController
 * @Author pengy
 * @Date 2018/6/20 17:55
 */
@RestController
@RequestMapping("${adminPath}/eventSorce")
public class EventSorceController extends BaseController {

    @Autowired
    private EventSorceService eventSorceService;

    /**
     * query event list
     */
    @RequestMapping("/listEventSorce")
    @CrossOrigin
    public String listEventSorce(@Param("evtSrcCode") String evtSrcCode, @Param("evtSrcName") String evtSrcName) {
        List<EventSorce> eventSorceList = new ArrayList<>();
        try {
            eventSorceList = eventSorceService.listEventSorces(evtSrcCode, evtSrcName);
        } catch (Exception e) {
            logger.error("[op:EventSorceController] fail to listEventSorce for evtSrcCode = {},evtSrcName = {}! Exception: ", evtSrcCode, evtSrcName, e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENTSORCE_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENTSORCE_LIST_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(eventSorceList);
    }

    /**
     * delete event sorce
     */
    @RequestMapping("/delEventSorceDel")
    @CrossOrigin
    public String delEventSorceDel(@Param("evtSrcId") Long evtSrcId) {
        try {
            eventSorceService.delEventSorce(evtSrcId);
        } catch (Exception e) {
            logger.error("[op:EventSorceController] fail to delEventSorceDel for evtSrcId = {}! Exception: ", evtSrcId, e);
            return initFailRespInfo(ErrorCode.DELETE_EVENTSORCE_FAILURE.getErrorMsg(), ErrorCode.DELETE_EVENTSORCE_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(null);
    }

    /**
     * edit event sorce
     */
    @RequestMapping("/editEventSorce")
    @CrossOrigin
    public String editEventSorce(@Param("evtSrcId") Long evtSrcId) {
        EventSorce eventSorce = new EventSorce();
        try {
            eventSorce = eventSorceService.editEventSorce(evtSrcId);
        } catch (Exception e) {
            logger.error("[op:EventSorceController] fail to editEventSorce for evtSrcId = {}! Exception: ", evtSrcId, e);
            return initFailRespInfo(ErrorCode.EDIT_EVENTSORCE_FAILURE.getErrorMsg(), ErrorCode.EDIT_EVENTSORCE_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(eventSorce);
    }

    /**
     * add event sorce
     */
    @RequestMapping("/saveEventSorce")
    @CrossOrigin
    public String saveEventSorce(EventSorce eventSorce) {
        try {
            eventSorceService.saveEventSorce(eventSorce);
        } catch (Exception e) {
            logger.error("[op:EventSorceController] fail to addEventSorce for eventSorce = {}! Exception: ", JSON.toJSON(eventSorce), e);
            return initFailRespInfo(ErrorCode.ADD_EVENTSORCE_FAILURE.getErrorMsg(), ErrorCode.ADD_EVENTSORCE_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(null);
    }

    /**
     * update event sorce
     */
    @RequestMapping("/updateEventSorce")
    @CrossOrigin
    public String updateEventSorce(EventSorce eventSorce) {
        try {
            eventSorceService.updateEventSorce(eventSorce);
        } catch (Exception e) {
            logger.error("[op:EventSorceController] fail to updateEventSorce for eventSorce = {}! Exception: ", JSON.toJSON(eventSorce), e);
            return initFailRespInfo(ErrorCode.UPDATE_EVENTSORCE_FAILURE.getErrorMsg(), ErrorCode.UPDATE_EVENTSORCE_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(null);
    }

}
