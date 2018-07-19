/**
 * @(#)SysAreaServiceImpl.java, 2018/7/9.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.system;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.system.SysAreaMapper;
import com.zjtelcom.cpct.domain.SysArea;
import com.zjtelcom.cpct.service.system.SysAreaService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/07/09 14:59
 * version: V1.0
 */
@Service
public class SysAreaServiceImpl implements SysAreaService {

    @Autowired
    private SysAreaMapper sysAreaMapper;

    @Override
    public Map<String, Object> listSysArea() {
        List<SysArea> sysAreas = sysAreaMapper.selectAll();
        Map<String, Object> map = new HashMap<>();
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("sysAreas", sysAreas);
        return map;
    }

    @Override
    public Map<String, Object> listSysCity() {
        // 获取等级为2的地市级别的城市
        List<SysArea> sysCitys = sysAreaMapper.selectByAreaLevel(2);
        Map<String, Object> map = new HashMap<>();
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("sysCitys", sysCitys);
        return map;
    }
}