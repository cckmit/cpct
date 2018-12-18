package com.zjtelcom.cpct.open.service.event;

import com.zjtelcom.cpct.dto.event.EventSorce;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/10/26
 * @Description:
 */
public interface OpenEventSourceService {

    Map<String, Object> saveEventSorce(EventSorce eventSorce);

    Map<String, Object> getEventSorce(Long evtSrcId);

    Map<String, Object> updateEventSorce(String evtSrcId,String params) throws IllegalAccessException, InvocationTargetException, InstantiationException;

    Map<String, Object> deleteEventSorce(Long evtSrcId);

    Map<String, Object> listEventSorcePage( Map<String, Object> map);

}
