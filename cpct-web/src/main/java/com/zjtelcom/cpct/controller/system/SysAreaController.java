/**
 * @(#)SysAreaController.java, 2018/7/9.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.controller.system;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.service.system.SysAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/07/09 15:04
 * version: V1.0
 */
@RestController
@RequestMapping("${adminPath}/SysArea")
public class SysAreaController {

    @Autowired
    private SysAreaService sysAreaService;

    @RequestMapping(value = "/listSysArea", method = RequestMethod.POST)
    @CrossOrigin
    public String listSysArea(){
        Map<String, Object> map = sysAreaService.listSysArea();
        return JSON.toJSONString(map);
    }

    @RequestMapping(value = "/listSysCity", method = RequestMethod.POST)
    @CrossOrigin
    public String listSysCity(){
        Map<String, Object> map = sysAreaService.listSysCity();
        return JSON.toJSONString(map);
    }


    @RequestMapping(value = "/listSysAreaTree", method = RequestMethod.POST)
    @CrossOrigin
    public String listSysAreaTree(){
        Map<String, Object> map = sysAreaService.listAllAreaTrea();
        return JSON.toJSONString(map);
    }


}