package com.zjtelcom.cpct.controller.event;

import com.zjtelcom.cpct.controller.BaseController;
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
@RequestMapping("${adminPath}/event")
public class EventController extends BaseController {

    @Autowired
    private EventService eventService;

    /**
     * query event list
     */
    @RequestMapping("/listEvents")
    @CrossOrigin
    public String listEvents(@Param("evtSrcId") Long evtSrcId, @Param("eventName") String eventName) {
        List<EventList> eventLists = new ArrayList<>();
        try {
            eventLists = eventService.listEvents(evtSrcId, eventName);
        } catch (Exception e) {
            logger.error("[op:EventController] fail to listEvents for evtSrcId = {},eventName = {}! Exception: ", evtSrcId, eventName, e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(eventLists);
    }

    /**
     * event delete
     */
    @RequestMapping("/delEvent")
    @CrossOrigin
    public String delEvent(@Param("eventId") Long eventId) {

        return null;
    }

    /**
     * event add
     */
    @RequestMapping("/addEvent")
    @CrossOrigin
    public String addEvent(@Param("eventId") Long eventId) {

        return null;
    }

}
