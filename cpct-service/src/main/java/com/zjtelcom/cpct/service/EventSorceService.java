package com.zjtelcom.cpct.service;

import com.zjtelcom.cpct.domain.EventSorce;

import java.util.List;

/**
 * @Description EventSorceService
 * @Author pengy
 * @Date 2018/6/21 9:45
 */

public interface EventSorceService {

    List<EventSorce> listEventSorces(String evtSrcCode, String eventName);

    void eventSorceDel(Long evtSrcId);

}
