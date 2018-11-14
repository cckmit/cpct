/**
 * @(#)EventSorceService.java, 2018/8/20.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.synchronize;

import java.util.Map;

/**
 * @Description 同步事件源
 * @Author pengy
 * @Date: 2018/8/28
 */
public interface SynEventSorceService {

    Map<String,Object> synchronizeSingleEventSorce(Long eventId,String roleName);

    Map<String,Object> synchronizeBatchEventSorce(String roleName);

    Map<String,Object> deleteSingleEventSorce(Long eventId,String roleName);

}