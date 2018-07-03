package com.zjtelcom.cpct.controller.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.event.EventSceneDO;
import com.zjtelcom.cpct.dto.event.EventScene;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.request.event.CreateEventSceneReq;
import com.zjtelcom.cpct.request.event.ModEventSceneReq;
import com.zjtelcom.cpct.request.event.QryEventSceneListReq;
import com.zjtelcom.cpct.response.event.QryeventSceneRsp;
import com.zjtelcom.cpct.service.event.EventSceneService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 事件场景controller
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
    @RequestMapping("/qryEventSceneList")
    @CrossOrigin
    public String qryEventSceneList(@RequestBody QryEventSceneListReq qryEventSceneListReq) {
        QryeventSceneRsp qryeventSceneRsp = new QryeventSceneRsp();
        try {
            qryeventSceneRsp = eventSceneService.qryEventSceneList(qryEventSceneListReq);
        } catch (Exception e) {
            logger.error("[op:EventSceneController] fail to listEventScenes for qryEventSceneListReq = {}! Exception: ", JSONArray.toJSON(qryEventSceneListReq), e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_SCENE_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_SCENE_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSONString(qryeventSceneRsp);
    }

    /**
     * 新增事件场景
     */
    @RequestMapping("/createEventScene")
    @CrossOrigin
    public String createEventScene(@RequestBody CreateEventSceneReq createEventSceneReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = eventSceneService.createEventScene(createEventSceneReq);
        } catch (Exception e) {
            logger.error("[op:EventSceneController] fail to saveEventScenes for createEventSceneReq = {}! Exception: ", JSONArray.toJSON(createEventSceneReq), e);
            return initFailRespInfo(ErrorCode.SAVE_EVENT_SCENE_LIST_FAILURE.getErrorMsg(), ErrorCode.SAVE_EVENT_SCENE_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 查看事件场景
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
     * 修改事件场景
     */
    @RequestMapping("/modEventScene")
    @CrossOrigin
    public String modEventScene(@RequestBody ModEventSceneReq modEventSceneReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = eventSceneService.modEventScene(modEventSceneReq);
        } catch (Exception e) {
            logger.error("[op:EventSceneController] fail to editEventScene for modEventSceneReq = {}! Exception: ", JSONArray.toJSON(modEventSceneReq), e);
            return initFailRespInfo(ErrorCode.UPDATE_EVENT_SCENE_LIST_FAILURE.getErrorMsg(), ErrorCode.UPDATE_EVENT_SCENE_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 删除事件场景
     */
    @RequestMapping("/delEventScene")
    @CrossOrigin
    public String delEventScene(@RequestBody EventScene eventScene) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = eventSceneService.delEventScene(eventScene);
        } catch (Exception e) {
            logger.error("[op:EventSceneController] fail to editEventScene for modEventSceneReq = {}! Exception: ", JSONArray.toJSON(eventScene), e);
            return initFailRespInfo(ErrorCode.UPDATE_EVENT_SCENE_LIST_FAILURE.getErrorMsg(), ErrorCode.UPDATE_EVENT_SCENE_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSONString(maps);
    }

}
