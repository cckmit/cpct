package com.zjtelcom.cpct.dubbo.controller;


import com.alibaba.fastjson.JSON;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.dao.campaign.MktCamEvtRelMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamEvtRelDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.dto.campaign.MktCamEvtRel;
import com.zjtelcom.cpct.dto.campaign.MktCampaign;
import com.zjtelcom.cpct.dubbo.service.EventApiService;
import com.zjtelcom.cpct.dubbo.service.MktCampaignSyncApiService;
import com.zjtelcom.cpct.dubbo.service.TrialRedisService;
import com.zjtelcom.cpct.service.campaign.MktCampaignService;
import com.zjtelcom.cpct.service.channel.SearchLabelService;
import com.zjtelcom.cpct.service.grouping.TrialProdService;
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
    @Autowired(required = false)
    private MktCampaignSyncApiService syncApiService;
    @Autowired
    private SearchLabelService searchLabelService;
    @Autowired
    private MktCamEvtRelMapper evtRelMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private TrialProdService trialProdService;

    @Autowired
    private MktCampaignMapper mktCampaignMapper;

    @Autowired
    private MktCampaignService mktCampaignService;
    @PostMapping(value = "/testIssuedSuccessMktCheck")
    @CrossOrigin
    public void testIssuedSuccessMktCheck() {
        try {
            String msg = "runCycleMarketCampaignDay执行任务";
            //筛选出周期性、已发布的活动
            List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListByTypeAndStatus("2000", "2002");
            //筛选出周期为"天"的营销活动
            List<MktCampaignDO> mktCampaignDOs = new ArrayList<>();
            for (MktCampaignDO mktCampaignDO : mktCampaignDOList) {
                String[] execInvl = mktCampaignDO.getExecInvl().split("-");
                if (execInvl.length < 2) {
                    continue;
                }
                if (execInvl[1].equals("1000") && mktCampaignDO.getMktCampaignCategory().equals("3000") && mktCampaignDO.getMktCampaignType().equals("1000")) {
                    mktCampaignDOs.add(mktCampaignDO);
                }
            }
            cycleDayRule(mktCampaignDOs);
        } catch (Exception e) {
        }
    }



    /**
     * 单位为天的周期性营销活动
     */
    public void runCycleMarketCampaignDay() throws Throwable { //不带Job job参数、没有返回，适用于无参数无返回的任务

    }

    /**
     * 周期为“天”的方案
     */
    private void cycleDayRule(List<MktCampaignDO> list) {
        try {
            Map<String, Object> result = new HashMap<>();
            //筛选出条件满足的周期性活动id
            List<Integer> campaignIdList = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());
            for (MktCampaignDO campaignDO : list) {
                String planEndTime = sdf.format(campaignDO.getPlanEndTime());
                String planBeginTime = sdf.format(campaignDO.getPlanBeginTime());
                long flagEnd = (sdf.parse(planEndTime).getTime() - sdf.parse(today).getTime()) / (1000 * 3600 * 24);
                long flagBegin = (sdf.parse(today).getTime() - sdf.parse(planBeginTime).getTime()) / (1000 * 3600 * 24);
                //判断当前日期是否在活动的生失效时间内
                if (flagEnd >= 0 && flagBegin >= 0) {
                    String[] execInvl = campaignDO.getExecInvl().split("-");
                    String execInitTime = campaignDO.getExecInitTime();
                    //当前时间与执行时间的相差时间
                    long dayDifference = (sdf.parse(today).getTime() - sdf.parse(execInitTime).getTime()) / (1000 * 3600 * 24);
                    //判断当前时间是否在执行时间之后
                    if (dayDifference >= 0) {
                        int cycleTime = Integer.parseInt(execInvl[0]);
                        //判断相差时间是否符合周期间隔
                        if (dayDifference % cycleTime == 0) {
                            campaignIdList.add(Integer.parseInt(campaignDO.getMktCampaignId().toString()));
                        }
                    }
                }
            }
            System.out.println("*******************周期为“天”的营销活动列表*******************" + campaignIdList);
            result.put("idList", campaignIdList);
            result.put("perCampaign", "PER_CAMPAIGN");
            trialProdService.campaignIndexTask(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    /**
     * 获取活动列表
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getCampaignList", method = RequestMethod.POST)
    @CrossOrigin
    public String getCampaignList(@RequestBody Map<String, Object> params) throws Exception {
        String mktCampaignName = params.get("mktCampaignName").toString();  // 活动名称
        Long eventId = null;
        String mktCampaignType = null;
        if (params.get("eventId") != null) {
            eventId = Long.valueOf(params.get("eventId").toString());
        }
        if (params.get("mktCampaignType")!=null && !params.get("mktCampaignType").equals("")){
            mktCampaignType = params.get("mktCampaignType").toString(); // 活动
        }
        Map<String, Object> map = mktCampaignService.getCampaignList(mktCampaignName, mktCampaignType, eventId);
        return JSON.toJSONString(map);
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
