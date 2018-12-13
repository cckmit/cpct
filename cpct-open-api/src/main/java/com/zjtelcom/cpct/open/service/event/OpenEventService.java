package com.zjtelcom.cpct.open.service.event;

import com.zjtelcom.cpct.open.entity.event.Event;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface OpenEventService {

    Map<String, Object> getEvent(Long contactEvtId);

    Map<String, Object> saveEvent(Event event);

    Map<String, Object> updateEvent(String eventId, String params);

    Map<String, Object> deleteEvent(Long contactEvtId);

    Map<String, Object> listEventPage(Map<String, Object> params);

}
