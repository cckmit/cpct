package com.zjtelcom.cpct.controller.test;

import com.alibaba.fastjson.JSON;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.rule.RuleResult;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.channel.MktVerbalConditionMapper;
import com.zjtelcom.cpct.domain.channel.RequestInstRel;
import com.zjtelcom.cpct.service.EngineTestService;
import com.zjtelcom.cpct.service.campaign.MktCamChlResultApiService;
import com.zjtelcom.cpct.service.campaign.MktCampaignApiService;
import com.zjtelcom.cpct.service.grouping.TarGrpService;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct_offer.dao.inst.RequestInstRelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
    private RequestInstRelMapper requestInstRelMapper;

    @PostMapping("fourthDataSource")
    @CrossOrigin
    public Object fourthDataSource() {
        RequestInstRel requestInstRel = requestInstRelMapper.selectByPrimaryKey(1L);
        return requestInstRel;
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
}


