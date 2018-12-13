package com.zjtelcom.cpct.open.service.event;

import com.zjtelcom.cpct.open.entity.event.EventType;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface OpenEventTypeService {

    Map<String, Object> getEventType(Long evtTypeId);

    Map<String, Object> saveEventType(EventType eventType);

    Map<String, Object> updateEventType(String evtTypeId, String params);

    Map<String, Object> deleteEventType(Long evtTypeId);

    Map<String, Object> listEventPageType(Map<String, Object> params);
}
