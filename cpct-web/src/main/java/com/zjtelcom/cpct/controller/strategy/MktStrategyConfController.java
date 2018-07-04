/**
 * @(#)MktStrategyConfController.java, 2018/7/4.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.controller.strategy;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfDetail;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/07/04 15:46
 * version: V1.0
 */

@RestController
@RequestMapping("${adminPath}/MktStrategyConf")
public class MktStrategyConfController extends BaseController {

    @Autowired
    private MktStrategyConfService mktStrategyConfService;


    /**
     * 添加策略配置信息
     *
     * @param mktStrategyConfDetail
     * @return
     */
    @RequestMapping("/saveMktStrategyConf")
    @CrossOrigin
    public String saveMktStrategyConf(@RequestBody MktStrategyConfDetail mktStrategyConfDetail) {
        Map<String, Object> map = new HashMap<>();
        try {
            map = mktStrategyConfService.saveMktStrategyConf(mktStrategyConfDetail);
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfController] failed to save MktStrategyConf = {}", mktStrategyConfDetail, e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 修改策略配置信息
     *
     * @param mktStrategyConfDetail
     * @return
     */
    @RequestMapping("/updateMktStrategyConf")
    @CrossOrigin
    public String updateMktStrategyConf(@RequestBody MktStrategyConfDetail mktStrategyConfDetail) {
        Map<String, Object> map = new HashMap<>();
        try {
            map = mktStrategyConfService.updateMktStrategyConf(mktStrategyConfDetail);
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfController] failed to update MktStrategyConf = {}", mktStrategyConfDetail, e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 查询策略配置信息
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/getMktStrategyConf", method = RequestMethod.POST)
    @CrossOrigin
    public String getMktStrategyConf(@RequestBody Map<String, String> params) {
        Map<String, Object> map = new HashMap<>();
        Long mktStrategyConfId = Long.valueOf(params.get("mktStrategyConfId"));
        try {
            map = mktStrategyConfService.getMktStrategyConf(mktStrategyConfId);
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfController] failed to get by mktStrategyConfId = {}", mktStrategyConfId, e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 删除策略配置信息
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/deleteMktStrategyConf", method = RequestMethod.POST)
    @CrossOrigin
    public String deleteMktStrategyConf(@RequestBody Map<String, String> params) {
        Map<String, Object> map = new HashMap<>();
        Long mktStrategyConfId = Long.valueOf(params.get("mktStrategyConfId"));
        try {
            map = mktStrategyConfService.deleteMktStrategyConf(mktStrategyConfId);
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfController] failed to delete by mktStrategyConfId = {}", mktStrategyConfId, e);
        }
        return JSON.toJSONString(map);
    }
}