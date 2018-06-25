package com.zjtelcom.cpct.controller.event;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.event.EventMatchRulDTO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.event.EventMatchRulService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description EventMatchRulController
 * @Author pengy
 * @Date 2018/6/22 9:44
 */

@RestController
@RequestMapping("${adminPath}/eventMatchRul")
public class EventMatchRulController extends BaseController {

    @Autowired
    private EventMatchRulService eventMatchRulService;

    /**
     * query eventMatchRul list
     */
    @RequestMapping("/listEventMatchRuls")
    @CrossOrigin
    public String listEventMatchRul(@Param("evtRulName") String evtRulName) {
        List<EventMatchRulDTO> eventMatchRulDTOS = new ArrayList<>();
        try {
            eventMatchRulDTOS = eventMatchRulService.listEventMatchRuls(evtRulName);
        } catch (Exception e) {
            logger.error("[op:EventMatchRulController] fail to listEventMatchRuls for evtRulName = {}! Exception: ", evtRulName, e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENTMATCHRUL_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENTMATCHRUL_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(eventMatchRulDTOS);
    }



}
