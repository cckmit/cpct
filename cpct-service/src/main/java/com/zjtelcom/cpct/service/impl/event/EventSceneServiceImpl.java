package com.zjtelcom.cpct.service.impl.event;

import com.zjtelcom.cpct.dao.event.EventSceneMapper;
import com.zjtelcom.cpct.domain.event.DO.EventSceneDO;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.event.EventSceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

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
    public List<EventSceneDO> listSceneEvents(EventSceneDO eveneSceneDO) {
        List<EventSceneDO> eventLists = new ArrayList<>();
        try {
            eventLists = eventSceneMapper.listEventSences(eveneSceneDO);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventSceneServiceImpl] fail to listSceneEvents ", e);
        }
        return eventLists;
    }

    /**
     * 新增事件场景
     */
    @Transactional(readOnly = false)
    @Override
    public void saveEventScene(EventSceneDO eveneSceneDO) {
        try {
            eventSceneMapper.saveEventScene(eveneSceneDO);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventSceneServiceImpl] fail to saveEventScenes ", e);
        }
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
     * 新增事件场景
     */
    @Transactional(readOnly = false)
    @Override
    public void updateEventScene(EventSceneDO eveneSceneDO) {
        try {
            eventSceneMapper.updateByPrimaryKey(eveneSceneDO);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventSceneServiceImpl] fail to updateEventScene ", e);
        }
    }

}
