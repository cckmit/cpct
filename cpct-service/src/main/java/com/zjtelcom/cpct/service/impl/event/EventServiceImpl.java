package com.zjtelcom.cpct.service.impl.event;

import com.zjtelcom.cpct.dao.event.EventMapper;
import com.zjtelcom.cpct.domain.event.DO.EventDO;
import com.zjtelcom.cpct.domain.event.DO.EventItemDO;
import com.zjtelcom.cpct.domain.event.DTO.EventDTO;
import com.zjtelcom.cpct.domain.event.EventList;
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

    /**
     * listEvents
     * @return
     */
    @Override
    public List<EventList> listEvents(Long evtSrcId,String eventName) {
        List<EventList> eventLists = new ArrayList<>();
        try {
            eventLists = eventMapper.listEvents(evtSrcId,eventName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventServiceImpl] fail to listEvents ", e);
        }
        return eventLists;
    }

    /**
     * 新增事件
     */
    @Override
    public void saveEvent(EventDTO eventDTO) {
        EventDO eventDO = new EventDO();
        try {
            //事件信息插入
            CopyPropertiesUtil.copyBean2Bean(eventDO,eventDTO);
            eventMapper.saveEvent(eventDO);

            //关联到事件采集项 todo
            List<EventItemDO> itemDOList = eventDTO.getEventItemDOList();


            //关联到活动 todo

            //关联到事件匹配规则 todo

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventServiceImpl] fail to saveEvent ", e);
        }
    }
}
