package com.zjtelcom.cpct.service.impl.event;

import com.zjtelcom.cpct.dao.event.EventItemMapper;
import com.zjtelcom.cpct.dao.event.EventMapper;
import com.zjtelcom.cpct.dao.event.EventSceneMapper;
import com.zjtelcom.cpct.domain.event.DO.EventDO;
import com.zjtelcom.cpct.domain.event.DO.EventItemDO;
import com.zjtelcom.cpct.domain.event.DO.EventSceneDO;
import com.zjtelcom.cpct.domain.event.DTO.EventDTO;
import com.zjtelcom.cpct.domain.event.EventList;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.event.EventService;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description EventServiceImpl
 * @Author pengy
 * @Date 2018/6/21 9:46
 */
@Service
@Transactional
public class EventServiceImpl extends BaseService implements EventService {

    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private EventItemMapper eventItemMapper;
    @Autowired
    private EventSceneMapper eventSceneMapper;

    /**
     * listEvents
     *
     * @return
     */
    @Override
    public List<EventList> listEvents(Long evtSrcId, String eventName) {
        List<EventList> eventLists = new ArrayList<>();
        try {
            eventLists = eventMapper.listEvents(evtSrcId, eventName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventServiceImpl] fail to listEvents ", e);
        }
        return eventLists;
    }

    /**
     * 新增事件
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveEvent(EventDTO eventDTO) {
        EventDO eventDO = new EventDO();
        try {
            //事件信息插入
            CopyPropertiesUtil.copyBean2Bean(eventDO, eventDTO);
            eventDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getErrorCode());
            eventMapper.saveEvent(eventDO);

            //关联到事件采集项
            List<EventItemDO> itemDOList = eventDTO.getEventItemDOList();
            if (itemDOList != null) {
                for (EventItemDO eventItemDO : itemDOList) {
                    eventItemMapper.saveEventItem(eventItemDO);
                }
            }
            //事件关联到场景
            EventSceneDO eventSceneDO = new EventSceneDO();
            eventSceneDO.setEventId(eventDO.getEventId());
            List<EventSceneDO> sceneDOList = eventSceneMapper.listEventSences(eventSceneDO);
            //场景关联到活动
            if (sceneDOList != null) {
                for (EventSceneDO evtSceneDO : sceneDOList) {

                }
            }
            //关联到事件匹配规则 todo 暂时未找到场景，待确认

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventServiceImpl] fail to saveEvent ", e);
        }
    }

    /**
     * 事件删除
     */
    @Transactional(readOnly = false)
    @Override
    public void delEvent(Long eventId) {
        try {
            eventMapper.delEvent(eventId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventServiceImpl] fail to delEvent ", e);
        }
    }

    /**
     * 关闭事件
     */
    @Transactional(readOnly = false)
    @Override
    public void closeEvent(Long eventId) {
        try {
            eventMapper.updateEventStatusCd(eventId, StatusCode.STATUS_CODE_FAILURE.getErrorCode());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventServiceImpl] fail to closeEvent ", e);
        }
    }

    /**
     * 编辑事件
     */
    @Transactional(readOnly = true)
    @Override
    public EventDTO editEvent(Long eventId) {
        EventDTO eventDTO = new EventDTO();
        try {
            EventDO eventDO = eventMapper.getEventById(eventId);
            CopyPropertiesUtil.copyBean2Bean(eventDTO, eventDO);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventServiceImpl] fail to editEvent ", e);
        }
        return eventDTO;
    }

}
