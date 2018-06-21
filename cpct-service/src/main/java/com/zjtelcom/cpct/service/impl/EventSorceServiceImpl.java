package com.zjtelcom.cpct.service.impl;

import com.zjtelcom.cpct.dao.EventSorceMapper;
import com.zjtelcom.cpct.domain.EventSorce;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.EventSorceService;
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
     * @return
     */
    @Override
    public List<EventSorce> listEventSorces(String evtSrcCode, String evtSrcName) {
        List<EventSorce> eventSorceList = new ArrayList<>();
        try {
            eventSorceList = eventSorceMapper.listEventSorces(evtSrcCode,evtSrcName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventSorceServiceImpl] fail to listEvents ", e);
        }
        return eventSorceList;
    }

    /**
     *  delete event sorce
     */
    @Override
    public void eventSorceDel(Long evtSrcId) {
        try {
            eventSorceMapper.eventSorceDel(evtSrcId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventSorceServiceImpl] fail to eventSorceDel ", e);
        }
    }

}
