/**
 * @(#)SysAreaController.java, 2018/7/9.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.controller.system;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.service.system.SysAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public String listSysArea() {
        Map<String, Object> map = null;
        try {
            map = sysAreaService.listSysArea();
            map.put("resultCode", CommonConstant.CODE_SUCCESS);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
        }
        return JSON.toJSONString(map);
    }

    /**
     * 获取地市级别列表
     *
     * @return
     */
    @RequestMapping(value = "/listSysCity", method = RequestMethod.POST)
    @CrossOrigin
    public String listSysCity(@RequestBody  Map<String, Object> params) {
        String lanId = (String) params.get("lanId");
        Integer areaId;
        if (lanId == null || "".equals(lanId) || "null".equals(lanId)) {
            //TODO 获取当前用户所在地区
            areaId = 1;
        } else {
            areaId = Integer.valueOf(lanId);
        }
        Map<String, Object> map = null;
        try {
            map = sysAreaService.listCityByParentId(areaId);
            map.put("resultCode", CommonConstant.CODE_SUCCESS);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 获取下发城市树
     *
     * @return
     */
    @RequestMapping(value = "/listSysAreaTree", method = RequestMethod.POST)
    @CrossOrigin
    public String listSysAreaTree() {
        Map<String, Object> map = null;
        try {
            map = sysAreaService.listAllAreaTrea();
            map.put("resultCode", CommonConstant.CODE_SUCCESS);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 获取选中的城市
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/getCityTable", method = RequestMethod.POST)
    @CrossOrigin
    public String getCityTable(@RequestBody  Map<String, Object> params) {
        List<Integer> areaIds = (List<Integer>) params.get("areaIds");
        Map<String, Object> cityTableMap = null;
        try {
            cityTableMap = sysAreaService.getCityTable(areaIds);
            cityTableMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        } catch (Exception e) {
            cityTableMap.put("resultCode", CommonConstant.CODE_FAIL);
        }
        return JSON.toJSONString(cityTableMap);
    }

    /**
     * 初始化城市数据到redis
     *
     * @return
     */
    @RequestMapping(value = "/saveCityTORedis", method = RequestMethod.POST)
    @CrossOrigin
    public String saveCityTORedis() {
        Map<String, Object> map = sysAreaService.saveCityTORedis();
        return JSON.toJSONString(map);
    }


}