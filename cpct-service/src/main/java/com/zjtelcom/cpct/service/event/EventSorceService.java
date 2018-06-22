package com.zjtelcom.cpct.service.event;

import com.zjtelcom.cpct.domain.event.EventSorce;

import java.util.List;

/**
 * @Description EventSorceService
 * @Author pengy
 * @Date 2018/6/21 9:45
 */

public interface EventSorceService {

    List<EventSorce> listEventSorces(String evtSrcCode, String eventName);

    void delEventSorce(Long evtSrcId);

    EventSorce editEventSorce(Long evtSrcId);

    void saveEventSorce(EventSorce eventSorce);

    void updateEventSorce(EventSorce eventSorce);

}
