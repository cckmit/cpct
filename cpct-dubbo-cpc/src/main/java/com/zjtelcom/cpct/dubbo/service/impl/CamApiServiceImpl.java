package com.zjtelcom.cpct.dubbo.service.impl;

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
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.event.ContactEvtMatchRulMapper;
import com.zjtelcom.cpct.dao.event.EventMatchRulConditionMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyFilterRuleRelMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.channel.VerbalVO;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dubbo.service.CamApiService;
import com.zjtelcom.cpct.elastic.config.IndexList;
import com.zjtelcom.cpct.elastic.service.EsHitService;
import com.zjtelcom.cpct.enums.AreaNameEnum;
import com.zjtelcom.cpct.service.dubbo.CamCpcService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct_prod.dao.offer.OfferProdMapper;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CamApiServiceImpl implements CamApiService {

    @Value("${thread.maxPoolSize}")
    private int maxPoolSize;
    @Value("${table.infallible}")
    private String defaultInfallibleTable;

    private static final Logger log = LoggerFactory.getLogger(CamApiServiceImpl.class);

    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper; //事件与活动关联表

    @Autowired
    private MktCampaignMapper mktCampaignMapper; //活动基本信息

    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper; //分群规则条件表

    @Autowired
    private MktStrategyConfMapper mktStrategyConfMapper; //策略基本信息

    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;//策略规则

    @Autowired
    private FilterRuleMapper filterRuleMapper; //过滤规则

    @Autowired
    private MktStrategyFilterRuleRelMapper mktStrategyFilterRuleRelMapper;//过滤规则与策略关系

    @Autowired
    private MktCamItemMapper mktCamItemMapper; //销售品

    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper; //协同渠道配置基本信息

    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper; //协同渠道配置的渠道

    @Autowired
    private MktVerbalConditionMapper mktVerbalConditionMapper; //规则存储公共表（此处查询协同渠道子策略规则和话术规则）

    @Autowired
    private MktCamScriptMapper mktCamScriptMapper; //营销脚本

    @Autowired
    private MktVerbalMapper mktVerbalMapper; //话术

    @Autowired
    private InjectionLabelMapper injectionLabelMapper; //标签因子

    @Autowired
    private EsHitService esHitService;  //es存储

    @Autowired
    private RedisUtils redisUtils;  // redis方法

    @Autowired(required = false)
    private YzServ yzServ; //因子实时查询dubbo服务

    @Autowired(required = false)
    private ContactChannelMapper contactChannelMapper; //渠道信息

    @Autowired
    private ContactEvtMatchRulMapper contactEvtMatchRulMapper; //事件规则

    @Autowired
    private EventMatchRulConditionMapper eventMatchRulConditionMapper;  //事件规则条件

    @Autowired
    private MktCamDisplayColumnRelMapper mktCamDisplayColumnRelMapper;

//    @Autowired
//    private OfferProdMapper offerProdMapper;

    @Autowired(required = false)
    private SysParamsMapper sysParamsMapper;  //查询系统参数

    @Autowired(required = false)
    private ICacheOfferEntityQryService iCacheOfferEntityQryService; // 查询销售品实例缓存实体

    @Autowired(required = false)
    private ICacheOfferRelIndexQryService iCacheOfferRelIndexQryService; // 根据offerInstId和statusCd(1000-有效)查询offerProdInstRelId

    @Autowired(required = false)
    private ICacheRelEntityQryService iCacheRelEntityQryService;

    @Autowired(required = false)
    private ICacheProdIndexQryService iCacheProdIndexQryService;

    Map<String,Boolean> flagMap = new ConcurrentHashMap();

    @Autowired(required = false)
    private CamCpcService camCpcService;

    /**
     * 活动级别验证
     */
    @Override
    public Map<String, Object> ActivityTask(Map<String, String> params, Long activityId, Map<String, String> privateParams, Map<String, String> laubelItems, List<Map<String, Object>> evtTriggers, List<Map<String, Object>> strategyMapList, DefaultContext<String, Object> context) {
        Map<String, Object> activity = camCpcService.ActivityCpcTask(params, activityId, privateParams, laubelItems, evtTriggers, strategyMapList, context);
        return activity;
    }


    /**
     * 获取规则列表（规则级）
     */
    class RuleTask implements Callable<Map<String, Object>> {
        private Long strategyConfId; //策略配置id
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

        public RuleTask(Map<String, String> params, Map<String, String> privateParams, Long strategyConfId, String strategyConfName, Long tarGrpId, String productStr, String evtContactConfIdStr, Long mktStrategyConfRuleId, String mktStrategyConfRuleName, DefaultContext<String, Object> context, String lanId) {
            this.strategyConfId = strategyConfId;
            this.strategyConfName = strategyConfName;
            this.tarGrpId = tarGrpId;
            this.reqId = params.get("reqId");
            this.productStr = productStr;
            this.evtContactConfIdStr = evtContactConfIdStr;
            this.ruleId = mktStrategyConfRuleId;
            this.ruleName = mktStrategyConfRuleName;
            this.params = params;
            this.privateParams = privateParams;
            this.context = context;
            this.lanId = lanId;
        }

        @Override
        public Map<String, Object> call() throws Exception {

            long begin = System.currentTimeMillis();

            //初始化es log   标签使用
            JSONObject esJson = new JSONObject();
            //初始化es log   规则使用
            JSONObject jsonObject = new JSONObject();

            // 获取 PROM_INTEG_ID标签
            String promIntegId = "";
            if (context.get("PROM_INTEG_ID") != null) {
                promIntegId = (String) context.get("PROM_INTEG_ID");
            }

            MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(Long.valueOf(privateParams.get("activityId")));
            MktStrategyConfDO mktStrategyConfDO = mktStrategyConfMapper.selectByPrimaryKey(strategyConfId);
            MktStrategyConfRuleDO mktStrategyConfRuleDO = mktStrategyConfRuleMapper.selectByPrimaryKey(ruleId);

            jsonObject.put("ruleId", mktStrategyConfRuleDO.getInitId());
            jsonObject.put("ruleName", ruleName);
            jsonObject.put("hitEntity", privateParams.get("accNbr")); //命中对象
            jsonObject.put("reqId", reqId);
            jsonObject.put("eventId", params.get("eventCode"));
            jsonObject.put("activityId", mktCampaignDO.getInitId());
            jsonObject.put("strategyConfId", mktStrategyConfDO.getInitId());
            jsonObject.put("productStr", productStr);
            jsonObject.put("evtContactConfIdStr", evtContactConfIdStr);
            jsonObject.put("tarGrpId", tarGrpId);
            jsonObject.put("promIntegId", promIntegId);

            //ES log 标签实例
            esJson.put("reqId", reqId);
            esJson.put("eventId", params.get("eventCode"));
            esJson.put("activityId", mktCampaignDO.getInitId());
            esJson.put("ruleId", mktStrategyConfRuleDO.getInitId());
            esJson.put("ruleName", ruleName);
            esJson.put("integrationId", params.get("integrationId"));
            esJson.put("accNbr", params.get("accNbr"));
            esJson.put("strategyConfId", mktStrategyConfDO.getInitId());
            esJson.put("tarGrpId", tarGrpId);
            esJson.put("promIntegId", promIntegId);
            esJson.put("hitEntity", privateParams.get("accNbr")); //命中对象

            Map<String, Object> ruleMap = new ConcurrentHashMap<>();
            //初始化返回结果中的推荐信息列表
            List<Map<String, Object>> taskChlList = new ArrayList<>();

            //  2.判断客户分群规则---------------------------
            //判断匹配结果，如匹配则向下进行，如不匹配则continue结束本次循环
            //拼装redis key
            ExpressRunner runner = new ExpressRunner();
            runner.addFunction("toNum", new StringToNumOperator("toNum"));

            //如果分群id为空
            if (tarGrpId == null) {
                jsonObject.put("hit", "false");
                jsonObject.put("msg", "分群ID异常");
                esHitService.save(jsonObject, IndexList.RULE_MODULE);
                return Collections.EMPTY_MAP;
            }

            //记录实例不足的标签
            StringBuilder notEnoughLabel = new StringBuilder();

            //判断表达式在缓存中有没有
            String express = (String) redisUtils.get("EXPRESS_" + tarGrpId);

            SysParams sysParams = (SysParams) redisUtils.get("EVT_SWITCH_CHECK_LABEL");
            if (sysParams == null) {
                List<SysParams> systemParamList = sysParamsMapper.findParamKeyIn("CHECK_LABEL");
                if (systemParamList.size() > 0) {
                    redisUtils.set("EVT_SWITCH_CHECK_LABEL", systemParamList.get(0));
                }
            }


            String realProdFilter = (String) redisUtils.get("REAL_PROD_FILTER");
            if (realProdFilter == null) {
                List<SysParams> sysParamsList = sysParamsMapper.listParamsByKeyForCampaign("REAL_PROD_FILTER");
                if (sysParamsList != null && sysParamsList.size() > 0) {
                    realProdFilter = sysParamsList.get(0).getParamValue();
                    redisUtils.set("REAL_PROD_FILTER", realProdFilter);
                }
            }
            if (realProdFilter != null && "1".equals(realProdFilter)) {
                List<String> prodList = new ArrayList<>();
                log.info("111------accNbr --->" + privateParams.get("accNbr"));
                CacheResultObject<Set<String>> prodInstIdsObject = iCacheProdIndexQryService.qryProdInstIndex2(privateParams.get("accNbr"));
                //   log.info("222------prodInstIdsObject --->" + JSON.toJSONString(prodInstIdsObject));
                if(prodInstIdsObject!=null &&  prodInstIdsObject.getResultObject() !=null ){
                    Set<String> prodInstIds = prodInstIdsObject.getResultObject();
                    for (String prodInstId : prodInstIds) {
                        // 根据prodInstId 和 statusCd(1000-有效)查询offerProdInstRelId
                        //           log.info("333------prodInstId --->" + prodInstId);
                        CacheResultObject<Set<String>> setCacheResultObject = iCacheOfferRelIndexQryService.qryOfferProdInstRelIndex2(prodInstId, "1000");
                        //            log.info("444------setCacheResultObject --->" + JSON.toJSONString(setCacheResultObject));
                        if (setCacheResultObject != null && setCacheResultObject.getResultObject() != null) {
                            Set<String> offerProdInstRelIdSet = setCacheResultObject.getResultObject();
                            for (String offerProdInstRelId : offerProdInstRelIdSet) {
                                // 查询销售品产品实例关系缓存实体
                                CacheResultObject<OfferProdInstRel> offerProdInstRelCacheEntity = iCacheRelEntityQryService.getOfferProdInstRelCacheEntity(offerProdInstRelId);
                                //                   log.info("555------offerProdInstRelCacheEntity --->" + JSON.toJSONString(offerProdInstRelCacheEntity));
                                if (offerProdInstRelCacheEntity != null && offerProdInstRelCacheEntity.getResultObject() != null) {
                                    OfferProdInstRel offerProdInstRel = offerProdInstRelCacheEntity.getResultObject();

                                    // 查询销售品实例缓存实体
                                    CacheResultObject<OfferInst> offerInstCacheEntity = iCacheOfferEntityQryService.getOfferInstCacheEntity(offerProdInstRel.getOfferInstId().toString());
                                    //                        log.info("666------offerInstCacheEntity --->" + JSON.toJSONString(offerInstCacheEntity));
                                    if(offerInstCacheEntity!=null && offerInstCacheEntity.getResultObject()!=null){
                                        OfferInst offerInst = offerInstCacheEntity.getResultObject();
                                        //Offer offer = offerProdMapper.selectByPrimaryKey(Integer.valueOf(offerInst.getOfferId().toString()));
                                        //                            log.info("777------offer --->" + JSON.toJSONString(offer));
                                        prodList.add(offerInst.getOfferId().toString());
                                    }
                                }
                            }
                        }
                    }
                }
                String productString = ChannelUtil.StringList2String(prodList);
                log.info("999------productStr --->" + JSON.toJSONString(productStr));
                context.put("PROM_LIST" , productString);
            }

            if (express == null || "".equals(express)) {
                List<LabelResult> labelResultList = new ArrayList<>();
                try {
                    LabelResult lr;
                    //查询规则下所有标签
                    List<Map<String, String>> labelMapList = (List<Map<String, String>>) redisUtils.get("RULE_ALL_LABEL_" + tarGrpId);
                    if (labelMapList == null) {
                        try {
                            labelMapList = tarGrpConditionMapper.selectAllLabelByTarId(tarGrpId);
                        } catch (Exception e) {
                            jsonObject.put("hit", "false");
                            jsonObject.put("msg", "规则下标签查询失败");
                            esHitService.save(jsonObject, IndexList.RULE_MODULE);
                            return Collections.EMPTY_MAP;
                        }
                        redisUtils.set("RULE_ALL_LABEL_" + tarGrpId, labelMapList);
                    }

                    if (labelMapList == null || labelMapList.size() <= 0) {
                        log.info("未查询到分群标签:" + privateParams.get("activityId") + "---" + ruleId);
                        jsonObject.put("hit", "false");
                        jsonObject.put("msg", "未查询到分群标签");
                        esHitService.save(jsonObject, IndexList.RULE_MODULE);
                        return Collections.EMPTY_MAP;
                    }

                    //将规则拼装为表达式
                    StringBuilder expressSb = new StringBuilder();
                    expressSb.append("if(");
                    //遍历所有规则
                    for (Map<String, String> labelMap : labelMapList) {
                        if(defaultInfallibleTable.equals(labelMap.get("code"))){
                            redisUtils.set("LEFT_PARAM_FLAG" + strategyConfId, ruleId);
                            flagMap.put(ruleId.toString(), true);
                            //log.info(Thread.currentThread().getName() + "flag = true进入...");
                            expressSb.append("true&&");
                            continue;
                        }
                        String type = labelMap.get("operType");
                        //保存标签的es log
                        lr = new LabelResult();
                        if ("PROM_LIST".equals(labelMap.get("code")) && "1".equals(realProdFilter)) {
                            FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(Long.valueOf(labelMap.get("rightParam")));
                            if (filterRule != null) {
                                lr.setRightOperand(filterRule.getChooseProduct());
                                labelMap.put("rightParam", filterRule.getChooseProduct());
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
                        expressSb.append(cpcExpression(labelMap.get("code"), type, labelMap.get("rightParam")));

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
                    return Collections.EMPTY_MAP;
                }
                //表达式存入redis
                redisUtils.set("EXPRESS_" + tarGrpId, express);
                esJson.put("labelResultList", JSONArray.toJSON(labelResultList));
            } else {
                List<LabelResult> labelResultList = new ArrayList<>();
                try {
                    LabelResult lr;
                    //查询规则下所有标签
                    List<Map<String, String>> labelMapList = (List<Map<String, String>>) redisUtils.get("RULE_ALL_LABEL_" + tarGrpId);
                    if (labelMapList == null) {
                        try {
                            labelMapList = tarGrpConditionMapper.selectAllLabelByTarId(tarGrpId);
                        } catch (Exception e) {
                            jsonObject.put("hit", "false");
                            jsonObject.put("msg", "规则下标签查询失败");
                            esHitService.save(jsonObject, IndexList.RULE_MODULE);
                            return Collections.EMPTY_MAP;
                        }
                        redisUtils.set("RULE_ALL_LABEL_" + tarGrpId, labelMapList);
                    }

                    //遍历所有规则
                    for (Map<String, String> labelMap : labelMapList) {
                        if(defaultInfallibleTable.equals(labelMap.get("code"))){
                            redisUtils.set("LEFT_PARAM_FLAG" + strategyConfId, ruleId);
                            flagMap.put(ruleId.toString(), true);
                            log.info("flag = true进入...");
                            continue;
                        }
                        //判断标签实例是否足够
                        if (!context.containsKey(labelMap.get("code"))) {
                            notEnoughLabel.append(labelMap.get("code")).append(",");
                        }

                    }

                    // 异步执行当个标签比较结果
                    new labelResultThread(esJson, labelMapList, context, sysParams, runner, labelResultList).run();

                } catch (Exception e) {
                    e.printStackTrace();
                    jsonObject.put("hit", "false");
                    jsonObject.put("msg", "表达式拼接异常");
                    esHitService.save(jsonObject, IndexList.RULE_MODULE);
                    return Collections.EMPTY_MAP;
                }
            }

            esHitService.save(esJson, IndexList.Label_MODULE);  //储存标签比较结果
            try {
                RuleResult ruleResult = new RuleResult();
                //初始化返回结果中的销售品条目
                List<Map<String, String>> productList = new ArrayList<>();
                if(flagMap.get(ruleId.toString()) == false) {
                    //验证是否标签实例不足
                    if (notEnoughLabel.length() > 0) {
                        log.info("notEnoughLabel.length() > 0->标签实例不足");
                        jsonObject.put("hit", "false");
                        jsonObject.put("msg", "标签实例不足：" + notEnoughLabel.toString());
                        esHitService.save(jsonObject, IndexList.RULE_MODULE);
                        return Collections.EMPTY_MAP;
                    }

                    //规则引擎计算
                    ExpressRunner runnerQ = new ExpressRunner();
                    runnerQ.addFunction("toNum", new StringToNumOperator("toNum"));
                    runnerQ.addFunction("checkProm", new PromCheckOperator("checkProm"));
                    runnerQ.addFunction("dateLabel", new ComperDateLabel("dateLabel"));

                    try {
                        ruleResult = runnerQ.executeRule(express, context, true, true);
                    } catch (Exception e) {
                        ruleMap.put("msg", "规则引擎计算失败");
                        jsonObject.put("hit", "false");
                        jsonObject.put("msg", "规则引擎计算失败");
                        esHitService.save(jsonObject, IndexList.RULE_MODULE);
                        return Collections.EMPTY_MAP;
                    }

                    jsonObject.put("express", express);

                    if (ruleResult.getResult() != null && ((Boolean) ruleResult.getResult())) {
                        jsonObject.put("hit", true);

                        //拼接返回结果
                        ruleMap.put("orderISI", params.get("reqId")); //流水号
                        ruleMap.put("activityId", privateParams.get("activityId")); //活动编码
                        ruleMap.put("activityName", privateParams.get("activityName")); //活动名称
                        ruleMap.put("activityType", privateParams.get("activityType")); //活动类型
                        ruleMap.put("activityStartTime", privateParams.get("activityStartTime")); //活动开始时间
                        ruleMap.put("activityEndTime", privateParams.get("activityEndTime")); //活动结束时间
                        ruleMap.put("skipCheck", "0"); //todo 调过预校验
                        ruleMap.put("orderPriority", privateParams.get("orderPriority")); //活动优先级
                        ruleMap.put("integrationId", privateParams.get("integrationId")); //集成编号（必填）
                        ruleMap.put("accNbr", privateParams.get("accNbr")); //业务号码（必填）
                        ruleMap.put("policyId", strategyConfId.toString()); //策略编码
                        ruleMap.put("policyName", strategyConfName); //策略名称
                        ruleMap.put("ruleId", ruleId.toString()); //规则编码
                        ruleMap.put("ruleName", ruleName); //规则名称
                        ruleMap.put("promIntegId", promIntegId); // 销售品实例ID

                        //查询销售品列表
                        if (productStr != null && !"".equals(productStr)) {
                            // 推荐条目集合存入redis -- linchao
                            List<MktCamItem> mktCamItemList = (List<MktCamItem>) redisUtils.get("MKT_CAM_ITEM_LIST_" + ruleId.toString());
                            if (mktCamItemList == null) {
                                mktCamItemList = new ArrayList<>();
                                String[] productArray = productStr.split("/");
                                for (String str : productArray) {
                                    // 从redis中获取推荐条目
                                    MktCamItem mktCamItem = (MktCamItem) redisUtils.get("MKT_CAM_ITEM_" + str);
                                    if (mktCamItem == null) {
                                        mktCamItem = mktCamItemMapper.selectByPrimaryKey(Long.valueOf(str));
                                        if (mktCamItem == null) {
                                            continue;
                                        }
                                        redisUtils.set("MKT_CAM_ITEM_" + mktCamItem.getMktCamItemId(), mktCamItem);
                                    }
                                    mktCamItemList.add(mktCamItem);
                                }
                                redisUtils.set("MKT_CAM_ITEM_LIST_" + ruleId.toString(), mktCamItemList);
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
                        Channel channelMessage = (Channel) redisUtils.get("MKT_ISALE_LABEL_" + params.get("channelCode"));
                        if (channelMessage == null) {
                            channelMessage = contactChannelMapper.selectByCode(params.get("channelCode"));
                            redisUtils.set("MKT_ISALE_LABEL_" + params.get("channelCode"), channelMessage);
                        }
                        String channelCode = null;

                        //新增加入reids -- linchao
                        List<MktCamChlConfDO> mktCamChlConfDOS = (List<MktCamChlConfDO>) redisUtils.get("MKT_CAMCHL_CONF_LIST_" + ruleId.toString());
                        if (mktCamChlConfDOS == null) {
                            mktCamChlConfDOS = new ArrayList<>();
                            if (evtContactConfIdArray != null && !"".equals(evtContactConfIdArray[0])) {
                                for (String str : evtContactConfIdArray) {
                                    MktCamChlConfDO mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(Long.valueOf(str));
                                    mktCamChlConfDOS.add(mktCamChlConfDO);
                                    redisUtils.set("MKT_CAMCHL_CONF_LIST_" + ruleId.toString(), mktCamChlConfDOS);
                                }
                            }
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
                                Map<String, Object> channelMap = ChannelTask(evtContactConfId, productList, context, reqId);
                                taskChlList.add(channelMap);
                            } else {
                                if (evtContactConfIdArray != null && !"".equals(evtContactConfIdArray[0])) {
                                    for (String str : evtContactConfIdArray) {
                                        //协同渠道规则表id（自建表）
                                        Long evtContactConfId = Long.parseLong(str);
                                        //提交线程
                                        //Future<Map<String, Object>> f = executorService.submit(new ChannelTask(evtContactConfId, productList, context, reqId));
                                        //将线程处理结果添加到结果集
                                        //threadList.add(f);
                                        Map<String, Object> channelMap = ChannelTask(evtContactConfId, productList, context, reqId);
                                        if (channelMap != null && !channelMap.isEmpty()) {
                                            taskChlList.add(channelMap);
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
                        return Collections.EMPTY_MAP;
                    }
                } else {
                    jsonObject.put("hit", true);

                    //拼接返回结果
                    ruleMap.put("orderISI", params.get("reqId")); //流水号
                    ruleMap.put("activityId", privateParams.get("activityId")); //活动编码
                    ruleMap.put("activityName", privateParams.get("activityName")); //活动名称
                    ruleMap.put("activityType", privateParams.get("activityType")); //活动类型
                    ruleMap.put("activityStartTime", privateParams.get("activityStartTime")); //活动开始时间
                    ruleMap.put("activityEndTime", privateParams.get("activityEndTime")); //活动结束时间
                    ruleMap.put("skipCheck", "0"); //todo 调过预校验
                    ruleMap.put("orderPriority", privateParams.get("orderPriority")); //活动优先级
                    ruleMap.put("integrationId", privateParams.get("integrationId")); //集成编号（必填）
                    ruleMap.put("accNbr", privateParams.get("accNbr")); //业务号码（必填）
                    ruleMap.put("policyId", strategyConfId.toString()); //策略编码
                    ruleMap.put("policyName", strategyConfName); //策略名称
                    ruleMap.put("ruleId", ruleId.toString()); //规则编码
                    ruleMap.put("ruleName", ruleName); //规则名称
                    ruleMap.put("promIntegId", promIntegId); // 销售品实例ID

                    //查询销售品列表
                    if (productStr != null && !"".equals(productStr)) {
                        // 推荐条目集合存入redis -- linchao
                        List<MktCamItem> mktCamItemList = (List<MktCamItem>) redisUtils.get("MKT_CAM_ITEM_LIST_" + ruleId.toString());
                        if (mktCamItemList == null) {
                            mktCamItemList = new ArrayList<>();
                            String[] productArray = productStr.split("/");
                            for (String str : productArray) {
                                // 从redis中获取推荐条目
                                MktCamItem mktCamItem = (MktCamItem) redisUtils.get("MKT_CAM_ITEM_" + str);
                                if (mktCamItem == null) {
                                    mktCamItem = mktCamItemMapper.selectByPrimaryKey(Long.valueOf(str));
                                    if (mktCamItem == null) {
                                        continue;
                                    }
                                    redisUtils.set("MKT_CAM_ITEM_" + mktCamItem.getMktCamItemId(), mktCamItem);
                                }
                                mktCamItemList.add(mktCamItem);
                            }
                            redisUtils.set("MKT_CAM_ITEM_LIST_" + ruleId.toString(), mktCamItemList);
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
                    Channel channelMessage = (Channel) redisUtils.get("MKT_ISALE_LABEL_" + params.get("channelCode"));
                    if (channelMessage == null) {
                        channelMessage = contactChannelMapper.selectByCode(params.get("channelCode"));
                        redisUtils.set("MKT_ISALE_LABEL_" + params.get("channelCode"), channelMessage);
                    }
                    String channelCode = null;

                    //新增加入reids -- linchao
                    List<MktCamChlConfDO> mktCamChlConfDOS = (List<MktCamChlConfDO>) redisUtils.get("MKT_CAMCHL_CONF_LIST_" + ruleId.toString());
                    if (mktCamChlConfDOS == null) {
                        mktCamChlConfDOS = new ArrayList<>();
                        if (evtContactConfIdArray != null && !"".equals(evtContactConfIdArray[0])) {
                            for (String str : evtContactConfIdArray) {
                                MktCamChlConfDO mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(Long.valueOf(str));
                                mktCamChlConfDOS.add(mktCamChlConfDO);
                                redisUtils.set("MKT_CAMCHL_CONF_LIST_" + ruleId.toString(), mktCamChlConfDOS);
                            }
                        }
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
                            Map<String, Object> channelMap = ChannelTask(evtContactConfId, productList, context, reqId);
                            taskChlList.add(channelMap);
                        } else {
                            if (evtContactConfIdArray != null && !"".equals(evtContactConfIdArray[0])) {
                                for (String str : evtContactConfIdArray) {
                                    //协同渠道规则表id（自建表）
                                    Long evtContactConfId = Long.parseLong(str);
                                    //提交线程
                                    //Future<Map<String, Object>> f = executorService.submit(new ChannelTask(evtContactConfId, productList, context, reqId));
                                    //将线程处理结果添加到结果集
                                    //threadList.add(f);
                                    Map<String, Object> channelMap = ChannelTask(evtContactConfId, productList, context, reqId);
                                    if (channelMap != null && !channelMap.isEmpty()) {
                                        taskChlList.add(channelMap);
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
                }
                ruleMap.put("taskChlList", taskChlList);
                if (taskChlList.size() > 0) {
                    jsonObject.put("hit", true);
                } else {
                    jsonObject.put("hit", false);
                    jsonObject.put("msg", "渠道均未命中");
                    esHitService.save(jsonObject, IndexList.RULE_MODULE);
                    return Collections.EMPTY_MAP;
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
    }

    private Map<String, Object> ChannelTask(Long evtContactConfId, List<Map<String, String>> productList, DefaultContext<String, Object> context, String reqId) {

        Date now = new Date();

        long begin = System.currentTimeMillis();

        //初始化返回结果推荐信息
        Map<String, Object> channelMap = new ConcurrentHashMap<>();

        List<Map<String, Object>> taskChlAttrList = new ArrayList<>();
        Map<String, Object> taskChlAttr;

        //查询渠道属性，渠道生失效时间过滤
        MktCamChlConfDetail mktCamChlConfDetail = null;
        List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = new ArrayList<>();
        if (mktCamChlConfDetail == null) {
            // 从数据库中获取并拼成mktCamChlConfDetail对象存入redis
            MktCamChlConfDO mktCamChlConfDO = new MktCamChlConfDO();
            mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(evtContactConfId);
            mktCamChlConfAttrDOList = mktCamChlConfAttrMapper.selectByEvtContactConfId(evtContactConfId);
            List<MktCamChlConfAttr> mktCamChlConfAttrList = new ArrayList<>();
            mktCamChlConfDetail = BeanUtil.create(mktCamChlConfDO, new MktCamChlConfDetail());
            for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList) {
                MktCamChlConfAttr mktCamChlConfAttrNew = BeanUtil.create(mktCamChlConfAttrDO, new MktCamChlConfAttr());
                mktCamChlConfAttrList.add(mktCamChlConfAttrNew);
            }
            mktCamChlConfDetail.setMktCamChlConfAttrList(mktCamChlConfAttrList);
            redisUtils.set("CHL_CONF_DETAIL_" + evtContactConfId, mktCamChlConfDetail);
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
                        return Collections.EMPTY_MAP;
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
            return Collections.EMPTY_MAP;
        }

        //渠道信息
        if (mktCamChlConfDetail.getContactChlCode() == null) {
            Channel channelMessage = contactChannelMapper.selectByPrimaryKey(mktCamChlConfDetail.getContactChlId());
            mktCamChlConfDetail.setContactChlCode(channelMessage.getContactChlCode());
            redisUtils.set("CHL_CONF_DETAIL_" + evtContactConfId, mktCamChlConfDetail);
        }
        channelMap.put("channelId", mktCamChlConfDetail.getContactChlCode());
        //查询渠道id
        channelMap.put("channelConfId", mktCamChlConfDetail.getContactChlId().toString()); //渠道id
        channelMap.put("pushType", mktCamChlConfDetail.getPushType()); //推送类型

        channelMap.put("pushTime", ""); // 推送时间

        //返回结果中添加销售品信息
        channelMap.put("productList", JSONArray.toJSON(productList));

        //查询渠道子策略 这里老系统暂时不返回
//              List<MktVerbalCondition> mktVerbalConditions = mktVerbalConditionMapper.findConditionListByVerbalId(evtContactConfId);

        //查询话术
        List<String> scriptLabelList = new ArrayList<>();
        String contactScript = null;
        String mktVerbalStr = null;
        // 从redis获取的mktCamChlConfDetail中获取脚本
        CamScript camScript = mktCamChlConfDetail.getCamScript();
        if (camScript == null) {
            // 数据库中获取脚本存入redis
            camScript = mktCamScriptMapper.selectByConfId(evtContactConfId);
            mktCamChlConfDetail.setCamScript(camScript);
            redisUtils.set("CHL_CONF_DETAIL_" + evtContactConfId, mktCamChlConfDetail);
        }

        if (camScript != null) {
            contactScript = camScript.getScriptDesc();
            if (contactScript != null) {
                scriptLabelList.addAll(subScript(contactScript));
            }
        } else {
            //未查询到话术 不命中
            return Collections.EMPTY_MAP;
        }

        //查询指引
        // 从redis获取的mktCamChlConfDetail中获取指引
        List<VerbalVO> verbalVOList = mktCamChlConfDetail.getVerbalVOList();
        if (verbalVOList == null) {
            List<MktVerbal> mktVerbals = mktVerbalMapper.findVerbalListByConfId(evtContactConfId);
            verbalVOList = new ArrayList<>();
            for (MktVerbal mktVerbal : mktVerbals) {
                VerbalVO verbalVO = BeanUtil.create(mktVerbal, new VerbalVO());
                verbalVOList.add(verbalVO);
            }
            mktCamChlConfDetail.setVerbalVOList(verbalVOList);
            redisUtils.set("CHL_CONF_DETAIL_" + evtContactConfId, mktCamChlConfDetail);
        }

        if (verbalVOList.size() > 0) {
            for (VerbalVO verbalVO : verbalVOList) {
                //查询指引规则 todo
//                        List<MktVerbalCondition> channelConditionList = mktVerbalConditionMapper.findChannelConditionListByVerbalId(mktVerbal.getVerbalId());

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
        String channelFilterCode = (String) redisUtils.get("CHANNEL_FILTER_CODE");
        if (channelFilterCode == null){
            List<SysParams> sysParamsList = sysParamsMapper.listParamsByKeyForCampaign("CHANNEL_FILTER_CODE");
            if (sysParamsList != null && sysParamsList.size() > 0) {
                channelFilterCode = sysParamsList.get(0).getParamValue();
                redisUtils.set("CHANNEL_FILTER_CODE", channelFilterCode);
            }
        }
        int index = -1;
        if (channelFilterCode != null) {
            index = channelFilterCode.indexOf(mktCamChlConfDetail.getContactChlCode());
        }

        if (index >= 0) {
            //判断脚本中有无未查询到的标签
            if (contactScript != null) {
                if (subScript(contactScript).size() > 0) {
//                    System.out.println("推荐话术标签替换含有无值的标签");
                    return Collections.EMPTY_MAP;
                }
            }

            //痛痒点
            if (mktVerbalStr != null) {
                if (subScript(mktVerbalStr).size() > 0) {
//                    System.out.println("推荐指引标签替换含有无值的标签");
                    return Collections.EMPTY_MAP;
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

    private Map<String, Object> getLabelValue(JSONObject param) {
        //更换为dubbo因子查询-----------------------------------------------------
        Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(param));
        return dubboResult;
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
            Date dateRight = dateFormat.parse(rightParam);
            // 左参跟右参对比
            int countDay = dateLeft.compareTo(dateRight);
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

    public String cpcExpression(String code, String type, String rightParam) {
        StringBuilder express = new StringBuilder();

        // 从redis 中获取所有的时间类型标签集合
        List<String> labelCodeList = (List<String>) redisUtils.get("LABEL_CODE_LIST");
        if (labelCodeList == null) {
            labelCodeList = injectionLabelMapper.selectLabelCodeByType("1100");// 1100 代表为时间类型的标签
            if (labelCodeList != null) {
                redisUtils.set("LABEL_CODE_LIST", labelCodeList);
            }
        }


        if ("PROM_LIST".equals(code)) {
            express.append("(checkProm(").append(code).append(",").append(type).append(",").append(rightParam);
            express.append("))");
        } else if (labelCodeList.contains(code)) {
            // todo 时间类型标签
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


    public static String cpcLabel(Label label, String type, String rightParam) {
        StringBuilder express = new StringBuilder();
        express.append("if(");

        if ("7100".equals(type)) {
            express.append("!");
        }
        express.append("(");
        express.append(assLabel(label, type, rightParam));
        express.append(") {return true}");

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


    public static String assLabel(Label label, String type, String rightParam) {
        StringBuilder express = new StringBuilder();
        switch (type) {
            case "1000":
                express.append("toNum(").append(label.getInjectionLabelCode()).append("))");
                express.append(" > ");
                express.append(rightParam);
                break;
            case "2000":
                express.append("toNum(").append(label.getInjectionLabelCode()).append("))");
                express.append(" < ");
                express.append(rightParam);
                break;
            case "3000":
                express.append(label.getInjectionLabelCode()).append(")");
                express.append(" == ");
                if (NumberUtils.isNumber(rightParam)) {
                    express.append(rightParam);
                } else {
                    express.append("\"").append(rightParam).append("\"");
                }
                break;
            case "4000":
                express.append(label.getInjectionLabelCode()).append(")");
                express.append(" != ");
                if (NumberUtils.isNumber(rightParam)) {
                    express.append(rightParam);
                } else {
                    express.append("\"").append(rightParam).append("\"");
                }

                break;
            case "5000":
                express.append("toNum(").append(label.getInjectionLabelCode()).append("))");
                express.append(" >= ");
                express.append(rightParam);
                break;
            case "6000":
                express.append("toNum(").append(label.getInjectionLabelCode()).append("))");
                express.append(" <= ");
                express.append(rightParam);
                break;
            case "7100":
            case "7000":
                express.append(label.getInjectionLabelCode()).append(")");
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
            case "7200":
                express.append("toNum(").append(label.getInjectionLabelCode()).append("))");
                String[] strArray2 = rightParam.split(",");
                express.append(" >= ").append(strArray2[0]);
                express.append(" && ").append("(toNum(");
                express.append(label.getInjectionLabelCode()).append("))");
                express.append(" <= ").append(strArray2[1]);

        }
        return express.toString();
    }


    /**
     * 异步执行当个标签比较结果
     */
    class labelResultThread implements Runnable {
        private JSONObject esJson;
        private List<Map<String, String>> labelMapList;
        private DefaultContext<String, Object> context;
        private SysParams sysParams;
        private ExpressRunner runner;
        private List<LabelResult> labelResultList;

        public labelResultThread(JSONObject esJson, List<Map<String, String>> labelMapList, DefaultContext<String, Object> context, SysParams sysParams, ExpressRunner runner, List<LabelResult> labelResultList) {
            this.esJson = esJson;
            this.labelMapList = labelMapList;
            this.context = context;
            this.sysParams = sysParams;
            this.runner = runner;
            this.labelResultList = labelResultList;
        }

        @Override
        public void run() {
            // 从redis 中获取所有的时间类型标签集合
            List<String> labelCodeList = (List<String>) redisUtils.get("LABEL_CODE_LIST");
            if (labelCodeList == null) {
                labelCodeList = injectionLabelMapper.selectLabelCodeByType("1100");// 1100 代表为时间类型的标签
                if (labelCodeList != null) {
                    redisUtils.set("LABEL_CODE_LIST", labelCodeList);
                }
            }
            for (Map<String, String> labelMap : labelMapList) {
                String type = labelMap.get("operType");
                //保存标签的es log
                LabelResult lr = new LabelResult();
                lr.setOperType(type);
                lr.setLabelCode(labelMap.get("code"));
                lr.setLabelName(labelMap.get("name"));
                if ("PROM_LIST".equals(labelMap.get("code"))) {
                    FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(Long.valueOf(labelMap.get("rightParam")));
                    lr.setRightOperand(filterRule.getChooseProduct());
                } else {
                    lr.setRightOperand(labelMap.get("rightParam"));
                }

                lr.setClassName(labelMap.get("className"));

                //判断标签实例是否足够
                if (context.containsKey(labelMap.get("code"))) {
                    lr.setRightParam(context.get(labelMap.get("code")).toString());
                    if (sysParams != null && "1".equals(sysParams.getParamValue())) {
                        try {

                            if (labelCodeList != null && labelCodeList.contains(labelMap.get("code"))) {
                                ExpressRunner runnerQ = new ExpressRunner();

                                //将规则拼装为表达式
                                StringBuilder expressSb = new StringBuilder();
                                expressSb.append("if(");
                                expressSb.append(cpcExpression(labelMap.get("code"), type, labelMap.get("rightParam")));
                                runnerQ.addFunction("toNum", new StringToNumOperator("toNum"));
                                runnerQ.addFunction("checkProm", new PromCheckOperator("checkProm"));
                                runnerQ.addFunction("dateLabel", new ComperDateLabel("dateLabel"));
                                expressSb.append(") {return true} else {return false}");
                                RuleResult ruleResult = runnerQ.executeRule(expressSb.toString(), context, true, true);
                                if (null != ruleResult.getResult()) {
                                    lr.setResult((Boolean) ruleResult.getResult());
                                } else {
                                    lr.setResult(false);
                                }
                            } else {
                                RuleResult ruleResultOne = runner.executeRule(cpcLabel(labelMap.get("code"), type, labelMap.get("rightParam")), context, true, true);
                                if (null != ruleResultOne.getResult()) {
                                    lr.setResult((Boolean) ruleResultOne.getResult());
                                } else {
                                    lr.setResult(false);
                                }
                            }

                        } catch (Exception e) {
                            lr.setResult(false);
                        }
                    }
                } else {
                    lr.setRightParam("无值");
                    lr.setResult(false);
                }
                labelResultList.add(lr);

            }
            esJson.put("labelResultList", JSONArray.toJSON(labelResultList));
            esHitService.save(esJson, IndexList.Label_MODULE);  //储存标签比较结果
        }
    }

}