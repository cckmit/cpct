package com.zjtelcom.cpct.dubbo.controller;


import com.alibaba.fastjson.JSON;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.dubbo.model.RetCamResp;
import com.zjtelcom.cpct.dubbo.service.EventApiService;
import com.zjtelcom.cpct.dubbo.service.MktCampaignApiService;
import com.zjtelcom.cpct.dubbo.service.MktCampaignSyncApiService;
import com.zjtelcom.cpct.dubbo.service.TrialRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/mktCamTest")
public class MktCampaignApiTestController {
    @Autowired
    private MktCampaignApiService mktCampaignApiService;

    @RequestMapping(value = "/qryMktCampaignDetail", method = RequestMethod.POST)
    @CrossOrigin
    public String qryMktCampaignDetail(Long mktCampaignId) {
        RetCamResp retCamResp = new RetCamResp();
        try {
            retCamResp = mktCampaignApiService.qryMktCampaignDetail(mktCampaignId);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return JSON.toJSONString(retCamResp);
    }

}
