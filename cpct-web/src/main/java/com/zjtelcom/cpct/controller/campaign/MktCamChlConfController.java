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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @RequestMapping("/saveMktCamChlConf")
    @CrossOrigin
    public String saveMktCamChlConf(MktCamChlConfDetail mktCamChlConfDetail){
        Map<String, Object> map = new HashMap<>();
        try {
            map = mktCamChlConfService.saveMktCamChlConf(mktCamChlConfDetail);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfController] failed to save mktCamChlConfDetail = {} Exception: ",JSON.toJSON(mktCamChlConfDetail), e);
        }
        return JSON.toJSONString(map);
    }

    @RequestMapping("/updateMktCamChlConf")
    @CrossOrigin
    public String updateMktCamChlConf(MktCamChlConfDetail mktCamChlConfDetail){
        Map<String, Object> map = new HashMap<>();
        try {
            map = mktCamChlConfService.updateMktCamChlConf(mktCamChlConfDetail);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfController] failed to update mktCamChlConfDetail = {} Exception: ",JSON.toJSON(mktCamChlConfDetail), e);
        }
        return JSON.toJSONString(map);
    }


}