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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/07/04 15:46
 * version: V1.0
 */

@RestController
@RequestMapping("${adminPath}/mktStrategyConf")
public class MktStrategyConfController extends BaseController {

    @Autowired
    private MktStrategyConfService mktStrategyConfService;


    /**
     * 添加策略配置信息
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/saveMktStrategyConf", method = RequestMethod.POST)
    @CrossOrigin
    public String saveMktStrategyConf(@RequestBody Map<String, List<MktStrategyConfDetail>> params) {
        Map<String, Object> map = new HashMap<>();
        Map<String, List<Long>> mktStrategyConfIdListMap = new HashMap<>();
        List<Long> mktStrategyConfIdList = new ArrayList<>();
        try {
            List<MktStrategyConfDetail> mktStrategyConfDetailList = params.get("mktStrategyConfDetailList");

            for (MktStrategyConfDetail mktStrategyConfDetail : mktStrategyConfDetailList) {
                map = mktStrategyConfService.saveMktStrategyConf(mktStrategyConfDetail);
                mktStrategyConfIdList.add(Long.valueOf(map.get("mktStrategyConfId").toString()));
            }
            mktStrategyConfIdListMap.put("mktStrategyConfIdList", mktStrategyConfIdList);
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfController] failed to save mktStrategyConfIdList = {}", JSON.toJSON(mktStrategyConfIdList), e);
        }
        return JSON.toJSONString(mktStrategyConfIdListMap);
    }


    /**
     * 修改策略配置信息
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/updateMktStrategyConf", method = RequestMethod.POST)
    @CrossOrigin
    public String updateMktStrategyConf(@RequestBody Map<String, List<MktStrategyConfDetail>> params) {
        Map<String, List<Long>> mktStrategyConfIdListMap = new HashMap<>();
        List<Long> mktStrategyConfIdList = new ArrayList<>();
        try {
            List<MktStrategyConfDetail> mktStrategyConfDetailList = params.get("mktStrategyConfDetailList");

            for (MktStrategyConfDetail mktStrategyConfDetail : mktStrategyConfDetailList) {
                mktStrategyConfService.updateMktStrategyConf(mktStrategyConfDetail);
                mktStrategyConfIdList.add(mktStrategyConfDetail.getMktStrategyConfId());
            }
            mktStrategyConfIdListMap.put("mktStrategyConfIdList", mktStrategyConfIdList);
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfController] failed to save mktStrategyConfIdList = {}", JSON.toJSON(mktStrategyConfIdList), e);
        }
        return JSON.toJSONString(mktStrategyConfIdListMap);
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