package com.zjtelcom.cpct.service;

import com.zjtelcom.cpct.domain.EventList;

import java.util.List;

/**
 * @Description EventService
 * @Author pengy
 * @Date 2018/6/21 9:45
 */

public interface EventService {

    List<EventList> listEvents( Long evtSrcId, String eventName);

}
