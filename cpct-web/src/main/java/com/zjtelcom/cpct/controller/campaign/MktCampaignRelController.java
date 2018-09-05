/**
 * @(#)MktCampaignController.java, 2018/9/4.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.controller.campaign;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.service.campaign.MktCampaignRelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/09/04 18:34
 * version: V1.0
 */
@RestController
@RequestMapping("${adminPath}/campaignRel")
public class MktCampaignRelController {
    @Autowired
    private MktCampaignRelService mktCampaignRelService;

    /**
     * 获取当前活动的父活动 和 子活动
     *
     * @return
     */
    @RequestMapping(value = "/getMktCampaignRel", method = RequestMethod.POST)
    @CrossOrigin
    public String getMktCampaignRel(@RequestBody Map<String, String> params) throws Exception {
        Long mktCampaignId =Long.valueOf(params.get("mktCampaignId"));
        Map<String, Object> mktCampaignRel = mktCampaignRelService.getMktCampaignRel(mktCampaignId);
        return JSON.toJSONString(mktCampaignRel);
    }
}