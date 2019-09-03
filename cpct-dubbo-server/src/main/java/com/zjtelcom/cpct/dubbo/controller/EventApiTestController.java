package com.zjtelcom.cpct.dubbo.controller;


import com.alibaba.fastjson.JSON;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.dao.campaign.MktCamEvtRelMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamEvtRelDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.dto.campaign.MktCamEvtRel;
import com.zjtelcom.cpct.dto.campaign.MktCampaign;
import com.zjtelcom.cpct.dubbo.service.EventApiService;
import com.zjtelcom.cpct.dubbo.service.MktCampaignApiService;
import com.zjtelcom.cpct.dubbo.service.MktCampaignSyncApiService;
import com.zjtelcom.cpct.dubbo.service.TrialRedisService;
import com.zjtelcom.cpct.service.channel.SearchLabelService;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/eventTest")
public class EventApiTestController {


    @Autowired(required = false)
    private EventApiService eventApiService;
    @Autowired
    private TrialRedisService trialRedisService;

    @Autowired(required = false)
    private YzServ yzServ;
//    @Autowired
//    private MktCampaignPrdMapper mktCampaignPrdMapper;
    @Autowired
    private MktCampaignSyncApiService syncApiService;
    @Autowired
    private SearchLabelService searchLabelService;
    @Autowired
    private MktCamEvtRelMapper evtRelMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private MktCampaignApiService mktCampaignApiService;


    @PostMapping("copyMktCampaign")
    public  Map<String,Object> copyMktCampaign(@RequestBody HashMap<String,String> key) {
        Map<String,Object> result = new HashMap<>();
        List<MktCampaignDO> campaigns = new ArrayList<>();
        try {
            result =  mktCampaignApiService.copyMktCampaign(Long.valueOf(key.get("campaignId")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    @PostMapping("test")
    public  Map<String,String> test(@RequestBody HashMap<String,String> key) {
        Map<String,String> result = new HashMap<>();
        List<MktCampaignDO> campaigns = new ArrayList<>();
        try {
            List<Long> idlIST = new ArrayList<>();
            idlIST = ChannelUtil.StringToIdList(key.get("key"));
            for (Long id : idlIST){
                Map<String, String> mktAllLabels = searchLabelService.labelListByEventId(id );  //查询事件下使用的所有标签
                if (null != mktAllLabels) {
                    redisUtils.set("EVT_ALL_LABEL_" + id, mktAllLabels);
                    result.put("EVENT_"+id,JSON.toJSONString(redisUtils.get("EVT_ALL_LABEL_"+id)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping(value = "/cpc", method = RequestMethod.POST)
    @CrossOrigin
    public String CalculateCPC(@RequestBody Map<String, Object> params) {
        Map result = new HashMap();
        try {
            result = eventApiService.CalculateCPC(params);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return JSON.toJSONString(result);
    }


    @RequestMapping(value = "/cpcSync", method = RequestMethod.POST)
    @CrossOrigin
    public String CalculateCPCSync(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) {

        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET");
        response.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token");

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String date = df.format(new Date());

        params.put("reqId","EVT" + date + getRandNum(1,999999));

        Map result = new HashMap();
        try {
            result = eventApiService.CalculateCPCSync(params);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return JSON.toJSONString(result);
    }

    public static int getRandNum(int min, int max) {
        int randNum = min + (int)(Math.random() * ((max - min) + 1));
        return randNum;
    }

    @RequestMapping(value = "/secondChannelSynergy", method = RequestMethod.POST)
    @CrossOrigin
    public String secondChannelSynergy(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) {

        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET");
        response.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token");

        Map result = new HashMap();
        try {
            result = eventApiService.secondChannelSynergy(params);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return JSON.toJSONString(result);
    }


    @RequestMapping(value = "/label", method = RequestMethod.POST)
    @CrossOrigin
    public String label(@RequestBody String params) {
        Map result = new HashMap();
        try {
            result = yzServ.queryYz(params);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return JSON.toJSONString(result);
    }


}
