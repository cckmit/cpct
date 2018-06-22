package com.zjtelcom.cpct.controller.event;

import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.event.DTO.EventTypeDTO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.event.EventTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description EventTypeController
 * @Author pengy
 * @Date 2018/6/20 17:55
 */
@RestController
@RequestMapping("${adminPath}/eventType")
public class EventTypeController extends BaseController {

    @Autowired
    private EventTypeService eventTypeService;

    /**
     * query eventType list
     */
    @RequestMapping("/listEventTypes")
    @CrossOrigin
    public String listEventTypes() {
        List<EventTypeDTO> eventTypeDTOS = new ArrayList<>();
        try {
            eventTypeDTOS = eventTypeService.listEventTypes();
        } catch (Exception e) {
            logger.error("[op:EventTypeController] fail to listEventTypes ! Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENTTYPE_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENTTYPE_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(eventTypeDTOS);
    }

}
