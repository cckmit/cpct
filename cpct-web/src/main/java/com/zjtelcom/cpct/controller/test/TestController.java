package com.zjtelcom.cpct.controller.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ctzj.smt.bss.sysmgr.model.common.SysmgrResultObject;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.ctzj.smt.bss.sysmgr.privilege.service.dubbo.api.ISystemPostDubboService;
import com.ctzj.smt.bss.sysmgr.privilege.service.dubbo.api.ISystemUserDtoDubboService;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.rule.RuleResult;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.MktVerbalConditionMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.User;
import com.zjtelcom.cpct.service.EngineTestService;
import com.zjtelcom.cpct.service.campaign.MktCamChlResultApiService;
import com.zjtelcom.cpct.service.campaign.MktCampaignApiService;
import com.zjtelcom.cpct.service.campaign.MktCampaignService;
import com.zjtelcom.cpct.service.channel.LabelService;
import com.zjtelcom.cpct.service.es.EsHitsService;
import com.zjtelcom.cpct.service.event.ContactEvtService;
import com.zjtelcom.cpct.service.grouping.TarGrpService;
import com.zjtelcom.cpct.service.grouping.TarGrpTemplateService;
import com.zjtelcom.cpct.service.impl.dubbo.CamCpcSpecialLogic;
import com.zjtelcom.cpct.service.scheduled.ScheduledTaskService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfRuleService;
import com.zjtelcom.cpct.util.MapUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping("${adminPath}/test")
public class TestController extends BaseController {

    @Autowired
    private TarGrpService tarGrpService;

    @Autowired
    private EngineTestService engineTestService;

    @Autowired
    private MktVerbalConditionMapper mktVerbalConditionMapper;

    @Autowired(required = false)
    private MktCampaignApiService mktCampaignApiService;

    @Autowired
    private MktCamChlResultApiService mktCamChlResultApiService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired(required = false)
    private ISystemPostDubboService iSystemPostDubboService;

    @Autowired(required = false)
    private ISystemUserDtoDubboService iSystemUserDtoDubboService;
    @Autowired(required = false)
    private MktCampaignService mktCampaignService;
    @Autowired
    private LabelService labelService;
    @Autowired
    private MktStrategyConfRuleService ruleService;


    @PostMapping("test")
    @CrossOrigin
    public Object test(@RequestBody HashMap<String,Object> params) {
        Map<String,Object> result = new HashMap<>();
        try {
            Long ruleId = MapUtil.getLongNum(params.get("ruleId"));
            List<Long> list = (List<Long>) params.get("list");
            result = ruleService.test(ruleId,list);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    @PostMapping("importLabel")
    @CrossOrigin
    public Object importLabel(MultipartFile file) {
        Map<String,Object> result = new HashMap<>();
        try {
             result = labelService.importLabel(file);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @PostMapping("importLabelValue")
    @CrossOrigin
    public Object importLabelValue(MultipartFile file) {
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.importLabelValue(file);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    @PostMapping("searchByCampaignId")
    @CrossOrigin
    public Object searchByCampaignId(Long campaignId) {
        Map<String,Object> result = mktCampaignService.searchByCampaignId(campaignId);
        return result;
    }



    @PostMapping("userTest")
    @CrossOrigin
    public Object userTest() {
        Map<String,Object> resutl = new HashMap<>();
        SysmgrResultObject<SystemUserDto> systemUserDtoSysmgrResultObject = iSystemUserDtoDubboService.qrySystemUserDto(121119809L, new ArrayList<Long>());
        resutl.put("staffId",systemUserDtoSysmgrResultObject);
        SysmgrResultObject<SystemUserDto> sysUser = iSystemUserDtoDubboService.qrySystemUserDto(100010L, new ArrayList<Long>());
        resutl.put("sysUser",sysUser);

        return resutl;
    }


    @RequestMapping(value = "/redisTest", method = RequestMethod.POST)
    @CrossOrigin
    public String redis()  {
        List<String> codeList = new ArrayList<>();
        codeList.add("1234");
        codeList.add("123456");
        codeList.add("1gggg");
        codeList.add("1hjj");
        codeList.add("1kkk");
        List<String> code2List = new ArrayList<>();
        code2List.add("234");
        code2List.add("23456");
        code2List.add("2gggg");
        code2List.add("2hjj");
        code2List.add("2kkk");
        redisUtils.hset("LABEL_CODE_"+25,1+"",codeList);
        redisUtils.hset("LABEL_CODE_"+25,2+"",code2List);

        Object ob = redisUtils.hgetAllRedisList("LABEL_CODE_"+25);
        List<String> oblist = (List<String>) ob;
        System.out.println(oblist);

        System.out.println("==============================================");

        List<String> list = redisUtils.hgetAllField("LABEL_CODE_" + 25);
        System.out.println(list);
        return "success";
    }




    @RequestMapping(value = "/rule", method = RequestMethod.POST)
    @CrossOrigin
    public String getCityTable(@RequestBody  Map<String, Object> params) throws Exception {
        List<Integer> areaIds = (List<Integer>) params.get("areaIds");

        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("TYPE_4G_FLG","是");


        String exx = "if(TYPE_4G_FLG==\"是\") {return true} else {return false}";
        RuleResult ruleResult = runner.executeRule(exx, context, true, true);


        System.out.println("exx=" + exx);
        System.out.println("context=" + context);
        System.out.println("result=" + ruleResult.getResult());
        System.out.println("Tree=" + ruleResult.getRule().toTree());
        System.out.println("TraceMap=" + ruleResult.getTraceMap());



        return null;
    }

    /**
     * 协同中心活动详情接口测试
     * @param params
     * @return
     * @throws Exception
     */
/*    @RequestMapping(value = "/getMktCampaignApi", method = RequestMethod.POST)
    @CrossOrigin
    public String getMktCampaignApi(@RequestBody  Map<String, Object> params) throws Exception {
        Long mktCampaignId = Long.valueOf((String) params.get("mktCampaignId"));
        Map<String, Object> map = mktCampaignApiService.qryMktCampaignDetail(mktCampaignId);
        return JSON.toJSONString(map);
    }*/



    @RequestMapping(value = "/getMktCampaignApi", method = RequestMethod.POST)
    @CrossOrigin
    public String getMktCampaignApi(@RequestBody  Map<String, Object> params) throws Exception {
        Long mktCampaignId = Long.valueOf((Integer)params.get("mktCampaignId"));
        Map<String, Object> map = mktCampaignApiService.qryMktCampaignDetail(mktCampaignId);
       // return JSON.toJSONString(map);
       // redisUtils.setRedis("mktCampaignResp_test", map.get("mktCampaignResp"));
      //  MktCampaignResp mktCampaignResp = (MktCampaignResp) redisUtils.getRedis("mktCampaignResp_test");
      //  return mktCampaignResp;
        return JSON.toJSONString(map);
    }


    @RequestMapping(value = "/secondChannelSynergy", method = RequestMethod.POST)
    @CrossOrigin
    public String secondChannelSynergy(@RequestBody  Map<String, Object> params) throws Exception {
        Map<String, Object> map = mktCamChlResultApiService.secondChannelSynergy(params);
        return JSON.toJSONString(map);
    }

    @Autowired
    private EsHitsService esHitsService;

    @PostMapping("saveEsLogTest")
    @CrossOrigin
    public void saveEsLog() {
        esHitsService.save(null, "111");
    }


    @PostMapping("campaignDelayNotice")
    @CrossOrigin
    public void campaignDelayNotice() {
        mktCampaignService.campaignDelayNotice();
    }

    @Autowired(required = false)
    private TarGrpTemplateService tarGrpTemplateService;

    @PostMapping("tarGrpTemplateScheduledBatchIssue")
    @CrossOrigin
    public Map<String, Object> tarGrpTemplateScheduledBatchIssue() {
        return tarGrpTemplateService.tarGrpTemplateScheduledBatchIssue();
    }
    @RequestMapping(value = "/salesOffShelf", method = RequestMethod.POST)
    @CrossOrigin
    public String salesOffShelf(@RequestBody  Map<String, Object> params) throws Exception {
        Map<String, Object> map = mktCampaignApiService.salesOffShelf(new HashMap<>());
        return JSON.toJSONString(map);
    }

    @Autowired
    private ContactEvtService contactEvtService;
    // 刷新事件源接口与事件关系数据
    // 1.先刷事件源接口表，把所有的渠道都刷进事件源接口表，一个渠道一个事件源接口
    @PostMapping("xxxxxx1")
    @CrossOrigin
    public void xxxxxx1() {
        contactEvtService.xxxxxx1();
    }

    @PostMapping("xxxxxx2")
    @CrossOrigin
    public void xxxxxx2() {
        contactEvtService.xxxxxx2();
    }

    @PostMapping("xxxxxx3")
    @CrossOrigin
    public void xxxxxx3() {
        contactEvtService.xxxxxx3();
    }

    @RequestMapping(value = "/saveMktCamDesc", method = RequestMethod.POST)
    @CrossOrigin
    public String saveMktCamDesc() {
        Map<String, Object> map = mktCampaignService.saveMktCamDesc();
        return JSON.toJSONString(map);
    }

    @Autowired
    private MktCampaignMapper mktCampaignMapper;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @PostMapping("issuedSuccessMktCheck")
    @CrossOrigin
    public void issuedSuccessMktCheck() {
        scheduledTaskService.issuedSuccessMktCheck();
    }

    @Autowired
    private CamCpcSpecialLogic camCpcSpecialLogic;

    @PostMapping("onlineScanCodeOrCallPhone4Home")
    @CrossOrigin
    public String onlineScanCodeOrCallPhone4Home(@RequestBody  HashMap<String, Object> params) {
        return camCpcSpecialLogic.onlineScanCodeOrCallPhone4Home(params);
    }


    @Test
    public void SSSSSSS() {
        /*Integer rate = 1;
        String handleRateString = "0.00012";
        Double handleRate = Double.valueOf(handleRateString);
        System.out.println(handleRate * 100 < rate);*/
        // scheduledTaskService.issuedSuccessMktCheck();

        Object newObj = null;
        String s17 = "%C2%AC%C3%AD%00%05t%00%3Eif%28%28PROM_AGREE_EXP_TYPE%3E%3D1%29%29+%7Breturn+true%7D+else+%7Breturn+false%7D";
        String s18 = "%C2%AC%C3%AD%00%05t%00%C2%9F%5B%7B%22labelCode%22%3A%22PROM_AGREE_EXP_TYPE%22%2C%22labelDataType%22%3A%221300%22%2C%22labelName%22%3A%22%C3%A5%C2%8D%C2%8F%C3%A8%C2%AE%C2%AE%C3%A5%C2%88%C2%B0%C3%A6%C2%9C%C2%9F%C3%A6%C2%97%C2%B6%C3%A9%C2%95%C2%BF%C3%AF%C2%BC%C2%88%C3%A6%C2%9C%C2%88%C3%AF%C2%BC%C2%89%22%2C%22operType%22%3A%225000%22%2C%22rightOperand%22%3A%222000%22%2C%22rightParam%22%3A%221%22%7D%5D";
        String s19 = "%C2%AC%C3%AD%00%05t%00%3Eif%28%28PROM_AGREE_EXP_TYPE%3E%3D6%29%29+%7Breturn+true%7D+else+%7Breturn+false%7D";
        try {
            if(s18 != null) {
                String redStr = java.net.URLDecoder.decode(s19, "UTF-8");
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                newObj = objectInputStream.readObject();
                System.out.println(newObj.toString());
               /* String[] split = newObj.toString().split(",");
                List<String> list = Arrays.asList(split);
                System.out.println(!list.contains(String.valueOf(33823L)));*/
                objectInputStream.close();
                byteArrayInputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}


