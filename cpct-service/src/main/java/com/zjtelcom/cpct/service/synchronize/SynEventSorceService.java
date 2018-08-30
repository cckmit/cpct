/**
 * @(#)EventSorceService.java, 2018/8/20.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.synchronize;

import com.zjtelcom.cpct.dto.event.EventSorce;

import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/08/20 15:57
 * version: V1.0
 */
public interface SynEventSorceService {

    Map<String,Object> synchronizeSingleEventSorce(Long eventId,String roleName);

    Map<String,Object> synchronizeBatchEventSorce(String roleName);
}