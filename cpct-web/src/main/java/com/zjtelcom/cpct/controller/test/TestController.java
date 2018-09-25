package com.zjtelcom.cpct.controller.test;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.rule.RuleResult;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.channel.MktVerbalConditionMapper;
import com.zjtelcom.cpct.dao.channel.PpmProductMapper;
import com.zjtelcom.cpct.service.EngineTestService;
import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.dto.grouping.TarGrpDetail;
import com.zjtelcom.cpct.service.campaign.MktCampaignApiService;
import com.zjtelcom.cpct.service.grouping.TarGrpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private MktCampaignApiService mktCampaignApiService;


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



    @RequestMapping(value = "/getMktCampaignApi", method = RequestMethod.POST)
    @CrossOrigin
    public String getMktCampaignApi(@RequestBody  Map<String, Object> params) throws Exception {
        Long mktCampaignId = Long.valueOf((Integer)params.get("mktCampaignId"));
        Map<String, Object> map = mktCampaignApiService.qryMktCampaignDetail(mktCampaignId);
        return JSON.toJSONString(map);
    }

}


