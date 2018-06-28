package com.zjtelcom.cpct.controller.event;

import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.event.EventTypeDO;
import com.zjtelcom.cpct.dto.event.EventTypeDTO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.event.EventTypeService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 事件场景目录controller
 * @Author pengy
 * @Date 2018/6/28 12:35
 */
@RestController
@RequestMapping("${adminPath}/eventSceneType")
public class EventSceneTypeController extends BaseController {

    @Autowired
    private EventTypeService eventTypeService;

    /**
     * 查询事件场景目录列表
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

    /**
     * 新增事件场景目录保存
     */
    @RequestMapping("/saveEventTypes")
    @CrossOrigin
    public String saveEventTypes(EventTypeDO eventTypeDO) {
        try {
            eventTypeService.saveEventType(eventTypeDO);
        } catch (Exception e) {
            logger.error("[op:EventTypeController] fail to saveEventTypes eventTypeDO = {}! Exception: ", JSONArray.toJSON(eventTypeDO), e);
            return initFailRespInfo(ErrorCode.SAVE_EVENTTYPE_FAILURE.getErrorMsg(), ErrorCode.SAVE_EVENTTYPE_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(null);
    }

    /**
     * 编辑事件场景目录
     */
    @RequestMapping("/editEventType")
    @CrossOrigin
    public String editEventType(@Param("evtTypeId") Long evtTypeId) {
        EventTypeDTO eventTypeDTO = new EventTypeDTO();
        try {
            eventTypeDTO = eventTypeService.getEventTypeDTOById(evtTypeId);
        } catch (Exception e) {
            logger.error("[op:EventTypeController] fail to editEventTypes evtTypeId = {}! Exception: ", evtTypeId, e);
            return initFailRespInfo(ErrorCode.EDIT_EVENTTYPE_FAILURE.getErrorMsg(), ErrorCode.EDIT_EVENTTYPE_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(eventTypeDTO);
    }

    /**
     * 编辑事件场景目录保存
     */
    @RequestMapping("/updateEventType")
    @CrossOrigin
    public String updateEventType(EventTypeDO eventTypeDO) {
        try {
            eventTypeService.updateEventType(eventTypeDO);
        } catch (Exception e) {
            logger.error("[op:EventTypeController] fail to updateEventType eventTypeDO = {}! Exception: ", JSONArray.toJSON(eventTypeDO), e);
            return initFailRespInfo(ErrorCode.UPDATE_EVENTTYPE_FAILURE.getErrorMsg(), ErrorCode.UPDATE_EVENTTYPE_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(null);
    }

    /**
     * 删除事件场景目录
     */
    @RequestMapping("/delEventType")
    @CrossOrigin
    public String delEventType(@Param("evtTypeId") Long evtTypeId) {
        try {
            eventTypeService.delEventType(evtTypeId);
        } catch (Exception e) {
            logger.error("[op:EventTypeController] fail to delEventType evtTypeId = {}! Exception: ", evtTypeId, e);
            return initFailRespInfo(ErrorCode.DEL_EVENTTYPE_FAILURE.getErrorMsg(), ErrorCode.DEL_EVENTTYPE_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(null);
    }

}
