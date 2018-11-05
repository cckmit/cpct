/**
 * @(#)MktCamChlConfController.java, 2018/7/4.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.controller.campaign;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConf;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.service.campaign.MktCamChlConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 协同渠道
 * author: linchao
 * date: 2018/07/04 14:54
 * version: V1.0
 */

@RestController
@RequestMapping("${adminPath}/mktCamChlConf")
public class MktCamChlConfController extends BaseController {

    @Autowired
    private MktCamChlConfService mktCamChlConfService;


    @RequestMapping(value = "/saveMktCamChlConf", method = RequestMethod.POST)
    @CrossOrigin
    public String saveMktCamChlConf(@RequestBody MktCamChlConfDetail mktCamChlConfDetail){
        Map<String, Object> map = new HashMap<>();
        try {
            map = mktCamChlConfService.saveMktCamChlConf(mktCamChlConfDetail);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfController] failed to save mktCamChlConfDetail = {} Exception: ",JSON.toJSON(mktCamChlConfDetail), e);
        }
        return JSON.toJSONString(map);
    }

    /**
     * 更新协同渠道配置
     *
     * @param mktCamChlConfDetail
     * @return
     */
    @RequestMapping(value = "/updateMktCamChlConf", method = RequestMethod.POST)
    @CrossOrigin
    public String updateMktCamChlConf(@RequestBody MktCamChlConfDetail mktCamChlConfDetail){
        Map<String, Object> map = new HashMap<>();
        try {
            map = mktCamChlConfService.updateMktCamChlConf(mktCamChlConfDetail);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfController] failed to update mktCamChlConfDetail = {} Exception: ",JSON.toJSON(mktCamChlConfDetail), e);
        }
        return JSON.toJSONString(map);
    }

    /**
     * 获取协同渠道配置
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/getMktCamChlConf", method = RequestMethod.POST)
    @CrossOrigin
    public String getMktCamChlConf(@RequestBody Map<String, String> params){
        Map<String, Object> map = new HashMap<>();
        Long evtContactConfId = Long.valueOf(params.get("evtContactConfId"));
        try {
            map = mktCamChlConfService.getMktCamChlConf(evtContactConfId);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfController] failed to get mktCamChlConfDetail = {} Exception: ", evtContactConfId, e);
        }
        return JSON.toJSONString(map);
    }

    /**
     * 删除协同渠道配置
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/deleteMktCamChlConf", method = RequestMethod.POST)
    @CrossOrigin
    public String deleteMktCamChlConf(@RequestBody Map<String, String> params){
        Map<String, Object> map = new HashMap<>();
        Long evtContactConfId = Long.valueOf(params.get("evtContactConfId"));
        Long ruleId = Long.valueOf(params.get("ruleId"));
        try {
            // 删除推送渠道
            map = mktCamChlConfService.deleteMktCamChlConf(evtContactConfId, ruleId);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfController] failed to delete mktCamChlConfDetail = {} Exception: ", evtContactConfId, e);
        }
        return JSON.toJSONString(map);
    }



}