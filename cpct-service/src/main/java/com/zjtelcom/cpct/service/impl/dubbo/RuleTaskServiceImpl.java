package com.zjtelcom.cpct.service.impl.dubbo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ctzj.smt.bss.cache.service.api.CacheEntityApi.ICacheOfferEntityQryService;
import com.ctzj.smt.bss.cache.service.api.CacheEntityApi.ICacheRelEntityQryService;
import com.ctzj.smt.bss.cache.service.api.CacheIndexApi.ICacheOfferRelIndexQryService;
import com.ctzj.smt.bss.cache.service.api.CacheIndexApi.ICacheProdIndexQryService;
import com.ctzj.smt.bss.cache.service.api.model.CacheResultObject;
import com.ctzj.smt.bss.customer.model.dataobject.OfferInst;
import com.ctzj.smt.bss.customer.model.dataobject.OfferProdInstRel;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import com.ql.util.express.rule.RuleResult;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.domain.campaign.MktCamChlConfDO;
import com.zjtelcom.cpct.domain.campaign.MktCamItem;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.CamScript;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.domain.channel.LabelResult;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.channel.VerbalVO;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.elastic.config.IndexList;
import com.zjtelcom.cpct.elastic.service.EsHitService;
import com.zjtelcom.cpct.service.dubbo.RuleTaskService;
import com.zjtelcom.cpct.service.es.CoopruleService;
import com.zjtelcom.cpct.service.es.EsHitsService;
import com.zjtelcom.cpct.service.event.EventRedisService;
import com.zjtelcom.cpct.service.system.SysParamsService;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.ThreadPool;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zjtelcom.cpct.enums.Operator.BETWEEN;

@Service
@Transactional
public class RuleTaskServiceImpl implements RuleTaskService,Callable {

    private static final Logger log = LoggerFactory.getLogger(RuleTaskServiceImpl.class);

    private Long strategyConfId; //策略配置idS
    private String strategyConfName; //策略配置id
    private Long tarGrpId;
    private String productStr;
    private String evtContactConfIdStr;
    private String reqId;
    private Long ruleId;
    private String ruleName;
    private Map<String, String> params;
    private Map<String, String> privateParams;
    private DefaultContext<String, Object> context;
    private String lanId;
    private EsHitsService esHitService;  //es存储
    private RedisUtils redisUtils;  // redis方法
    private YzServ yzServ; //因子实时查询dubbo服务
    private ICacheOfferEntityQryService iCacheOfferEntityQryService; // 查询销售品实例缓存实体
    private ICacheOfferRelIndexQryService iCacheOfferRelIndexQryService;
    private ICacheRelEntityQryService iCacheRelEntityQryService;
    private ICacheProdIndexQryService iCacheProdIndexQryService;
    private SysParamsService sysParamsService;
    private CoopruleService coopruleService;
    private EventRedisService eventRedisService;


    /**
     * 获取规则列表（规则级）
     */
    public RuleTaskServiceImpl(HashMap<String, Object> hashMap){
        this.strategyConfId = (Long) hashMap.get("strategyConfId");
        this.strategyConfName = (String) hashMap.get("strategyConfName");
        this.tarGrpId = (Long) hashMap.get("tarGrpId");
        this.reqId = (String) hashMap.get("reqId");
        this.productStr =(String) hashMap.get("productId");
        this.evtContactConfIdStr = (String) hashMap.get("evtContactConfId");
        this.ruleId = (Long) hashMap.get("ruleId");
        this.ruleName = (String) hashMap.get("ruleName");
        this.params = (Map<String, String>)hashMap.get("params");
        this.privateParams = (Map<String, String>)hashMap.get("privateParams");
        this.context = (DefaultContext<String, Object>)hashMap.get("context");
        this.lanId = (String) hashMap.get("lanId");
        this.redisUtils = (RedisUtils) hashMap.get("redisUtils");
        this.yzServ = (YzServ) hashMap.get("yzServ");
        this.iCacheOfferEntityQryService = (ICacheOfferEntityQryService) hashMap.get("iCacheOfferEntityQryService");
        this.iCacheOfferRelIndexQryService = (ICacheOfferRelIndexQryService) hashMap.get("iCacheOfferRelIndexQryService");
        this.iCacheRelEntityQryService = (ICacheRelEntityQryService) hashMap.get("iCacheRelEntityQryService");
        this.iCacheProdIndexQryService = (ICacheProdIndexQryService) hashMap.get("iCacheProdIndexQryService");
        this.sysParamsService = (SysParamsService) hashMap.get("sysParamsService");
        this.coopruleService = (CoopruleService) hashMap.get("coopruleService");
        this.eventRedisService = (EventRedisService) hashMap.get("eventRedisService");
        this.esHitService = (EsHitsService) hashMap.get("esHitService");
    }


//    @Value("${table.infallible}")
    private String defaultInfallibleTable = "CPCP_CAM_DEFAULT";

//    @Autowired
//    private EsHitsService esHitService;  //es存储
//
//    @Autowired
//    private RedisUtils redisUtils;  // redis方法
//
//    @Autowired(required = false)
//    private YzServ yzServ; //因子实时查询dubbo服务
//
//    @Autowired(required = false)
//    private ICacheOfferEntityQryService iCacheOfferEntityQryService; // 查询销售品实例缓存实体
//
//    @Autowired(required = false)
//    private ICacheOfferRelIndexQryService iCacheOfferRelIndexQryService; // 根据offerInstId和statusCd(1000-有效)查询offerProdInstRelId
//
//    @Autowired(required = false)
//    private ICacheRelEntityQryService iCacheRelEntityQryService;
//
//    @Autowired(required = false)
//    private ICacheProdIndexQryService iCacheProdIndexQryService;
//
//    @Autowired
//    private SysParamsService sysParamsService;
//
//    @Autowired
//    private CoopruleService coopruleService;
//
//    @Autowired
//    private EventRedisService eventRedisService;

    Map<String, Boolean> flagMap = new ConcurrentHashMap();


    @Override
    public Object call() throws Exception {
        Map<String, Object> nonPassedMsg = new HashMap<>();
        long begin = System.currentTimeMillis();

        //初始化es log   标签使用
        JSONObject esJson = new JSONObject();
        //初始化es log   规则使用
        JSONObject jsonObject = new JSONObject();

        // 获取 ASSI_PROM_INTEG_ID标签
        String promIntegId = "";
        if (context.get("ASSI_PROM_INTEG_ID") != null) {
            promIntegId = (String) context.get("ASSI_PROM_INTEG_ID");
        }
        MktCampaignDO mktCampaignDO = new MktCampaignDO();
        MktStrategyConfDO mktStrategyConfDO = new MktStrategyConfDO();
        MktStrategyConfRuleDO mktStrategyConfRuleDO = new MktStrategyConfRuleDO();
        try {
            Long activityId = Long.valueOf(privateParams.get("activityId"));
            Map<String, Object> mktCampaignRedis = eventRedisService.getRedis("MKT_CAMPAIGN_", activityId);

            if (mktCampaignRedis != null) {
                mktCampaignDO = (MktCampaignDO) mktCampaignRedis.get("MKT_CAMPAIGN_" + activityId);
            }

            Map<String, Object> mktStrategyConfRedis = eventRedisService.getRedis("MKT_STRATEGY_", strategyConfId);
            if (mktStrategyConfRedis != null) {
                mktStrategyConfDO = (MktStrategyConfDO) mktStrategyConfRedis.get("MKT_STRATEGY_" + strategyConfId);
            }

            Map<String, Object> mktStrategyConfRuleRedis = eventRedisService.getRedis("MKT_RULE_", ruleId);
            if (mktStrategyConfRuleRedis != null) {
                mktStrategyConfRuleDO = (MktStrategyConfRuleDO) mktStrategyConfRuleRedis.get("MKT_RULE_" + ruleId);
            }

            jsonObject.put("ruleId", mktStrategyConfRuleDO.getInitId());
            jsonObject.put("ruleName", ruleName);
            jsonObject.put("hitEntity", privateParams.get("accNbr")); //命中对象
            jsonObject.put("reqId", reqId);
            jsonObject.put("eventId", params.get("eventCode"));
            jsonObject.put("activityId", mktCampaignDO.getMktCampaignId());
            jsonObject.put("strategyConfId", mktStrategyConfDO.getMktStrategyConfId());
            jsonObject.put("productStr", productStr);
            jsonObject.put("evtContactConfIdStr", evtContactConfIdStr);
            jsonObject.put("tarGrpId", tarGrpId);
            jsonObject.put("promIntegId", promIntegId);


            //ES log 标签实例
            esJson.put("reqId", reqId);
            esJson.put("eventCode", params.get("eventCode"));
            esJson.put("activityId", mktCampaignDO.getMktCampaignId());
            esJson.put("ruleId", mktStrategyConfRuleDO.getMktStrategyConfRuleId());
            esJson.put("ruleName", ruleName);
            esJson.put("integrationId", params.get("integrationId"));
            esJson.put("accNbr", params.get("accNbr"));
            esJson.put("strategyConfId", mktStrategyConfDO.getInitId());
            esJson.put("tarGrpId", tarGrpId);
            esJson.put("promIntegId", promIntegId);
            esJson.put("hitEntity", privateParams.get("accNbr")); //命中对象
        } catch (NumberFormatException e) {
            jsonObject.put("hit", "false");
            jsonObject.put("msg", "类型转换异常" + e.getMessage());
            esHitService.save(jsonObject, IndexList.RULE_MODULE);
            e.printStackTrace();
            // return Collections.EMPTY_MAP;
            nonPassedMsg.put("rule_" + ruleId, "类型转换异常");
            return nonPassedMsg;
        }

        Map<String, Object> ruleMap = new ConcurrentHashMap<>();
        //初始化返回结果中的推荐信息列表
        List<Map<String, Object>> taskChlList = new ArrayList<>();

        //  2.判断客户分群规则---------------------------
        //判断匹配结果，如匹配则向下进行，如不匹配则continue结束本次循环
        //拼装redis key
        ExpressRunner runner = new ExpressRunner();
        runner.addFunction("toNum", new StringToNumOperator("toNum"));
        log.info("12345");
        //如果分群id为空
        if (tarGrpId == null) {
            jsonObject.put("hit", "false");
            jsonObject.put("msg", "分群ID异常");
            esHitService.save(jsonObject, IndexList.RULE_MODULE);
            // return Collections.EMPTY_MAP;
            nonPassedMsg.put("rule_" + ruleId, "分群ID异常");
            return nonPassedMsg;
        }

        //记录实例不足的标签
        StringBuilder notEnoughLabel = new StringBuilder();

        // 销售品验证
        String realProdFilter = null;
        try {
            Map<String, Object> realProdFilterRedis = eventRedisService.getRedis("REAL_PROD_FILTER");
            if (realProdFilterRedis != null) {
                realProdFilter = (String) realProdFilterRedis.get("REAL_PROD_FILTER");
            }
            if (realProdFilter != null && "1".equals(realProdFilter) && context.get("PROM_LIST") != null) {
                List<String> prodList = new ArrayList<>();
                log.info("111------accNbr --->" + privateParams.get("accNbr"));
                CacheResultObject<Set<String>> prodInstIdsObject = iCacheProdIndexQryService.qryProdInstIndex2(privateParams.get("accNbr"));
                if (prodInstIdsObject != null && prodInstIdsObject.getResultObject() != null) {
                    Set<String> prodInstIds = prodInstIdsObject.getResultObject();
                    for (String prodInstId : prodInstIds) {
                        // 根据prodInstId 和 statusCd(1000-有效)查询offerProdInstRelId
                        CacheResultObject<Set<String>> setCacheResultObject = iCacheOfferRelIndexQryService.qryOfferProdInstRelIndex2(prodInstId, "1000");
                        if (setCacheResultObject != null && setCacheResultObject.getResultObject() != null) {
                            Set<String> offerProdInstRelIdSet = setCacheResultObject.getResultObject();
                            for (String offerProdInstRelId : offerProdInstRelIdSet) {
                                // 查询销售品产品实例关系缓存实体
                                CacheResultObject<OfferProdInstRel> offerProdInstRelCacheEntity = iCacheRelEntityQryService.getOfferProdInstRelCacheEntity(offerProdInstRelId);
                                if (offerProdInstRelCacheEntity != null && offerProdInstRelCacheEntity.getResultObject() != null) {
                                    OfferProdInstRel offerProdInstRel = offerProdInstRelCacheEntity.getResultObject();
                                    // 查询销售品实例缓存实体
                                    CacheResultObject<OfferInst> offerInstCacheEntity = iCacheOfferEntityQryService.getOfferInstCacheEntity(offerProdInstRel.getOfferInstId().toString());
                                    if (offerInstCacheEntity != null && offerInstCacheEntity.getResultObject() != null) {
                                        OfferInst offerInst = offerInstCacheEntity.getResultObject();
                                        prodList.add(offerInst.getOfferId().toString());
                                    }
                                }
                            }
                        }
                    }
                }
                String productString = ChannelUtil.StringList2String(prodList);
                log.info("999------productStr --->" + JSON.toJSONString(productStr));
                context.put("PROM_LIST", productString);
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("hit", "false");
            jsonObject.put("msg", "销售品过滤查询异常" + e.getMessage());
            esHitService.save(jsonObject, IndexList.RULE_MODULE);
            // return Collections.EMPTY_MAP;
            nonPassedMsg.put("rule_" + ruleId, "销售品过滤查询异常");
            return nonPassedMsg;
        }

        jsonObject.put("msg", "实时接入自定义时间类型标签值，那就不能拿缓存，只能实时拼接");
        esHitService.save(jsonObject, IndexList.RULE_MODULE);

        // ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
        // ！！！实时接入自定义时间类型标签值，那就不能拿缓存，只能实时拼接！！！
        // ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！

        String express = null;
        Object datetypeTargouidList = new Object();
        SysParams sysParams = null;
        try {
            Map<String, Object> datetypeRedis = eventRedisService.getRedis("DATETYPE_TARGOUID_LIST");
            if (datetypeRedis != null) {
                datetypeTargouidList = datetypeRedis.get("DATETYPE_TARGOUID_LIST");
            }

            if (datetypeTargouidList != null) {
                String[] timeTypeTarGrpIdList = datetypeTargouidList.toString().split(",");
                List<String> list = Arrays.asList(timeTypeTarGrpIdList);
                if (!list.contains(tarGrpId)) {
                    //判断表达式在缓存中有没有
                    express = (String) redisUtils.get("EXPRESS_" + tarGrpId);
                }
            }
            Map<String, Object> checkLabelRedis = eventRedisService.getRedis("CHECK_LABEL");
            sysParams = new SysParams();
            if (checkLabelRedis != null) {
                sysParams = (SysParams) checkLabelRedis.get("CHECK_LABEL");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("hit", "false");
            jsonObject.put("msg", "表达式缓存查询失败" + e.getMessage());
            esHitService.save(jsonObject, IndexList.RULE_MODULE);
        }
        if (express == null || "".equals(express)) {
            List<LabelResult> labelResultList = new ArrayList<>();
            try {
                LabelResult lr;
                //查询规则下所有标签
                List<Map<String, String>> labelMapList = new ArrayList<>();
                try {
                    Map<String, Object> ruleAllLabelRedis = eventRedisService.getRedis("RULE_ALL_LABEL_", tarGrpId);
                    if (ruleAllLabelRedis != null) {
                        labelMapList = (List<Map<String, String>>) ruleAllLabelRedis.get("RULE_ALL_LABEL_" + tarGrpId);
                    }
                } catch (Exception e) {
                    jsonObject.put("hit", "false");
                    jsonObject.put("msg", "规则下标签查询失败");
                    esHitService.save(jsonObject, IndexList.RULE_MODULE);
                    nonPassedMsg.put("rule_" + ruleId, "规则下标签查询失败");
                    return nonPassedMsg;
                }
                if (labelMapList == null || labelMapList.size() <= 0) {
                    log.info("未查询到分群标签:" + privateParams.get("activityId") + "---" + ruleId);
                    jsonObject.put("hit", "false");
                    jsonObject.put("msg", "未查询到分群标签");
                    esHitService.save(jsonObject, IndexList.RULE_MODULE);
                    nonPassedMsg.put("rule_" + ruleId, "未查询到分群标签");
                    return nonPassedMsg;
                }

                //将规则拼装为表达式
                StringBuilder expressSb = new StringBuilder();
                expressSb.append("if(");
                //遍历所有规则
                for (Map<String, String> labelMap : labelMapList) {
                    if (defaultInfallibleTable.equals(labelMap.get("code"))) {
                        redisUtils.hset("LEFT_PARAM_FLAG" + strategyConfId, ruleId.toString(), "1");
                        flagMap.put(ruleId.toString(), true);
                        log.info(Thread.currentThread().getName() + "flag = true进入...");
                        expressSb.append("true&&");
                        continue;
                    }
                    String type = labelMap.get("operType");
                    //保存标签的es log
                    lr = new LabelResult();
                    if ("PROM_LIST".equals(labelMap.get("code")) && "1".equals(realProdFilter)) {
                        Long filterRuleId = Long.valueOf(labelMap.get("rightParam"));
                        Map<String, Object> filterRuleRedis = eventRedisService.getRedis("FILTER_RULE_", filterRuleId);
                        FilterRule filterRule = null;
                        if (filterRuleRedis != null) {
                            filterRule = (FilterRule) filterRuleRedis.get("FILTER_RULE_" + filterRuleId);
                        }

                        if (filterRule != null) {
                            String checkProduct = filterRule.getChooseProduct();
                            String s = sysParamsService.systemSwitch("PRODUCT_FILTER_SWITCH");
                            if (s != null && s.equals("code")) {
                                checkProduct = filterRule.getChooseProductCode();
                            }
                            lr.setRightOperand(checkProduct);
                            labelMap.put("rightParam", checkProduct);
                        } else {
                            jsonObject.put("hit", "false");
                            jsonObject.put("msg", "未查询销售品过滤规则");
                        }
                    } else {
                        lr.setRightOperand(labelMap.get("rightParam"));
                    }

                    lr.setOperType(type);
                    lr.setLabelCode(labelMap.get("code"));
                    lr.setLabelName(labelMap.get("name"));
                    lr.setClassName(labelMap.get("className"));

                    //拼接表达式：主表达式
                    expressSb.append(cpcExpression(labelMap));

                    //判断标签实例是否足够
                    if (context.containsKey(labelMap.get("code"))) {
                        lr.setRightParam(context.get(labelMap.get("code")).toString());
                        if (sysParams != null && "1".equals(sysParams.getParamValue())) {
                            try {
                                RuleResult ruleResultOne = runner.executeRule(cpcLabel(labelMap.get("code"), type, labelMap.get("rightParam")), context, true, true);
                                if (null != ruleResultOne.getResult()) {
                                    lr.setResult((Boolean) ruleResultOne.getResult());
                                } else {
                                    lr.setResult(false);
                                }
                            } catch (Exception e) {
                                lr.setResult(false);
                            }
                        }
                    } else {
                        notEnoughLabel.append(labelMap.get("code")).append(",");
                        lr.setRightParam("无值");
                        lr.setResult(false);
                    }
                    expressSb.append("&&");
                    labelResultList.add(lr);
                }
                expressSb.delete(expressSb.length() - 2, expressSb.length());
                expressSb.append(") {return true} else {return false}");
                express = expressSb.toString();

            } catch (Exception e) {
                e.printStackTrace();
                jsonObject.put("hit", "false");
                jsonObject.put("msg", "表达式拼接异常");
                esHitService.save(jsonObject, IndexList.RULE_MODULE);
                // return Collections.EMPTY_MAP;
                nonPassedMsg.put("rule_" + ruleId, "表达式拼接异常");
                return nonPassedMsg;
            }
            //表达式存入redis
            redisUtils.set("EXPRESS_" + tarGrpId, express);
            esJson.put("labelResultList", JSONArray.toJSON(labelResultList));
        } else {
            List<LabelResult> labelResultList = new ArrayList<>();
            try {
                LabelResult lr;
                //查询规则下所有标签
                List<Map<String, String>> labelMapList = new ArrayList<>();
                try {
                    Map<String, Object> ruleAllLabelRedis = eventRedisService.getRedis("RULE_ALL_LABEL_", tarGrpId);
                    if (ruleAllLabelRedis != null) {
                        labelMapList = (List<Map<String, String>>) ruleAllLabelRedis.get("RULE_ALL_LABEL_" + tarGrpId);
                    }
                } catch (Exception e) {
                    jsonObject.put("hit", "false");
                    jsonObject.put("msg", "规则下标签查询失败");
                    esHitService.save(jsonObject, IndexList.RULE_MODULE);
                    nonPassedMsg.put("rule_" + ruleId, "规则下标签查询失败");
                    return nonPassedMsg;
                }

                //遍历所有规则
                for (Map<String, String> labelMap : labelMapList) {
                    if (defaultInfallibleTable.equals(labelMap.get("code"))) {
                        redisUtils.hset("LEFT_PARAM_FLAG" + strategyConfId, ruleId.toString(), "1");
                        flagMap.put(ruleId.toString(), true);
                        log.info("flag = true进入...");
                        continue;
                    }
                    //判断标签实例是否足够
                    if (!context.containsKey(labelMap.get("code"))) {
                        notEnoughLabel.append(labelMap.get("code")).append(",");
                    }
                }
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("esJson",esJson);
                hashMap.put("labelMapList",labelMapList);
                hashMap.put("context",context);
                hashMap.put("sysParams",sysParams);
                hashMap.put("runner",runner);
                hashMap.put("labelResultList",labelResultList);
                hashMap.put("sysParamsService",sysParamsService);
                hashMap.put("esHitService",esHitService);
                hashMap.put("eventRedisService",eventRedisService);
                // 异步执行当个标签比较结果
//                new CamCpcServiceImpl.labelResultThread(esJson, labelMapList, context, sysParams, runner, labelResultList).run();
                 ThreadPool.execute(new labelResultServiceImpl(hashMap));

            } catch (Exception e) {
                e.printStackTrace();
                jsonObject.put("hit", "false");
                jsonObject.put("msg", "表达式拼接异常");
                esHitService.save(jsonObject, IndexList.RULE_MODULE);
                nonPassedMsg.put("rule_" + ruleId, "表达式拼接异常");
                return nonPassedMsg;
            }
        }

        esHitService.save(esJson, IndexList.Label_MODULE);  //储存标签比较结果
        try {
            RuleResult ruleResult = new RuleResult();
            //初始化返回结果中的销售品条目
            List<Map<String, String>> productList = new ArrayList<>();
            // if(flagMap.get(ruleId.toString()) == false) {
            //验证是否标签实例不足
            if (notEnoughLabel.length() > 0) {
                // log.info("notEnoughLabel.length() > 0->标签实例不足");
                jsonObject.put("hit", "false");
                jsonObject.put("msg", "标签实例不足：" + notEnoughLabel.toString());
                esHitService.save(jsonObject, IndexList.RULE_MODULE);
                // return Collections.EMPTY_MAP;
                nonPassedMsg.put("rule_" + ruleId, "标签实例不足：" + notEnoughLabel.toString());
                return nonPassedMsg;
            }

            //规则引擎计算
            ExpressRunner runnerQ = new ExpressRunner();
            runnerQ.addFunction("toNum", new StringToNumOperator("toNum"));
            runnerQ.addFunction("checkProm", new PromCheckOperator("checkProm"));
            runnerQ.addFunction("dateLabel", new ComperDateLabel("dateLabel"));
            // 固定必中默认值设置为1
            context.put("CPCP_CAM_DEFAULT", "1");

            try {
                ruleResult = runnerQ.executeRule(express, context, true, true);
            } catch (Exception e) {
                ruleMap.put("msg", "规则引擎计算失败");
                jsonObject.put("hit", "false");
                jsonObject.put("msg", "规则引擎计算失败");
                esHitService.save(jsonObject, IndexList.RULE_MODULE);
                // return Collections.EMPTY_MAP;
                nonPassedMsg.put("rule_" + ruleId, "规则引擎计算失败");
                return nonPassedMsg;
            }

            jsonObject.put("express", express);

            if (ruleResult.getResult() != null && ((Boolean) ruleResult.getResult())) {
                jsonObject.put("hit", true);

                //拼接返回结果
                ruleMap.put("orderISI", params.get("reqId")); //流水号
                ruleMap.put("activityId", mktCampaignDO.getInitId().toString()); //活动编码
                ruleMap.put("activityName", privateParams.get("activityName")); //活动名称
                ruleMap.put("activityType", privateParams.get("activityType")); //活动类型
                ruleMap.put("activityStartTime", privateParams.get("activityStartTime")); //活动开始时间
                ruleMap.put("activityEndTime", privateParams.get("activityEndTime")); //活动结束时间
                ruleMap.put("skipCheck", "0"); //todo 调过预校验
                ruleMap.put("orderPriority", privateParams.get("orderPriority")); //活动优先级
                ruleMap.put("integrationId", privateParams.get("integrationId")); //集成编号（必填）
                ruleMap.put("accNbr", privateParams.get("accNbr")); //业务号码（必填）
                ruleMap.put("policyId", mktStrategyConfDO.getInitId().toString()); //策略编码
                ruleMap.put("policyName", strategyConfName); //策略名称
                ruleMap.put("ruleId", mktStrategyConfRuleDO.getInitId().toString()); //规则编码
                ruleMap.put("nowRuleId", ruleId); //新规则编码
                ruleMap.put("ruleName", ruleName); //规则名称
                ruleMap.put("promIntegId", promIntegId); // 销售品实例ID
                ruleMap.put("isMarketRule", flagMap.get(ruleId.toString()) == true ? "0" : "1"); // 是否随销规则标识
                if (context.get("AREA_ID") != null) {
                    ruleMap.put("areaId", context.get("AREA_ID")); // 落地网格
                }


                //查询销售品列表
                if (productStr != null && !"".equals(productStr)) {
                    // 推荐条目集合存入redis
                    Map<String, Object> params = new HashMap<>();
                    params.put("productStr", productStr);
                    Map<String, Object> mktCamItemListRedis = eventRedisService.getRedis("MKT_CAM_ITEM_LIST_", ruleId, params);
                    List<MktCamItem> mktCamItemList = new ArrayList<>();
                    if (mktCamItemListRedis != null) {
                        mktCamItemList = (List<MktCamItem>) mktCamItemListRedis.get("MKT_CAM_ITEM_LIST_" + ruleId);
                    }

                    for (MktCamItem mktCamItem : mktCamItemList) {
                        Map<String, String> product = new ConcurrentHashMap<>();
                        product.put("productId", mktCamItem.getItemId().toString());
                        product.put("productCode", mktCamItem.getOfferCode());
                        product.put("productName", mktCamItem.getOfferName());
                        product.put("productType", mktCamItem.getItemType());
                        product.put("productFlag", "1000");  //todo 销售品标签
                        //销售品优先级
                        if (mktCamItem.getPriority() != null) {
                            product.put("productPriority", mktCamItem.getPriority().toString());
                        } else {
                            product.put("productPriority", "0");
                        }
                        productList.add(product);
                    }
                }
                jsonObject.put("productList", productList);

                if (ruleResult.getResult() == null) {
                    ruleResult.setResult(false);
                }

                //获取协同渠道所有id
                String[] evtContactConfIdArray = evtContactConfIdStr.split("/");

                //判断需要返回的渠道
                Map<String, Object> param = new HashMap<>();
                param.put("channelCode", params.get("channelCode"));
                Map<String, Object> channelCodeRedis = eventRedisService.getRedis("CHANNEL_CODE_", param);
                Channel channelMessage = new Channel();
                if (channelCodeRedis != null) {
                    channelMessage = (Channel) channelCodeRedis.get("CHANNEL_CODE_" + params.get("channelCode"));
                }

                String channelCode = null;

                //新增加入reids -- linchao
                Map<String, Object> params = new HashMap<>();
                params.put("evtContactConfIdArray", evtContactConfIdArray);
                Map<String, Object> mktCamChlConfListRedis = eventRedisService.getRedis("MKT_CAMCHL_CONF_LIST_", ruleId, params);
                List<MktCamChlConfDO> mktCamChlConfDOS = new ArrayList<>();
                if (mktCamChlConfListRedis != null) {
                    mktCamChlConfDOS = (List<MktCamChlConfDO>) mktCamChlConfListRedis.get("MKT_CAMCHL_CONF_LIST_" + ruleId);
                }

                for (MktCamChlConfDO mktCamChlConfDO : mktCamChlConfDOS) {
                    if (mktCamChlConfDO.getContactChlId().equals(channelMessage.getContactChlId())) {
                        channelCode = mktCamChlConfDO.getEvtContactConfId().toString();
                        break;
                    }
                }

                //初始化结果集
                List<Future<Map<String, Object>>> threadList = new ArrayList<>();
                //初始化线程池
                ExecutorService executorService = Executors.newCachedThreadPool();

                try {
                    //遍历协同渠道
                    if (channelCode != null) {
                        Long evtContactConfId = Long.parseLong(channelCode);
                        //提交线程
                        //Future<Map<String, Object>> f = executorService.submit(new ChannelTask(evtContactConfId, productList, context, reqId));
                        //将线程处理结果添加到结果集
                        //threadList.add(f);
                        Map<String, Object> channelMap = ChannelTask(evtContactConfId, productList, context, reqId, nonPassedMsg, ruleId);
                        if (channelMap.containsKey("rule_" + ruleId)) {
                            nonPassedMsg.put("rule_" + ruleId, channelMap.remove("rule_" + ruleId));
                        } else {
                            taskChlList.add(channelMap);
                        }
                    } else {
                        if (evtContactConfIdArray != null && !"".equals(evtContactConfIdArray[0])) {
                            for (String str : evtContactConfIdArray) {
                                //协同渠道规则表id（自建表）
                                Long evtContactConfId = Long.parseLong(str);
                                //提交线程
                                //Future<Map<String, Object>> f = executorService.submit(new ChannelTask(evtContactConfId, productList, context, reqId));
                                //将线程处理结果添加到结果集
                                //threadList.add(f);
                                Map<String, Object> channelMap = ChannelTask(evtContactConfId, productList, context, reqId, nonPassedMsg, ruleId);
                                if (channelMap != null && !channelMap.isEmpty()) {
                                    if (channelMap.containsKey("rule_" + ruleId)) {
                                        nonPassedMsg.put("rule_" + ruleId, channelMap.remove("rule_" + ruleId));
                                    } else {
                                        taskChlList.add(channelMap);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //发生异常关闭线程池
                    executorService.shutdownNow();
                } finally {
                    //关闭线程池
                    executorService.shutdownNow();
                }
            } else {
                ruleMap.put("msg", "规则引擎匹配未通过");
                jsonObject.put("hit", "false");
                jsonObject.put("msg", "规则引擎匹配未通过");
                esHitService.save(jsonObject, IndexList.RULE_MODULE);
                // return Collections.EMPTY_MAP;
                nonPassedMsg.put("rule_" + ruleId, "规则引擎匹配未通过");
                return nonPassedMsg;
            }
            ruleMap.put("taskChlList", taskChlList);
            if (taskChlList.size() > 0) {
                jsonObject.put("hit", true);
            } else {
                jsonObject.put("hit", false);
                jsonObject.put("msg", "渠道均未命中");
                esHitService.save(jsonObject, IndexList.RULE_MODULE);
                // return Collections.EMPTY_MAP;
                nonPassedMsg.put("rule_" + ruleId, "渠道均未命中");
                return nonPassedMsg;
            }


            //受理规则校验 开关
            String isaleCheck = eventRedisService.getRedis("ISALE_CHECK_FLG") == null ? "0" : eventRedisService.getRedis("ISALE_CHECK_FLG").toString();
            //isale预校验固定参数
            String loginId = "";
            Map<String, Object> coolLoginIdRedis = eventRedisService.getRedis("COOL_LOGIN_ID");
            List<Map<String, String>> sysParam = new ArrayList<>();
            if (coolLoginIdRedis != null) {
                sysParam = (List<Map<String, String>>) coolLoginIdRedis.get("COOL_LOGIN_ID");
            }
            if (sysParam != null && !sysParam.isEmpty()) {
                loginId = sysParam.get(0).get("value");
            }

            if ("1".equals(isaleCheck) && !loginId.equals("")) {
                Long timeStart = System.currentTimeMillis();
                log.info("进入受理规则校验，参数：taskChlList = " + JSON.toJSONString(taskChlList) + ", activityType = " + privateParams.get("activityType") + ", integrationId = " + params.get("integrationId") + ", loginId = " + loginId + ", LATN_ID = " + params.get("lanId"));
                coopruleService.validateProduct(taskChlList, privateParams.get("activityType"), params.get("integrationId"), loginId, params.get("lanId"));
                Long time = System.currentTimeMillis() - timeStart;
                if (taskChlList == null || taskChlList.isEmpty()) {
                    nonPassedMsg.put("rule_" + ruleId, "受理规则校验未通过   ");
                    return nonPassedMsg;
                }
            }
            esHitService.save(jsonObject, IndexList.RULE_MODULE);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("hit", false);
            jsonObject.put("msg", "规则异常");
            esHitService.save(jsonObject, IndexList.RULE_MODULE);
        }

        return ruleMap;
    }

    /**
     * 规则引擎过滤：字符串转数字 （用于大小区间比较）
     * <p>
     * 参数：待比较字段
     */
    class StringToNumOperator extends Operator {
        public StringToNumOperator(String name) {
            this.name = name;
        }

        public Object executeInner(Object[] list) throws Exception {
            String str = (String) list[0];
            if (NumberUtils.isNumber(str)) {
                return NumberUtils.toDouble(str);
            } else {
                return str;
            }
        }
    }


    public String cpcExpression(Map<String, String> labelMap) {
        StringBuilder express = new StringBuilder();
        // 从redis 中获取所有的时间类型标签集合
        Map<String, Object> labelCodeListRedis = eventRedisService.getRedis("LABEL_CODE_LIST");
        List<String> labelCodeList = new ArrayList<>();
        if (labelCodeListRedis != null) {
            labelCodeList = (List<String>) labelCodeListRedis.get("LABEL_CODE_LIST");
        }
        String code = labelMap.get("code");
        String type = labelMap.get("operType");
        String rightParam = labelMap.get("rightParam");

        if ("PROM_LIST".equals(code)) {
            express.append("(checkProm(").append(code).append(",").append(type).append(",").append(rightParam);
            express.append("))");
        } else if (labelCodeList.contains(code)) {
            // todo 时间类型标签
            String updateStaff = String.valueOf(labelMap.get("updateStaff"));
            if ("200".equals(updateStaff)) {
                if (type.equals(BETWEEN.getValue().toString())) {
                    String[] split = rightParam.split(",");
                    String time1 = DateUtil.getPreDay(Integer.parseInt(split[0]));
                    String time2 = DateUtil.getPreDay(Integer.parseInt(split[1]));
                    rightParam = time1 + "," + time2;
                } else {
                    rightParam = DateUtil.getPreDay(Integer.parseInt(rightParam));
                }
            }
            express.append("(dateLabel(").append(code).append(",").append(type).append(",").append("\"" + rightParam + "\"");
            express.append("))");
        } else {
            if ("7100".equals(type)) {
                express.append("!");
            }
            express.append("((");
            express.append(assLabel(code, type, rightParam));
            express.append(")");
        }
        return express.toString();
    }

    public static String assLabel(String code, String type, String rightParam) {
        StringBuilder express = new StringBuilder();

        switch (type) {
            case "1000":
                express.append("toNum(").append(code).append("))");
                express.append(" > ");
                express.append(rightParam);
                break;
            case "2000":
                express.append("toNum(").append(code).append("))");
                express.append(" < ");
                express.append(rightParam);
                break;
            case "3000":
                express.append("toNum(").append(code).append("))");
                express.append(" == ");
                if (NumberUtils.isNumber(rightParam)) {
                    express.append(rightParam);
                } else {
                    express.append("\"").append(rightParam).append("\"");
                }
                break;
            case "4000":
                express.append("toNum(").append(code).append("))");
                express.append(" != ");
                if (NumberUtils.isNumber(rightParam)) {
                    express.append(rightParam);
                } else {
                    express.append("\"").append(rightParam).append("\"");
                }
                break;
            case "5000":
                express.append("toNum(").append(code).append("))");
                express.append(" >= ");
                express.append(rightParam);
                break;
            case "6000":
                express.append("toNum(").append(code).append("))");
                express.append(" <= ");
                express.append(rightParam);
                break;
            case "7100":    //不包含于
            case "7000":    //包含于
                express.append(code).append(")");
                express.append(" in ");
                String[] strArray = rightParam.split(",");
                express.append("(");
                for (int j = 0; j < strArray.length; j++) {
                    express.append("\"").append(strArray[j]).append("\"");
                    if (j != strArray.length - 1) {
                        express.append(",");
                    }
                }
                express.append(")");
                break;
            case "7200":  //区间于
                express.append("toNum(").append(code).append("))");
                String[] strArray2 = rightParam.split(",");
                express.append(" >= ").append(strArray2[0]);
                express.append(" && ").append("(toNum(");
                express.append(code).append("))");
                express.append(" <= ").append(strArray2[1]);
                break;
        }

        return express.toString();
    }

    public static String cpcLabel(String code, String type, String rightParam) {
        StringBuilder express = new StringBuilder();
        express.append("if(");

        if ("7100".equals(type)) {
            express.append("!");
        }
        express.append("(");
        express.append(assLabel(code, type, rightParam));
        express.append(") {return true}");

        return express.toString();
    }


    /**
     * 规则引擎过滤：销售品标签过滤
     * <p>
     * 参数：已办理销售品、类型、过滤规则配置销售品
     */
    class PromCheckOperator extends Operator {
        public PromCheckOperator(String name) {
            this.name = name;
        }

        public Object executeInner(Object[] list) throws Exception {
            boolean productCheck = false;
            //获取用户已办理销售品
            String productStr = (String) list[0];
            //获取过滤类型
            String type = list[1].toString();
            //过滤规则配置的销售品
            String[] checkProductArr = new String[list.length - 2];
            for (int i = 2; i < list.length; i++) {
                checkProductArr[i - 2] = list[i].toString();
            }
            //获取需要过滤的销售品
            if (checkProductArr != null && checkProductArr.length > 0) {
                //    String[] checkProductArr = checkProduct.split(",");
                if (productStr != null && !"".equals(productStr)) {
                    if ("7000".equals(type)) {  //存在于
                        productCheck = false;
                        for (String product : checkProductArr) {
                            int index = productStr.indexOf(product);
                            if (index >= 0) {
                                productCheck = true;
                                break;
                            }
                        }
                    } else if ("7100".equals(type)) { //不存在于
                        productCheck = true;
                        for (String product : checkProductArr) {
                            int index = productStr.indexOf(product);
                            if (index >= 0) {
                                productCheck = false;
                                break;
                            }
                        }
                    }
                } else {

                    //存在于校验
                    if ("7100".equals(type)) {
                        productCheck = true;
                    } else if ("7000".equals(type)) {
                        productCheck = false;
                    }
                }
            }

            return productCheck;

        }
    }

    // 时间类型标签比较
    class ComperDateLabel extends Operator {

        public ComperDateLabel(String name) {
            this.name = name;
        }

        @Override
        public Object executeInner(Object[] list) throws Exception {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            boolean result = false;

            String date = list[0].toString();
            String operType = list[1].toString();
            String rightParam = list[2].toString();
            // 左参数转成时间
            Date dateLeft = dateFormat.parse(date);
            // 右参数转成时间
            Integer countDay = null;
            if (rightParam.contains(",")) {
                String[] rightParamArry = rightParam.split(",");
                String rightParam1 = rightParamArry[0];
                String rightParam2 = rightParamArry[1];
                Date dateRight1 = dateFormat.parse(rightParam1);
                countDay = dateLeft.compareTo(dateRight1);
            } else {
                Date dateRight = dateFormat.parse(rightParam);
                countDay = dateLeft.compareTo(dateRight);
            }
            // 左参跟右参对比
            if ("1000".equals(operType) && countDay > 0) {            //  > 大于
                result = true;
            } else if ("2000".equals(operType) && countDay < 0) {     // < 小于
                result = true;
            } else if ("3000".equals(operType) && countDay == 0) {     // = 等于
                result = true;
            } else if ("4000".equals(operType) && countDay != 0) {     // != 不等
                result = true;
            } else if ("5000".equals(operType) && (countDay == 0 || countDay > 0)) {     // >= 大于等于
                result = true;
            } else if ("6000".equals(operType) && (countDay == 0 || countDay < 0)) {     // <= 小于等于
                result = true;
            } else if ("7200".equals(operType) && (countDay == 0 || countDay > 0)) {  // 区间与 ,右参有2个参数
                String[] rightParamArry = rightParam.split(",");  // 区间与中的两个参数
                String rightParam1 = rightParamArry[0];
                String rightParam2 = rightParamArry[1];
                Date dateRight1 = dateFormat.parse(rightParam1);
                Date dateRight2 = dateFormat.parse(rightParam2);
                int count1 = dateLeft.compareTo(dateRight1);  // 数据与区间的前一个数据比较
                int count2 = dateLeft.compareTo(dateRight2);  // 数据与区间的后一个数据比较

                if ((count1 == 0 || count1 > 0) && (count2 == 0 || count2 < 0)) {
                    result = true;
                }
            }
            return result;
        }
    }


    private Map<String, Object> ChannelTask(Long evtContactConfId, List<Map<String, String>> productList, DefaultContext<String, Object> context, String reqId, Map<String, Object> nonPassedMsg, Long ruleId) {
        Date now = new Date();
        long begin = System.currentTimeMillis();
        //初始化返回结果推荐信息
        Map<String, Object> channelMap = new ConcurrentHashMap<>();
        List<Map<String, Object>> taskChlAttrList = new ArrayList<>();
        Map<String, Object> taskChlAttr;

        //查询渠道属性，渠道生失效时间过滤
        Map<String, Object> chlConfDetailRedis = eventRedisService.getRedis("CHL_CONF_DETAIL_", evtContactConfId);
        MktCamChlConfDetail mktCamChlConfDetail = new MktCamChlConfDetail();
        if (chlConfDetailRedis != null) {
            mktCamChlConfDetail = (MktCamChlConfDetail) chlConfDetailRedis.get("CHL_CONF_DETAIL_" + evtContactConfId);
        }
        boolean checkTime = true;
        for (MktCamChlConfAttr mktCamChlConfAttr : mktCamChlConfDetail.getMktCamChlConfAttrList()) {
            //渠道属性数据返回给协同中心
            if (mktCamChlConfAttr.getAttrId() == 500600010001L || mktCamChlConfAttr.getAttrId() == 500600010002L || mktCamChlConfAttr.getAttrId() == 500600010003L || mktCamChlConfAttr.getAttrId() == 500600010004L) {
                taskChlAttr = new ConcurrentHashMap<>();
                taskChlAttr.put("attrId", mktCamChlConfAttr.getAttrId().toString());
                taskChlAttr.put("attrKey", mktCamChlConfAttr.getAttrId().toString());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                taskChlAttr.put("attrValue", simpleDateFormat.format(Long.valueOf(mktCamChlConfAttr.getAttrValue())));
                taskChlAttrList.add(taskChlAttr);
            } else if (mktCamChlConfAttr.getAttrId() == 500600010005L || mktCamChlConfAttr.getAttrId() == 500600010011L) {
                taskChlAttr = new ConcurrentHashMap<>();
                taskChlAttr.put("attrId", mktCamChlConfAttr.getAttrId().toString());
                taskChlAttr.put("attrKey", mktCamChlConfAttr.getAttrId().toString());
                taskChlAttr.put("attrValue", mktCamChlConfAttr.getAttrValue());
                taskChlAttrList.add(taskChlAttr);
            } else if (mktCamChlConfAttr.getAttrId() == 500600010006L) { //判断渠道生失效时间
                if (!now.after(new Date(Long.parseLong(mktCamChlConfAttr.getAttrValue())))) {
                    checkTime = false;
                } else {
                    taskChlAttr = new ConcurrentHashMap<>();
                    taskChlAttr.put("attrId", mktCamChlConfAttr.getAttrId().toString());
                    taskChlAttr.put("attrKey", mktCamChlConfAttr.getAttrId().toString());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    taskChlAttr.put("attrValue", simpleDateFormat.format(Long.valueOf(mktCamChlConfAttr.getAttrValue())));
                    taskChlAttrList.add(taskChlAttr);
                }
            } else if (mktCamChlConfAttr.getAttrId() == 500600010007L) {
                if (now.after(new Date(Long.parseLong(mktCamChlConfAttr.getAttrValue())))) {
                    checkTime = false;
                } else {
                    taskChlAttr = new ConcurrentHashMap<>();
                    taskChlAttr.put("attrId", mktCamChlConfAttr.getAttrId().toString());
                    taskChlAttr.put("attrKey", mktCamChlConfAttr.getAttrId().toString());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    taskChlAttr.put("attrValue", simpleDateFormat.format(Long.valueOf(mktCamChlConfAttr.getAttrValue())));
                    taskChlAttrList.add(taskChlAttr);
                }
            } else if (mktCamChlConfAttr.getAttrId() == 500600010008L) {  //获取调查问卷ID
                //调查问卷
                channelMap.put("naireId", mktCamChlConfAttr.getAttrValue());
            } else if (mktCamChlConfAttr.getAttrId() == 500600010012L) {   //获取接触账号/推送账号(如果有)
                if (mktCamChlConfAttr.getAttrValue() != null && !"".equals(mktCamChlConfAttr.getAttrValue())) {
                    if (context.containsKey(mktCamChlConfAttr.getAttrValue())) {
                        channelMap.put("contactAccount", context.get(mktCamChlConfAttr.getAttrValue()));
                    } else {
                        //未查询到推送账号 就不命中
                        nonPassedMsg.put("rule_" + ruleId, "未查询到渠道推送账号");
                        return nonPassedMsg;
                    }
                }
            } else {
                taskChlAttr = new ConcurrentHashMap<>();
                taskChlAttr.put("attrId", mktCamChlConfAttr.getAttrId().toString());
                taskChlAttr.put("attrKey", mktCamChlConfAttr.getAttrId().toString());
                taskChlAttr.put("attrValue", mktCamChlConfAttr.getAttrValue().toString());
                taskChlAttrList.add(taskChlAttr);
            }

        }
        channelMap.put("taskChlAttrList", taskChlAttrList);

        if (!checkTime) {
            nonPassedMsg.put("rule_" + ruleId, "渠道生失效时间错误");
            return nonPassedMsg;
        }
        channelMap.put("channelId", mktCamChlConfDetail.getContactChlCode());
        //查询渠道id
        channelMap.put("channelConfId", mktCamChlConfDetail.getContactChlId().toString()); //渠道id
        channelMap.put("pushType", mktCamChlConfDetail.getPushType()); //推送类型
        channelMap.put("pushTime", ""); // 推送时间
        //返回结果中添加销售品信息
        channelMap.put("productList", JSONArray.toJSON(productList));

        //查询话术
        List<String> scriptLabelList = new ArrayList<>();
        String contactScript = null;
        String mktVerbalStr = null;
        // 从redis中获取脚本
        Map<String, Object> mktCamScriptRedis = eventRedisService.getRedis("MKT_CAM_SCRIPT_", evtContactConfId);
        CamScript camScript = new CamScript();
        if (mktCamScriptRedis != null) {
            camScript = (CamScript) mktCamScriptRedis.get("MKT_CAM_SCRIPT_" + evtContactConfId);
            log.info("camScript = " + JSON.toJSONString(camScript));
        }
        if (camScript != null) {
            contactScript = camScript.getScriptDesc();
            if (contactScript != null) {
                scriptLabelList.addAll(subScript(camScript.getScriptDesc()));
            }

        } else {
            //未查询到话术 不命中
            nonPassedMsg.put("rule_" + ruleId, "未查询到推送话术");
            return nonPassedMsg;
        }

        // 从redis中获取指引
        Map<String, Object> mktVerbalRedis = eventRedisService.getRedis("MKT_VERBAL_", evtContactConfId);
        List<VerbalVO> verbalVOList = new ArrayList<>();
        if (mktVerbalRedis != null) {
            verbalVOList = (List<VerbalVO>) mktVerbalRedis.get("MKT_VERBAL_" + evtContactConfId);
        }
        if (verbalVOList.size() > 0) {
            for (VerbalVO verbalVO : verbalVOList) {
                //查询指引规则 todo
                mktVerbalStr = verbalVOList.get(0).getScriptDesc();
                if (mktVerbalStr != null) {
                    scriptLabelList.addAll(subScript(mktVerbalStr));
                }
            }
        }
        if (scriptLabelList.size() > 0) {
            for (String labelStr : scriptLabelList) {
                if (context.containsKey(labelStr)) {
                    if (contactScript != null) {
                        contactScript = contactScript.replace("${" + labelStr + "}$", (String) context.get(labelStr));
                    }
                    if (mktVerbalStr != null) {
                        mktVerbalStr = mktVerbalStr.replace("${" + labelStr + "}$", (String) context.get(labelStr));
                    }
                }
            }
        }
        // 渠道话术拦截开关
        Map<String, Object> channelFilterCodeRedis = eventRedisService.getRedis("CHANNEL_FILTER_CODE");
        String channelFilterCode = null;
        if (channelFilterCodeRedis != null) {
            channelFilterCode = (String) channelFilterCodeRedis.get("CHANNEL_FILTER_CODE");
        }
        int index = -1;
        if (channelFilterCode != null) {
            index = channelFilterCode.indexOf(mktCamChlConfDetail.getContactChlCode());
        }

        if (index >= 0) {
            //判断脚本中有无未查询到的标签
            if (contactScript != null) {
                if (subScript(contactScript).size() > 0) {
                    nonPassedMsg.put("rule_" + ruleId, "推荐话术标签替换含有无值的标签");
                    return nonPassedMsg;
                }
            }

            //痛痒点
            if (mktVerbalStr != null) {
                if (subScript(mktVerbalStr).size() > 0) {
                    nonPassedMsg.put("rule_" + ruleId, "推荐指引标签替换含有无值的标签");
                    return nonPassedMsg;
                }
            }
        }            //返回结果中添加脚本信息
        channelMap.put("contactScript", contactScript == null ? "" : contactScript);
        channelMap.put("reason", mktVerbalStr == null ? "" : mktVerbalStr);
        //展示列标签
        return channelMap;
    }


    private List<String> subScript(String str) {
        List<String> result = new ArrayList<>();
        Pattern p = Pattern.compile("(?<=\\$\\{)([^$]+)(?=\\}\\$)");
        Matcher m = p.matcher(str);
        while (m.find()) {
            result.add(m.group(1));
        }
        return result;
    }
}
