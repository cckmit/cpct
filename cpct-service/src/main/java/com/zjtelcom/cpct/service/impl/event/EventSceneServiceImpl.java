package com.zjtelcom.cpct.service.impl.event;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.event.EventSceneMapper;
import com.zjtelcom.cpct.domain.event.EventSceneDO;
import com.zjtelcom.cpct.dto.event.EventScene;
import com.zjtelcom.cpct.dto.event.EventSceneDetail;
import com.zjtelcom.cpct.request.event.*;
import com.zjtelcom.cpct.response.event.QryeventSceneRsp;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.event.EventSceneService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description EventSceneServiceImpl
 * @Author pengy
 * @Date 2018/6/21 9:46
 */
@Service
@Transactional
public class EventSceneServiceImpl extends BaseService implements EventSceneService {

    @Autowired
    private EventSceneMapper eventSceneMapper;

    /**
     * 查询事件场景列表
     */
    @Transactional(readOnly = true)
    @Override
    public QryeventSceneRsp qryEventSceneList(QryEventSceneListReq qryEventSceneListReq) {
        List<EventScene> eventScenes = eventSceneMapper.qryEventSceneList(qryEventSceneListReq);
        QryeventSceneRsp qryeventSceneRsp = new QryeventSceneRsp();
        qryeventSceneRsp.setEventScenes(eventScenes);
        return qryeventSceneRsp;
    }

    /**
     * 新增事件场景
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> createEventScene(CreateEventSceneReq createEventSceneReq) {
        Map<String, Object> maps = new HashMap<>();
        List<EventSceneDetail> eventSceneDetails = new ArrayList<>();
        eventSceneDetails = createEventSceneReq.getEventSceneDetails();
        if (eventSceneDetails != null) {
            for (EventSceneDetail eventSceneDetail : eventSceneDetails) {
                EventScene eventScene = eventSceneDetail;
                eventScene.setCreateDate(DateUtil.getCurrentTime());
                eventScene.setStatusDate(DateUtil.getCurrentTime());
                eventScene.setUpdateStaff(UserUtil.loginId());
                eventScene.setCreateStaff(UserUtil.loginId());
                eventScene.setUpdateDate(DateUtil.getCurrentTime());
                eventScene.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                eventSceneMapper.createEventScene(eventScene);
            }
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 新增事件场景（集团）
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> createEventSceneJt(CreateEventSceneJtReq createEventSceneJtReq) {
        Map<String, Object> mapT = new HashMap<>();
        List<EventSceneDetail> eventSceneDetails = new ArrayList<>();
        eventSceneDetails = createEventSceneJtReq.getEventSceneDetails();
        if (eventSceneDetails != null) {
            for (EventSceneDetail eventSceneDetail : eventSceneDetails) {
                EventScene eventScene = eventSceneDetail;
                eventScene.setUpdateDate(DateUtil.getCurrentTime());
                eventScene.setStatusDate(DateUtil.getCurrentTime());
                eventScene.setUpdateStaff(UserUtil.loginId());
                eventScene.setCreateStaff(UserUtil.loginId());
                eventScene.setCreateDate(DateUtil.getCurrentTime());
                eventScene.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                eventSceneMapper.createEventScene(eventScene);
            }
        }
        mapT.put("resultCode", CommonConstant.CODE_SUCCESS);
        mapT.put("resultMsg", StringUtils.EMPTY);
        return mapT;
    }


    /**
     * 查看事件场景
     */
    @Transactional(readOnly = true)
    @Override
    public Map<String, Object> editEventScene(Long eventSceneId) {
        Map<String, Object> maps = new HashMap<>();
        EventScene eventScene = new EventScene();
        eventScene = eventSceneMapper.getEventScene(eventSceneId);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("eventScene", eventScene);
        return maps;

    }

    /**
     * 修改事件场景
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> modEventScene(ModEventSceneReq modEventSceneReq) {
        Map<String, Object> maps = new HashMap<>();
        List<EventSceneDetail> eventSceneDetails = new ArrayList<>();
        if (modEventSceneReq.getEventSceneDetails() != null) {
            eventSceneDetails = modEventSceneReq.getEventSceneDetails();
            for (EventSceneDetail eventSceneDetailT : eventSceneDetails) {
                EventScene eventScene = eventSceneDetailT;
                eventScene.setUpdateDate(DateUtil.getCurrentTime());
                eventScene.setUpdateStaff(UserUtil.loginId());
                eventSceneMapper.updateByPrimaryKey(eventScene);
            }
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 修改事件场景（集团）
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> modEventSceneJt(ModEventSceneJtReq modEventSceneJtReq) {
        Map<String, Object> maps = new HashMap<>();
        List<EventSceneDetail> eventSceneDetails = new ArrayList<>();
        if (modEventSceneJtReq.getEventSceneDetails() != null) {
            eventSceneDetails = modEventSceneJtReq.getEventSceneDetails();
            for (EventSceneDetail eventSceneDetail : eventSceneDetails) {
                EventScene eventScene = eventSceneDetail;
                eventScene.setUpdateStaff(UserUtil.loginId());
                eventScene.setUpdateDate(DateUtil.getCurrentTime());
                eventSceneMapper.updateByPrimaryKey(eventScene);
            }
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 删除事件场景
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> delEventScene(EventScene eventScene) {
        Map<String, Object> maps = new HashMap<>();
        eventSceneMapper.delEventScene(eventScene);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

}
