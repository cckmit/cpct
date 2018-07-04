package com.zjtelcom.cpct.service.impl.event;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.event.EventSceneMapper;
import com.zjtelcom.cpct.domain.event.EventSceneDO;
import com.zjtelcom.cpct.dto.event.EventScene;
import com.zjtelcom.cpct.dto.event.EventSceneDetail;
import com.zjtelcom.cpct.request.event.CreateEventSceneReq;
import com.zjtelcom.cpct.request.event.ModEventSceneReq;
import com.zjtelcom.cpct.request.event.QryEventSceneListReq;
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
                eventScene.setUpdateDate(DateUtil.getCurrentTime());
                eventScene.setStatusDate(DateUtil.getCurrentTime());
                eventScene.setUpdateStaff(UserUtil.loginId());
                eventScene.setCreateStaff(UserUtil.loginId());
                eventScene.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                eventSceneMapper.createEventScene(eventScene);
            }
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 编辑事件场景
     */
    @Transactional(readOnly = true)
    @Override
    public EventSceneDO editEventScene(Long eventSceneId) {
        EventSceneDO eventSceneDO = new EventSceneDO();
        try {
            eventSceneDO = eventSceneMapper.getEventSceneDO(eventSceneId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventSceneServiceImpl] fail to editEventScene ", e);
        }
        return eventSceneDO;

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
            for (EventSceneDetail eventSceneDetail : eventSceneDetails) {
                EventScene eventScene = eventSceneDetail;
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
