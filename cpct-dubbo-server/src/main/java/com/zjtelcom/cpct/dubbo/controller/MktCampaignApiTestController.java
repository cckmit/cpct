package com.zjtelcom.cpct.dubbo.controller;


import com.alibaba.fastjson.JSON;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.dubbo.model.RetCamResp;
import com.zjtelcom.cpct.dubbo.model.RetCamResult;
import com.zjtelcom.cpct.dubbo.service.*;
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

    @Autowired
    private ProductService productService;

    @Autowired
    private MktCampaignSyncApiService mktCampaignSyncApiService;

    @Autowired
    private MktCamChlResultApiService mktCamChlResultApiService;



    @RequestMapping(value = "/publishMktCampaign", method = RequestMethod.POST)
    @CrossOrigin
    public Map<String,Object> publishMktCampaign(Long mktCampaignId) {
        Map<String,Object> retCamResp = new HashMap<>();
        try {
            retCamResp = mktCampaignSyncApiService.publishMktCampaign(mktCampaignId);
        } catch (Exception e) {
            e.printStackTrace();
            return retCamResp;
        }
        return retCamResp;
    }


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



    @RequestMapping(value = "/selectProductCam", method = RequestMethod.POST)
    @CrossOrigin
    public String selectProductCam(@RequestBody Map<String,Object> paramListMap) {
        List<Map<String,Object>> paramList = (List<Map<String,Object>>) paramListMap.get("paramList");
        Map<String, Object> resultMap = new HashMap<>();
        try {
            resultMap = productService.selectProductCam(paramList);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return JSON.toJSONString(resultMap);
    }


    @PostMapping("/getCloseCampaign")
    @CrossOrigin
    public String getCloseCampaign(@RequestBody Map<String,Object> paramMap){
        Map<String, Object> resultMap = new HashMap<>();
        try {
            resultMap = productService.getCloseCampaign(paramMap);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return JSON.toJSONString(resultMap);
    }


    /**
     * 查询所有 有二次协同 且二次协同为工单，且有效的
     *
     * @return
     */
    @RequestMapping(value = "/selectResultByMktCampaignId", method = RequestMethod.POST)
    @CrossOrigin
    public String selectResultByMktCampaignId() {
        RetCamResult retCamResult = new RetCamResult();
        try {
            retCamResult = mktCamChlResultApiService.selectResultList();
        } catch (Exception e) {
            return e.getMessage();
        }
        return JSON.toJSONString(retCamResult);
    }
}
