package com.zjtelcom.cpct.service.impl.event;

import com.zjtelcom.cpct.dao.event.EventMapper;
import com.zjtelcom.cpct.domain.event.EventList;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.event.EventService;
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
}
