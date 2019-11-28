package com.zjtelcom.cpct.controller.api;


import com.alibaba.fastjson.JSON;
import com.ctzj.smt.bss.cooperate.service.dubbo.IContactTaskReceiptService;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.campaign.MktCamEvtRelMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.dubbo.service.EventApiService;
import com.zjtelcom.cpct.service.api.TestService;
import com.zjtelcom.cpct.service.channel.SearchLabelService;
import com.zjtelcom.cpct.service.event.EventInstService;
import com.zjtelcom.cpct.service.synchronize.campaign.SynchronizeCampaignService;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct_prd.dao.campaign.MktCampaignPrdMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;


@RestController
@RequestMapping("${adminPath}/api")
public class EventApiController extends BaseController {


    @Autowired(required = false)
    private EventApiService eventApiService;

    @Autowired(required = false)
    private YzServ yzServ;

    @Autowired(required = false)
    private IContactTaskReceiptService iContactTaskReceiptService; //协同中心dubbo

    @Autowired
    private SearchLabelService searchLabelService;
    @Autowired
    private MktCamEvtRelMapper evtRelMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired(required = false)
    private SynchronizeCampaignService synchronizeCampaignService;
    @Autowired
    private MktCampaignMapper campaignMapper;
    @Autowired
    private MktCampaignPrdMapper campaignPrdMapper;

    @Autowired(required = false)
    private TestService testService;

    @Autowired(required = false)
    private EventInstService eventInstService;


    @PostMapping("caculateTest")
    public  Map<String,Object> caculateTest() {
        Map<String,Object> result = new HashMap<>();
        List<MktCampaignDO> campaigns = new ArrayList<>();
        try {
            result = testService.caculateTest();
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

    @PostMapping("syncCampaign")
    public  Map<String,String> syncCampaign(@RequestBody HashMap<String,String> key) {
        Map<String,String> result = new HashMap<>();
        List<MktCampaignDO> campaigns = new ArrayList<>();
        try {
            List<MktCampaignDO> campaignDOS = campaignMapper.qryMktCampaignListByTypeAndStatus(null,"2002");
            for (MktCampaignDO campaignDO : campaignDOS){
                if (campaignPrdMapper.selectByPrimaryKey(campaignDO.getMktCampaignId())==null){
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                String roleName = "admin";
                                synchronizeCampaignService.synchronizeCampaign(campaignDO.getMktCampaignId(), roleName);
                            } catch (Exception e) {
                                e.printStackTrace();
                                logger.error("[op:MktCampaignServiceImpl] 活动同步失败 by mktCampaignId = {}, Expection = ",campaignDO.getMktCampaignId(), e);
                            }
                        }
                    }.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



    /**
     * 事件触发入口
     */
    @RequestMapping("/CalculateCPC")
    @CrossOrigin
    public String eventInput(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) {
        Map result = new HashMap();
        try {
            result = eventApiService.CalculateCPC(params);
        } catch (Exception e) {
            e.printStackTrace();
            return initFailRespInfo(e.getMessage(), "");
        }
        return initSuccRespInfo(result);
    }

    @RequestMapping(value = "/CalculateCPCSync", method = RequestMethod.POST)
    @CrossOrigin
    public String eventInputSync(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) {
        // 打开日志开关
        redisUtils.set("SYSYTEM_ESLOG_STATUS", "1");


        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET");
        response.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token");

        Map<String,Object> result = new HashMap<>();
        Map<String,Object> resultMap = new HashMap<>();

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String date = df.format(new Date());

        params.put("reqId","EVT" + date + getRandNum(1,999999));

        try {
            result = eventApiService.CalculateCPCSync(params);
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("resultCode",CODE_FAIL);
            resultMap.put("resultMsg","失败！");
            return JSON.toJSONString(resultMap);
        }
        // 关闭日志开关
        redisUtils.set("SYSYTEM_ESLOG_STATUS", "0");
        resultMap.put("resultCode",CODE_SUCCESS);
        resultMap.put("resultMsg",result);
        return JSON.toJSONString(resultMap);
    }


    @RequestMapping("/SecondChannelSynergy")
    @CrossOrigin
    public String SecondChannelSynergy(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) {
        Map result = new HashMap();
        try {
            result = eventApiService.secondChannelSynergy(params);
        } catch (Exception e) {
            e.printStackTrace();
            return initFailRespInfo(e.getMessage(), "");
        }
        return initSuccRespInfo(result);
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


    @RequestMapping(value = "/cpc", method = RequestMethod.POST)
    @CrossOrigin
    public String cpc(@RequestBody Map<String, Object> params) {
        Map result = new HashMap();
        try {
            result = iContactTaskReceiptService.contactTaskReceipt(params);
            if (result == null) {
                return JSON.toJSONString("失败");
            }
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



    @RequestMapping(value = "/queryEventInst", method = RequestMethod.POST)
    @CrossOrigin
    public String queryEventInst(@RequestBody Map<String, String> params) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap = eventInstService.queryEventInst(params);
        return JSON.toJSONString(resultMap);
    }


    @RequestMapping(value = "/queryEventInstLog", method = RequestMethod.POST)
    @CrossOrigin
    public String queryEventInstLog(@RequestBody Map<String, String> params) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap = eventInstService.queryEventInstLog(params);
        return JSON.toJSONString(resultMap);
    }

}
