/**
 * @(#)EventSorceService.java, 2018/8/20.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.event;

import com.zjtelcom.cpct.dto.event.EventSorce;

import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/08/20 15:57
 * version: V1.0
 */
public interface EventSorceService {

    Map<String, Object> saveEventSorce(EventSorce eventSorce);

    Map<String, Object> getEventSorce(Long evtSrcId);

    Map<String, Object> updateEventSorce(EventSorce eventSorce);

    Map<String, Object> deleteEventSorce(Long evtSrcId);

    Map<String, Object> listEventSorcePage(String evtSrcCode, String evtSrcName, Integer page, Integer pageSize);

    Map<String, Object> listEventSorceAll();
}