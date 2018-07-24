package com.zjtelcom.cpct.controller.event;

import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.event.EventSceneTypeDO;
import com.zjtelcom.cpct.dto.event.EventSceneTypeDTO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.event.EventSceneTypeService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
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
    private EventSceneTypeService eventSceneTypeService;

    /**
     * 查询事件场景目录列表
     */
    @PostMapping("/listEventSceneTypes")
    @CrossOrigin
    public String listEventSceneTypes() {
        List<EventSceneTypeDTO> eventTypeDTOS = new ArrayList<>();
        try {
            eventTypeDTOS = eventSceneTypeService.listEventSceneTypes();
        } catch (Exception e) {
            logger.error("[op:EventTypeController] fail to listEventSceneTypes ! Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENTTYPE_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENTTYPE_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(eventTypeDTOS);
    }

    /**
     * 新增事件场景目录保存
     */
    @PostMapping("/saveEventSceneTypes")
    @CrossOrigin
    public String saveEventSceneTypes(@RequestBody EventSceneTypeDO eventSceneTypeDO) {
        try {
            eventSceneTypeService.saveEventSceneType(eventSceneTypeDO);
        } catch (Exception e) {
            logger.error("[op:EventTypeController] fail to saveEventTypes eventTypeDO = {}! Exception: ", JSONArray.toJSON(eventSceneTypeDO), e);
            return initFailRespInfo(ErrorCode.SAVE_EVENTTYPE_FAILURE.getErrorMsg(), ErrorCode.SAVE_EVENTTYPE_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(null);
    }

    /**
     * 编辑事件场景目录
     */
    @PostMapping("/editEventSceneType")
    @CrossOrigin
    public String editEventSceneType(@RequestBody HashMap<String,Long> param) {
        EventSceneTypeDTO eventTypeDTO = new EventSceneTypeDTO();
        Long evtSceneTypeId = param.get("evtSceneTypeId");
        try {
            eventTypeDTO = eventSceneTypeService.getEventSceneTypeDTOById(evtSceneTypeId);
        } catch (Exception e) {
            logger.error("[op:EventTypeController] fail to editEventSceneType evtTypeId = {}! Exception: ", evtSceneTypeId, e);
            return initFailRespInfo(ErrorCode.EDIT_EVENTTYPE_FAILURE.getErrorMsg(), ErrorCode.EDIT_EVENTTYPE_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(eventTypeDTO);
    }

    /**
     * 编辑事件场景目录保存
     */
    @PostMapping("/updateEventSceneType")
    @CrossOrigin
    public String updateEventSceneType(@RequestBody EventSceneTypeDO eventTypeDO) {
        try {
            eventSceneTypeService.updateEventSceneType(eventTypeDO);
        } catch (Exception e) {
            logger.error("[op:EventTypeController] fail to updateEventSceneType eventTypeDO = {}! Exception: ", JSONArray.toJSON(eventTypeDO), e);
            return initFailRespInfo(ErrorCode.UPDATE_EVENTTYPE_FAILURE.getErrorMsg(), ErrorCode.UPDATE_EVENTTYPE_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(null);
    }

    /**
     * 删除事件场景目录
     */
    @PostMapping("/delEventSceneType")
    @CrossOrigin
    public String delEventSceneType(@RequestBody HashMap<String,Long> param) {
        Long evtSceneTypeId = param.get("evtSceneTypeId");
        try {
            eventSceneTypeService.delEventSceneType(evtSceneTypeId);
        } catch (Exception e) {
            logger.error("[op:EventTypeController] fail to delEventType evtTypeId = {}! Exception: ", evtSceneTypeId, e);
            return initFailRespInfo(ErrorCode.DEL_EVENTTYPE_FAILURE.getErrorMsg(), ErrorCode.DEL_EVENTTYPE_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(null);
    }

}
