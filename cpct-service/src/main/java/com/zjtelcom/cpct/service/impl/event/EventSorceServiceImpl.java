package com.zjtelcom.cpct.service.impl.event;

import com.zjtelcom.cpct.dao.event.EventSorceMapper;
import com.zjtelcom.cpct.domain.event.EventSorce;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.event.EventSorceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description EventSorceServiceImpl
 * @Author pengy
 * @Date 2018/6/21 9:46
 */
@Service
@Transactional
public class EventSorceServiceImpl extends BaseService implements EventSorceService {

    @Autowired
    private EventSorceMapper eventSorceMapper;

    /**
     * listEvents
     *
     * @return
     */
    @Override
    public List<EventSorce> listEventSorces(String evtSrcCode, String evtSrcName) {
        List<EventSorce> eventSorceList = new ArrayList<>();
        try {
            eventSorceList = eventSorceMapper.listEventSorces(evtSrcCode, evtSrcName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventSorceServiceImpl] fail to listEvents ", e);
        }
        return eventSorceList;
    }

    /**
     * delete event sorce
     */
    @Override
    public void delEventSorce(Long evtSrcId) {
        try {
            eventSorceMapper.delEventSorce(evtSrcId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventSorceServiceImpl] fail to eventSorceDel ", e);
        }
    }

    /**
     * edit event sorce
     */
    @Override
    public EventSorce editEventSorce(Long evtSrcId) {
        EventSorce eventSorce = new EventSorce();
        try {
            eventSorce = eventSorceMapper.editEventSorce(evtSrcId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventSorceServiceImpl] fail to editEventSorce ", e);
        }
        return eventSorce;
    }

    /**
     * add event sorce
     */
    @Override
    public void saveEventSorce(EventSorce eventSorce) {
        try {
            eventSorceMapper.saveEventSorce(eventSorce);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventSorceServiceImpl] fail to addEventSorce ", e);
        }
    }

    /**
     * update event sorce
     */
    @Override
    public void updateEventSorce(EventSorce eventSorce) {
        try {
            eventSorceMapper.updateEventSorce(eventSorce);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventSorceServiceImpl] fail to updateEventSorce ", e);
        }
    }

}
