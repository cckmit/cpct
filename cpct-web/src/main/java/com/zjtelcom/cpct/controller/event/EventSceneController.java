package com.zjtelcom.cpct.controller.event;

import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.event.DO.EventSceneDO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.event.EventSceneService;
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
    private EventSceneService eventSceneService;

    /**
     * 查询事件场景列表
     */
    @RequestMapping("/listEventScenes")
    @CrossOrigin
    public String listEventScenes(EventSceneDO eveneSceneDO) {
        List<EventSceneDO> eventSceneDTOS = new ArrayList<>();
        try {
            eventSceneDTOS = eventSceneService.listSceneEvents(eveneSceneDO);
        } catch (Exception e) {
            logger.error("[op:EventSceneController] fail to listEventScenes for eveneSceneDO = {}! Exception: ", JSONArray.toJSON(eveneSceneDO), e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_SCENE_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_SCENE_LIST_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(eventSceneDTOS);
    }

    /**
     * 新增事件场景列表
     */
    @RequestMapping("/saveEventScenes")
    @CrossOrigin
    public String saveEventScenes(EventSceneDO eveneSceneDO) {
        try {
            eventSceneService.saveEventScene(eveneSceneDO);
        } catch (Exception e) {
            logger.error("[op:EventSceneController] fail to saveEventScenes for eveneSceneDO = {}! Exception: ", JSONArray.toJSON(eveneSceneDO), e);
            return initFailRespInfo(ErrorCode.SAVE_EVENT_SCENE_LIST_FAILURE.getErrorMsg(), ErrorCode.SAVE_EVENT_SCENE_LIST_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(null);
    }

    /**
     * 编辑事件场景
     */
    @RequestMapping("/editEventScene")
    @CrossOrigin
    public String editEventScene(@Param("eventSceneId") Long eventSceneId) {
        EventSceneDO eventSceneDO = new EventSceneDO();
        try {
            eventSceneDO = eventSceneService.editEventScene(eventSceneId);
        } catch (Exception e) {
            logger.error("[op:EventSceneController] fail to editEventScene for eventSceneId = {}! Exception: ", eventSceneId, e);
            return initFailRespInfo(ErrorCode.EDIT_EVENT_SCENE_LIST_FAILURE.getErrorMsg(), ErrorCode.EDIT_EVENT_SCENE_LIST_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(eventSceneDO);
    }

    /**
     * 更新事件场景
     */
    @RequestMapping("/updateEventScene")
    @CrossOrigin
    public String updateEventScene(EventSceneDO eveneSceneDO) {
        try {
            eventSceneService.updateEventScene(eveneSceneDO);
        } catch (Exception e) {
            logger.error("[op:EventSceneController] fail to editEventScene for eveneSceneDO = {}! Exception: ", JSONArray.toJSON(eveneSceneDO), e);
            return initFailRespInfo(ErrorCode.UPDATE_EVENT_SCENE_LIST_FAILURE.getErrorMsg(), ErrorCode.UPDATE_EVENT_SCENE_LIST_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(null);
    }

}
