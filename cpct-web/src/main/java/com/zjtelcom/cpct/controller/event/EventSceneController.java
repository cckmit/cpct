package com.zjtelcom.cpct.controller.event;

import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.event.DTO.EventDTO;
import com.zjtelcom.cpct.domain.event.EventList;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.event.EventService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description eventController
 * @Author pengy
 * @Date 2018/6/20 17:55
 */
@RestController
@RequestMapping("${adminPath}/eventScene")
public class EventSceneController extends BaseController {

    @Autowired
    private EventService eventService;

    /**
     * 查询出事件场景列表
     */
    @RequestMapping("/listEventScenes")
    @CrossOrigin
    public String listEventScenes(@Param("evtSrcId") Long evtSrcId, @Param("eventName") String eventName) {
        List<EventList> eventLists = new ArrayList<>();
        try {
            eventLists = eventService.listEvents(evtSrcId, eventName);
        } catch (Exception e) {
            logger.error("[op:EventController] fail to listEvents for evtSrcId = {},eventName = {}! Exception: ", evtSrcId, eventName, e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(eventLists);
    }

}
