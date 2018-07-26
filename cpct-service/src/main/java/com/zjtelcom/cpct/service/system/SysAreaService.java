/**
 * @(#)SysAreaService.java, 2018/7/9.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.system;

import com.zjtelcom.cpct.domain.SysArea;

import java.util.List;
import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/07/09 14:58
 * version: V1.0
 */
public interface SysAreaService {

    Map<String, Object> listSysArea();

    Map<String, Object> listSysCity();

    Map<String, Object> listAllAreaTrea();

    Map<String, Object> getCityTable(List<Integer> areaIds);

    Map<String, Object> saveCityTORedis();

}