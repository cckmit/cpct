package com.zjtelcom.cpct.controller.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ctzj.smt.bss.sysmgr.model.common.Page;
import com.ctzj.smt.bss.sysmgr.model.common.SysmgrResultObject;
import com.ctzj.smt.bss.sysmgr.model.dataobject.SystemPost;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.ctzj.smt.bss.sysmgr.model.query.QrySystemPostReq;
import com.ctzj.smt.bss.sysmgr.privilege.service.dubbo.api.ISystemPostDubboService;
import com.ctzj.smt.bss.sysmgr.privilege.service.dubbo.api.ISystemUserDtoDubboService;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.rule.RuleResult;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.channel.MktVerbalConditionMapper;
import com.zjtelcom.cpct.dao.channel.ObjMktCampaignRelMapper;
import com.zjtelcom.cpct.domain.channel.ObjMktCampaignRel;
import com.zjtelcom.cpct.domain.channel.RequestInstRel;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRule;
import com.zjtelcom.cpct.service.EngineTestService;
import com.zjtelcom.cpct.service.campaign.CampaignService;
import com.zjtelcom.cpct.service.campaign.MktCamChlResultApiService;
import com.zjtelcom.cpct.service.campaign.MktCampaignApiService;
import com.zjtelcom.cpct.service.campaign.MktCampaignService;
import com.zjtelcom.cpct.service.channel.LabelService;
import com.zjtelcom.cpct.service.es.EsHitsService;
import com.zjtelcom.cpct.service.grouping.TarGrpService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfRuleService;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.MapUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.UserUtil;
import com.zjtelcom.cpct_offer.dao.inst.RequestInstRelMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.STATUSCD_EFFECTIVE;


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

    @Autowired
    private CampaignService campaignService;

    @PostMapping("campaignDelayNotice")
    @CrossOrigin
    public void campaignDelayNotice() {
        campaignService.campaignDelayNotice();
    }


}


