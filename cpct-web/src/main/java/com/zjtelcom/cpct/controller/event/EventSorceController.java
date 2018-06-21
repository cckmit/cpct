package com.zjtelcom.cpct.controller.event;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.EventSorce;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.EventSorceService;
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
    @RequestMapping("/eventSorceList")
    @CrossOrigin
    public String eventSorceList(@Param("evtSrcCode") String evtSrcCode, @Param("evtSrcName") String evtSrcName) {
        List<EventSorce> eventSorceList = new ArrayList<>();
        try {
            eventSorceList = eventSorceService.listEventSorces(evtSrcCode, evtSrcName);
        } catch (Exception e) {
            logger.error("[op:EventSorceController] fail to eventList for evtSrcCode = {},evtSrcName = {}! Exception: ", evtSrcCode, evtSrcName, e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENTSORCE_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENTSORCE_LIST_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(eventSorceList);
    }

    /**
     * delete event sorce
     */
    @RequestMapping("/eventSorceDel")
    @CrossOrigin
    public String eventSorceDel(@Param("evtSrcId") Long evtSrcId) {
        try {
            eventSorceService.eventSorceDel(evtSrcId);
        } catch (Exception e) {
            logger.error("[op:EventSorceController] fail to eventSorceDel for evtSrcId = {}! Exception: ", evtSrcId, e);
            return initFailRespInfo(ErrorCode.DELETE_EVENTSORCE_FAILURE.getErrorMsg(), ErrorCode.DELETE_EVENTSORCE_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(null);
    }

}
