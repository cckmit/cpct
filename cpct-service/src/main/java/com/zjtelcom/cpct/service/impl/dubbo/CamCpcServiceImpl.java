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
import com.zjtelcom.cpct.domain.campaign.MktCamChlConfAttrDO;
import com.zjtelcom.cpct.domain.campaign.MktCamChlConfDO;
import com.zjtelcom.cpct.domain.campaign.MktCamItem;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.channel.VerbalVO;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.elastic.config.IndexList;
import com.zjtelcom.cpct.elastic.service.EsHitService;
import com.zjtelcom.cpct.enums.AreaNameEnum;
import com.zjtelcom.cpct.service.dubbo.CamCpcService;
import com.zjtelcom.cpct.service.es.CoopruleService;
import com.zjtelcom.cpct.service.es.EsHitsService;
import com.zjtelcom.cpct.service.system.SysParamsService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.RedisUtils;
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
public class CamCpcServiceImpl implements CamCpcService {

    @Value("${thread.maxPoolSize}")
    private int maxPoolSize;
    @Value("${table.infallible}")
    private String defaultInfallibleTable;

    private static final Logger log = LoggerFactory.getLogger(CamCpcServiceImpl.class);

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
    private EsHitsService esHitService;  //es存储

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

    @Autowired
    private SysParamsService sysParamsService;

    @Autowired
    private CoopruleService coopruleService;

    Map<String,Boolean> flagMap = new ConcurrentHashMap();

    /**
     * 活动级别验证
     */
    @Override
    public Map<String, Object> ActivityCpcTask(Map<String, String> params, Long activityId, Map<String, String> privateParams, Map<String, String> laubelItems, List<Map<String, Object>> evtTriggers, List<Map<String, Object>> strategyMapList, DefaultContext<String, Object> context) {
        log.info("进入ActivityCpcTask...");

        //
        Map<String, Object> nonPassedMsg = new HashMap<>();

        Map<String, Object> activity = new ConcurrentHashMap<>();
        long begin = System.currentTimeMillis();
        String reqId = params.get("reqId");
        String lanId = params.get("lanId");

        // 获取本地网的中文名
        String lanName = AreaNameEnum.getNameByLandId(Long.valueOf(lanId));

        //初始化es log
        JSONObject esJson = new JSONObject();
        List<Map<String, Object>> ruleList = new ArrayList<>();

        //es log
        esJson.put("reqId", reqId);
        esJson.put("activityId", activityId);
        esJson.put("integrationId", params.get("integrationId"));
        esJson.put("accNbr", params.get("accNbr"));
        esJson.put("hitEntity", privateParams.get("accNbr")); //命中对象
        esJson.put("eventCode", params.get("eventCode"));

        MktCampaignDO mktCampaign = null;
        try {
            //查询活动基本信息
            try {
                Object campaign =  redisUtils.get("MKT_CAMPAIGN_" + activityId);
                if (campaign!=null){
                    mktCampaign = (MktCampaignDO) campaign;
                }
            } catch (Exception e) {
                log.info("活动信息查询失败，缓存失败");
                e.printStackTrace();
            }
            if (mktCampaign == null) {
                mktCampaign = mktCampaignMapper.selectByPrimaryKey(activityId);
                if (mktCampaign == null) {
                    //活动信息查询失败
                    esJson.put("hit", false);
                    esJson.put("msg", "活动信息查询失败，活动为null");
                    esHitService.save(esJson, IndexList.ACTIVITY_MODULE, params.get("reqId") + activityId + params.get("accNbr"));
                    nonPassedMsg.put("cam_" + activityId, "活动信息查询失败");
                    return nonPassedMsg;
                } else {
                    redisUtils.set("MKT_CAMPAIGN_" + activityId, mktCampaign);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            esJson.put("hit", false);
            esJson.put("msg", "活动信息查询失败");
            esHitService.save(esJson, IndexList.ACTIVITY_MODULE, params.get("reqId") + activityId + params.get("accNbr"));
            nonPassedMsg.put("cam_" + activityId, "活动信息查询失败");
            return nonPassedMsg;
        }

        if (mktCampaign == null) {
            //活动信息查询失败
            esJson.put("hit", false);
            esJson.put("msg", "活动信息查询失败，活动为null");
            esHitService.save(esJson, IndexList.ACTIVITY_MODULE, params.get("reqId") + activityId + params.get("accNbr"));
            nonPassedMsg.put("cam_" + activityId, "活动信息查询失败");
            return nonPassedMsg;
        }

        privateParams.put("activityId", mktCampaign.getMktCampaignId().toString()); //活动Id
        privateParams.put("activityName", mktCampaign.getMktCampaignName()); //活动名称
        if ("1000".equals(mktCampaign.getMktCampaignType())) {
            privateParams.put("activityType", "0"); //营销
        } else if ("5000".equals(mktCampaign.getMktCampaignType())) {
            privateParams.put("activityType", "1"); //服务
        } else if ("6000".equals(mktCampaign.getMktCampaignType())) {
            privateParams.put("activityType", "2"); //随销
        } else {
            privateParams.put("activityType", "0"); //活动类型 默认营销
        }
        if ("0".equals(mktCampaign.getIsCheckRule()) || "校验".equals(mktCampaign.getIsCheckRule())) {
            privateParams.put("skipCheck", "0"); //是否预校验
        } else {
            privateParams.put("skipCheck", "1"); //是否预校验
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        privateParams.put("activityStartTime", simpleDateFormat.format(mktCampaign.getPlanBeginTime())); //活动开始时间
        privateParams.put("activityEndTime", simpleDateFormat.format(mktCampaign.getPlanEndTime())); //活动结束时间

        //es log
        esJson.put("activityId", mktCampaign.getMktCampaignId().toString());
        esJson.put("activityName", mktCampaign.getMktCampaignName());
        esJson.put("activityCode", mktCampaign.getMktActivityNbr());
        esJson.put("reqId", reqId);

        //活动标签实例查询完成 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

        //iSale展示列参数对象初始化
        List<Map<String, Object>> itgTriggers = new ArrayList<>();
        //验证过滤规则 活动级
        Object o = redisUtils.get("MKT_FILTER_RULE_IDS_" + activityId);
        List<Long> filterRuleIds = null;
        if (o!=null){
            filterRuleIds = (List<Long>)o;
        }
        if (filterRuleIds == null) {
            filterRuleIds = mktStrategyFilterRuleRelMapper.selectByStrategyId(activityId);
            if (filterRuleIds == null) {
                //过滤规则信息查询失败
                esJson.put("hit", false);
                esJson.put("msg", "过滤规则信息查询失败");
                esHitService.save(esJson, IndexList.ACTIVITY_MODULE, params.get("reqId") + activityId + params.get("accNbr"));
                nonPassedMsg.put("cam_" + activityId, "活动信息查询失败");
                return nonPassedMsg;
            } else {
                redisUtils.set("MKT_FILTER_RULE_IDS_" + activityId, filterRuleIds);
            }
        }

        if (filterRuleIds != null && filterRuleIds.size() > 0) {
            //循环并判断过滤规则
            for (Long filterRuleId : filterRuleIds) {
                FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(filterRuleId);
                //判断过滤类型(红名单，黑名单)
                if (filterRule != null) {
                    if ("3000".equals(filterRule.getFilterType())) {  //销售品过滤
                        boolean productCheck = true; // 默认拦截
                        //获取需要过滤的销售品
                        String checkProduct = filterRule.getChooseProduct();
                        String s = sysParamsService.systemSwitch("PRODUCT_FILTER_SWITCH");
                        if (s != null && s.equals("code")) {
                            checkProduct = filterRule.getChooseProductCode();
                        }
                        if (checkProduct != null && !"".equals(checkProduct)) {
                            String esMsg = "";
                            //获取用户已办理销售品
                            String productStr = "";
                            // 销售品过滤
                            String realProdFilter = (String) redisUtils.get("REAL_PROD_FILTER");
                            if (realProdFilter == null) {
                                List<SysParams> sysParamsList = sysParamsMapper.listParamsByKeyForCampaign("REAL_PROD_FILTER");
                                if (sysParamsList != null && sysParamsList.size() > 0) {
                                    realProdFilter = sysParamsList.get(0).getParamValue();
                                    redisUtils.set("REAL_PROD_FILTER", realProdFilter);
                                }
                            }
                            Map<String, Object> filterRuleTimeMap = new HashMap<>();
                            // 判断是否进行CRM销售品过滤
                            if (realProdFilter != null && "1".equals(realProdFilter)) {
                                log.info("111------accNbr --->" + privateParams.get("accNbr"));
                                List<String> prodList = new ArrayList<>();
                                CacheResultObject<Set<String>> prodInstIdsObject = iCacheProdIndexQryService.qryProdInstIndex2(privateParams.get("accNbr"));
                            //    log.info("222------prodInstIdsObject --->" + JSON.toJSONString(prodInstIdsObject));
                                if(prodInstIdsObject!=null &&  prodInstIdsObject.getResultObject() !=null ){
                                    Set<String> prodInstIds = prodInstIdsObject.getResultObject();
                                    for (String prodInstId : prodInstIds) {
                                        // 根据prodInstId 和 statusCd(1000-有效)查询offerProdInstRelId
                            //            log.info("333------prodInstId --->" + prodInstId);
                                        CacheResultObject<Set<String>> setCacheResultObject = iCacheOfferRelIndexQryService.qryOfferProdInstRelIndex2(prodInstId, "1000");
                            //            log.info("444------setCacheResultObject --->" + JSON.toJSONString(setCacheResultObject));
                                        if (setCacheResultObject != null && setCacheResultObject.getResultObject() != null) {
                                            Set<String> offerProdInstRelIdSet = setCacheResultObject.getResultObject();
                                            for (String offerProdInstRelId : offerProdInstRelIdSet) {
                                                // 查询销售品产品实例关系缓存实体
                                                CacheResultObject<OfferProdInstRel> offerProdInstRelCacheEntity = iCacheRelEntityQryService.getOfferProdInstRelCacheEntity(offerProdInstRelId);
                            //                    log.info("555------offerProdInstRelCacheEntity --->" + JSON.toJSONString(offerProdInstRelCacheEntity));
                                                if (offerProdInstRelCacheEntity != null && offerProdInstRelCacheEntity.getResultObject() != null) {
                                                        OfferProdInstRel offerProdInstRel = offerProdInstRelCacheEntity.getResultObject();

                                                    // 查询销售品实例缓存实体
                                                    CacheResultObject<OfferInst> offerInstCacheEntity = iCacheOfferEntityQryService.getOfferInstCacheEntity(offerProdInstRel.getOfferInstId().toString());
                            //                        log.info("666------offerInstCacheEntity --->" + JSON.toJSONString(offerInstCacheEntity));
                                                    if(offerInstCacheEntity!=null && offerInstCacheEntity.getResultObject()!=null){
                                                        OfferInst offerInst = offerInstCacheEntity.getResultObject();
                                                        //Offer offer = offerProdMapper.selectByPrimaryKey(Integer.valueOf(offerInst.getOfferId().toString()));
                             //                           log.info("777------offer --->" + JSON.toJSONString(offer));
                                                        //prodStrList.add(offer.getOfferNbr());
                                                        prodList.add(offerInst.getOfferId().toString());
                                                        filterRuleTimeMap.put(offerInst.getOfferId().toString(), offerInst.getEffDate());
                            //                            log.info("888------filterRuleTimeMap --->" + JSON.toJSONString(filterRuleTimeMap));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                productStr = ChannelUtil.StringList2String(prodList);
                                log.info("999------productStr --->" + JSON.toJSONString(productStr));
                            } else if (!context.containsKey("PROM_LIST")) { // 有没有办理销售品--销售列表标签
                                //存在于校验
                                if ("2000".equals(filterRule.getOperator())) { // 不存在
                                    productCheck = false;
                                } else if ("1000".equals(filterRule.getOperator())) { // 存在于
                                    productCheck = true;
                                    esMsg = "未查询到销售品实例";
                                }
                            } else {
                                productStr = (String) context.get("PROM_LIST");
                            }
                            String[] checkProductArr = checkProduct.split(",");
                            if (productStr != null && !"".equals(productStr)) {
                                if ("1000".equals(filterRule.getOperator())) {  //存在于
                                    for (String product : checkProductArr) {
                                        int index = productStr.indexOf(product);
                                        if (index >= 0) {
                                            // 判断是否开启时间段过滤
                                            if ("true".equals(filterRule.getRemark())) {
                                                Date date = (Date) filterRuleTimeMap.get(product);
                                                // 判断时间段
                                                if (date != null && date.after(filterRule.getEffectiveDate()) && date.before(filterRule.getFailureDate())) {
                                                    productCheck = false;
                                                } else{
                                                    productCheck = true;
                                                    esMsg = "销售时间不在竣工时间范围内";
                                                }
                                            } else {
                                                productCheck = false;
                                            }

                                            break;
                                        }
                                    }
                                } else if ("2000".equals(filterRule.getOperator())) { //不存在于

                                    boolean noExistCheck = true;
                                    for (String product : checkProductArr) {
                                        int index = productStr.indexOf(product);
                                        if (index >= 0) {
                                            productCheck = true;
                                            noExistCheck = false;
                                            //被过滤的销售品
                                            esMsg = product;
                                            break;
                                        }
                                    }
                                    if (noExistCheck) {
                                        productCheck = false;
                                    }
                                }
                            } else {
                                //存在于校验
                                if ("2000".equals(filterRule.getOperator())) {
                                    productCheck = false;
                                } else if ("1000".equals(filterRule.getOperator())) {
                                    productCheck = true;
                                }
                            }

                            if (productCheck) {
                                esJson.put("hit", "false");
                                esJson.put("msg", "销售品过滤验证未通过:" + esMsg);
                                esHitService.save(esJson, IndexList.ACTIVITY_MODULE, params.get("reqId") + activityId + privateParams.get("accNbr"));
                                nonPassedMsg.put("cam_" + activityId, "销售品过滤验证未通过:" + esMsg);
                                return nonPassedMsg;
                            }
                        }
                    } else if ("4000".equals(filterRule.getFilterType())) {  //表达式过滤
                        //暂不处理
                        //do something
                    } else if ("6000".equals(filterRule.getFilterType())) {  //过扰规则
                        //将过扰规则的标签放到iSale展示列
                        //获取过扰标签
                        List<String> labels = (List<String>) redisUtils.get("FILTER_RULE_DISTURB_" + filterRuleId);
                        if (labels == null) {
                            labels = mktVerbalConditionMapper.getLabelListByConditionId(filterRule.getConditionId());
                            if (labels == null) {
                                //过滤规则信息查询失败
                                esJson.put("hit", false);
                                esJson.put("msg", "过扰规则信息查询失败 byId: " + filterRuleId);
                                esHitService.save(esJson, IndexList.ACTIVITY_MODULE, params.get("reqId") + activityId + privateParams.get("accNbr"));
                                nonPassedMsg.put("cam_" + activityId, "过扰规则信息查询失败 byId: " + filterRuleId);
                                return nonPassedMsg;
                            } else {
                                redisUtils.set("FILTER_RULE_DISTURB_" + filterRuleId, labels);
                            }
                        }

                        List<Map<String, Object>> triggerList = new ArrayList<>();
                        if (labels != null && labels.size() > 0) {
                            for (String labelCode : labels) {
                                if (context.containsKey(labelCode)) {
                                    Map<String, Object> map = new ConcurrentHashMap<>();
                                    map.put("key", labelCode);
                                    map.put("value", context.get(labelCode));
                                    map.put("display", "0");
                                    map.put("name", "");
                                    triggerList.add(map);
                                } else {
                                    //todo 过扰标签未查询到
                                }
                            }
                        }
                        Map<String, Object> disturb = new ConcurrentHashMap<>();
                        disturb.put("type", "disturb");
                        disturb.put("triggerList", triggerList);
                        itgTriggers.add(disturb);
                    }
                }
            }
        }

        //初始化结果集
        List<Future<Map<String, Object>>> threadList = new ArrayList<>();
        //初始化线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        //遍历策略列表
        for (Map<String, Object> strategyMap : strategyMapList) {
            //提交线程
               /* Future<Map<String, Object>> f = executorService.submit(
                        new StrategyTask(params, (Long) strategyMap.get("strategyConfId"), (String) strategyMap.get("strategyConfName"),
                                privateParams, context));*/
            Long strategyConfId = (Long) strategyMap.get("strategyConfId");
            String strategyConfName = (String) strategyMap.get("strategyConfName");
            List<Map<String, Object>> ruleMapList = (List<Map<String, Object>>) strategyMap.get("ruleMapList");
            if (ruleMapList != null && ruleMapList.size() > 0) {
                for (Map<String, Object> ruleMap : ruleMapList) {
                    Long ruleId = (Long) ruleMap.get("ruleId");
                    String ruleName = (String) ruleMap.get("ruleName");
                    Long tarGrpId = (Long) ruleMap.get("tarGrpId");
                    String productId = (String) ruleMap.get("productId");
                    String evtContactConfId = (String) ruleMap.get("evtContactConfId");
                    flagMap.put(ruleId.toString(), false);
                    Future<Map<String, Object>> f = executorService.submit(new RuleTask(params, privateParams, strategyConfId, strategyConfName, tarGrpId, productId, evtContactConfId, ruleId, ruleName, context, lanId));
                    //将线程处理结果添加到结果集
                    threadList.add(f);
                }
            }
        }
        //获取结果
        try {
            for (Future<Map<String, Object>> future : threadList) {
                try {
                    Map<String,Object> futureMap =  future.get();
                    if (!futureMap.isEmpty()) {
                        Boolean flag = true;
                        for (String key : futureMap.keySet()) {
                            if (key.contains("rule_")) {
                                flag = false;
                                nonPassedMsg.put(key, futureMap.get(key));
                                // break;
                            }
                        }
                        if (flag) ruleList.add(futureMap);
                    }
                    activity.put("nonPassedMsg", nonPassedMsg);
                } catch (InterruptedException e) {
                    esJson.put("hit", "false");
                    esJson.put("msg", "规则校验出错:" + e.getMessage());
                    esHitService.save(esJson, IndexList.ACTIVITY_MODULE, params.get("reqId") + activityId + privateParams.get("accNbr"));
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    esJson.put("hit", "false");
                    esJson.put("msg", "规则校验出错:" + e.getMessage());
                    esHitService.save(esJson, IndexList.ACTIVITY_MODULE, params.get("reqId") + activityId + privateParams.get("accNbr"));
                    e.printStackTrace();
                }
            }

            boolean isWithDefaultLabel = false;
            for (Map.Entry entry:flagMap.entrySet()) {
               if(true == Boolean.valueOf(entry.getValue().toString())){
                   isWithDefaultLabel = true;
                   break;
               }
            }

            if(isWithDefaultLabel) {
                // 判断是否有命中
                if (ruleList.size() > 1) {
                    List<String> ruleIdList = new ArrayList<>();
                    for (Map<String,Object> map : ruleList) {
                        Long ruleId2 = Long.valueOf(map.get("nowRuleId") == null ? "0" : map.get("nowRuleId").toString());
                        ruleIdList.add(ruleId2.toString());
                    }

                    // 非固定规则有命中的情况下，从命中列表中移出默认固定规则
                    for (Map<String, Object> strategyMap : strategyMapList) {
                        Long strategyConfId = (Long) strategyMap.get("strategyConfId");
                        // String ruleId = redisUtils.get("LEFT_PARAM_FLAG" + strategyConfId) == null? "":redisUtils.get("LEFT_PARAM_FLAG" + strategyConfId).toString();
                        Object object = redisUtils.hgetAllField("LEFT_PARAM_FLAG" + strategyConfId) == null ? "" : redisUtils.hgetAllField("LEFT_PARAM_FLAG" + strategyConfId);
                        if (object != null && object != "") {
                            List<String> list = (List<String>) object;
                            Iterator<Map<String, Object>> iterator = ruleList.iterator();
                            while (iterator.hasNext()) {
                                Map<String, Object> map = iterator.next();
                                Long ruleId2 = Long.valueOf(map.get("nowRuleId") == null ? "0" : map.get("nowRuleId").toString());
                                for (String field : list) {
                                    if (field.equals(ruleId2.toString())) {
                                        if (list.containsAll(ruleIdList)) {
                                            ruleList.remove(map);
                                        } else {
                                            iterator.remove();
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } /*else {
                    // 非固定规则无命中的情况下，从命中列表中移出不匹配采集项配置的固定规则
                    // 取采集项标签，按照采集项标签执行固定必中规则
                    if (s == null || s.equals("")) {
                        Object o1 = context.get(s);
                        if (o1 != null && "".equals(o1.toString())) {
                            String s1 = o1.toString();
                        }
                    }
                    // 移出固定规则中不匹配采集项值的规则

                }*/
                activity.put("ruleList", ruleList);
                Map<String, Object> itgTrigger;
                //查询展示列 （iSale）   todo  展示列的标签未查询到是否影响命中
                List<Map<String, Object>> iSaleDisplay = new ArrayList<>();
                iSaleDisplay = (List<Map<String, Object>>) redisUtils.get("MKT_ISALE_LABEL_" + mktCampaign.getIsaleDisplay());
                if (iSaleDisplay == null) {
                    iSaleDisplay = injectionLabelMapper.listLabelByDisplayId(mktCampaign.getIsaleDisplay());
                    redisUtils.set("MKT_ISALE_LABEL_" + mktCampaign.getIsaleDisplay(), iSaleDisplay);
                }
                if (iSaleDisplay != null && iSaleDisplay.size() > 0) {
                    Map<String, Object> triggers;
                    List<Map<String, Object>> triggerList1 = new ArrayList<>();
                    List<Map<String, Object>> triggerList2 = new ArrayList<>();
                    List<Map<String, Object>> triggerList3 = new ArrayList<>();
                    List<Map<String, Object>> triggerList4 = new ArrayList<>();

                    for (Map<String, Object> label : iSaleDisplay) {
                        if (context.containsKey((String) label.get("labelCode"))) {
                            triggers = new JSONObject();
                            triggers.put("key", label.get("labelCode"));
                            triggers.put("value", context.get((String) label.get("labelCode")));
                            triggers.put("display", 0); //todo 确定display字段
                            triggers.put("name", label.get("labelName"));
                            if ("1".equals(label.get("typeCode").toString())) {
                                triggerList1.add(triggers);
                            } else if ("2".equals(label.get("typeCode").toString())) {
                                triggerList2.add(triggers);
                            } else if ("3".equals(label.get("typeCode").toString())) {
                                triggerList3.add(triggers);
                            } else if ("4".equals(label.get("typeCode").toString())) {
                                triggerList4.add(triggers);
                            }
                        }
                    }
                    if (triggerList1.size() > 0) {
                        itgTrigger = new ConcurrentHashMap<>();
                        itgTrigger.put("triggerList", triggerList1);
                        itgTrigger.put("type", "固定信息");
                        itgTriggers.add(new JSONObject(itgTrigger));
                    }
                    if (triggerList2.size() > 0) {
                        itgTrigger = new JSONObject();
                        itgTrigger.put("triggerList", triggerList2);
                        itgTrigger.put("type", "营销信息");
                        itgTriggers.add(new JSONObject(itgTrigger));
                    }
                    if (triggerList3.size() > 0) {
                        itgTrigger = new JSONObject();
                        itgTrigger.put("triggerList", triggerList3);
                        itgTrigger.put("type", "费用信息");
                        itgTriggers.add(new JSONObject(itgTrigger));
                    }
                    if (triggerList4.size() > 0) {
                        itgTrigger = new JSONObject();
                        itgTrigger.put("triggerList", triggerList4);
                        itgTrigger.put("type", "协议信息");
                        itgTriggers.add(new JSONObject(itgTrigger));
                    }
                }

                //将iSale展示列的值放入返回结果
                Map<String, Object> evtContent = (Map<String, Object>) JSON.parse(params.get("evtContent"));
                for (Map<String, Object> ruleMap : ruleList) {
                    List<Map<String, Object>> ChlMap = (List<Map<String, Object>>) ruleMap.get("taskChlList");
                    for (Map<String, Object> map : ChlMap) {
                        map.put("itgTriggers", JSONArray.parse(JSONArray.toJSON(itgTriggers).toString()));
                        // map.put("triggers", JSONArray.parse(JSONArray.toJSON(evtTriggers).toString()));
                        List<Map<String, Object>> triggersList = new ArrayList<>();
                        if(evtContent != null){
                            for (Map.Entry entry : evtContent.entrySet()) {
                                Map<String, Object> trigger = new HashMap<>();
                                trigger.put("key", entry.getKey());
                                trigger.put("value", entry.getValue());
                                triggersList.add(trigger);
                            }
                            map.put("triggers", triggersList);
                        }
                    }
                }
                esHitService.save(esJson, IndexList.ACTIVITY_MODULE, params.get("reqId") + activityId + privateParams.get("accNbr"));
            }else {
                //判断是否有命中
                if (ruleList.size() > 0) {
                    activity.put("ruleList", ruleList);
                    esJson.put("hit", true); //添加命中标识

                    Map<String, Object> itgTrigger;

                    //查询展示列 （iSale）   todo  展示列的标签未查询到是否影响命中
                    List<Map<String, Object>> iSaleDisplay = new ArrayList<>();
                    iSaleDisplay = (List<Map<String, Object>>) redisUtils.get("MKT_ISALE_LABEL_" + mktCampaign.getIsaleDisplay());
                    if (iSaleDisplay == null) {
                        iSaleDisplay = injectionLabelMapper.listLabelByDisplayId(mktCampaign.getIsaleDisplay());
                        redisUtils.set("MKT_ISALE_LABEL_" + mktCampaign.getIsaleDisplay(), iSaleDisplay);
                    }

                    if (iSaleDisplay != null && iSaleDisplay.size() > 0) {

                        Map<String, Object> triggers;
                        List<Map<String, Object>> triggerList1 = new ArrayList<>();
                        List<Map<String, Object>> triggerList2 = new ArrayList<>();
                        List<Map<String, Object>> triggerList3 = new ArrayList<>();
                        List<Map<String, Object>> triggerList4 = new ArrayList<>();

                        for (Map<String, Object> label : iSaleDisplay) {
                            if (context.containsKey((String) label.get("labelCode"))) {
                                triggers = new JSONObject();
                                triggers.put("key", label.get("labelCode"));
                                triggers.put("value", context.get((String) label.get("labelCode")));
                                triggers.put("display", 0); //todo 确定display字段
                                triggers.put("name", label.get("labelName"));
                                if ("1".equals(label.get("typeCode").toString())) {
                                    triggerList1.add(triggers);
                                } else if ("2".equals(label.get("typeCode").toString())) {
                                    triggerList2.add(triggers);
                                } else if ("3".equals(label.get("typeCode").toString())) {
                                    triggerList3.add(triggers);
                                } else if ("4".equals(label.get("typeCode").toString())) {
                                    triggerList4.add(triggers);
                                }
                            }
                        }
                        if (triggerList1.size() > 0) {
                            itgTrigger = new ConcurrentHashMap<>();
                            itgTrigger.put("triggerList", triggerList1);
                            itgTrigger.put("type", "固定信息");
                            itgTriggers.add(new JSONObject(itgTrigger));
                        }
                        if (triggerList2.size() > 0) {
                            itgTrigger = new JSONObject();
                            itgTrigger.put("triggerList", triggerList2);
                            itgTrigger.put("type", "营销信息");
                            itgTriggers.add(new JSONObject(itgTrigger));
                        }
                        if (triggerList3.size() > 0) {
                            itgTrigger = new JSONObject();
                            itgTrigger.put("triggerList", triggerList3);
                            itgTrigger.put("type", "费用信息");
                            itgTriggers.add(new JSONObject(itgTrigger));
                        }
                        if (triggerList4.size() > 0) {
                            itgTrigger = new JSONObject();
                            itgTrigger.put("triggerList", triggerList4);
                            itgTrigger.put("type", "协议信息");
                            itgTriggers.add(new JSONObject(itgTrigger));
                        }
                    }

                    //将iSale展示列的值放入返回结果
                    Map<String, Object> evtContent = (Map<String, Object>) JSON.parse(params.get("evtContent"));
                    for (Map<String, Object> ruleMap : ruleList) {
                        List<Map<String, Object>> ChlMap = (List<Map<String, Object>>) ruleMap.get("taskChlList");
                        for (Map<String, Object> map : ChlMap) {
                            map.put("itgTriggers", JSONArray.parse(JSONArray.toJSON(itgTriggers).toString()));
                            // map.put("triggers", JSONArray.parse(JSONArray.toJSON(evtTriggers).toString()));
                            List<Map<String, Object>> triggersList = new ArrayList<>();
                            if (evtContent != null && !evtContent.isEmpty()) {
                                for (Map.Entry entry : evtContent.entrySet()) {
                                    Map<String, Object> trigger = new HashMap<>();
                                    trigger.put("key", entry.getKey());
                                    trigger.put("value", entry.getValue());
                                    triggersList.add(trigger);
                                }
                                map.put("triggers", triggersList);
                            }
                        }
                    }
                } else {
                    esJson.put("hit", false);
                    esJson.put("msg", "策略均未命中");
                }
                esHitService.save(esJson, IndexList.ACTIVITY_MODULE, params.get("reqId") + activityId + privateParams.get("accNbr"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            esJson.put("hit", false);
            esJson.put("msg", "获取计算结果异常");
            esHitService.save(esJson, IndexList.ACTIVITY_MODULE, params.get("reqId") + activityId + privateParams.get("accNbr"));
            //发生异常关闭线程池
            executorService.shutdownNow();
        } finally {
            //关闭线程池
            executorService.shutdownNow();
        }

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

            MktCampaignDO mktCampaignDO = null;
            MktStrategyConfDO mktStrategyConfDO = null;
            MktStrategyConfRuleDO mktStrategyConfRuleDO = null;
            try {
                mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(Long.valueOf(privateParams.get("activityId")));
                mktStrategyConfDO = mktStrategyConfMapper.selectByPrimaryKey(strategyConfId);
                mktStrategyConfRuleDO = mktStrategyConfRuleMapper.selectByPrimaryKey(ruleId);

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
                esJson.put("eventId", params.get("eventCode"));
                esJson.put("activityId",  mktCampaignDO.getMktCampaignId());
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
                jsonObject.put("msg", "类型转换异常"+e.getMessage());
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

            //判断表达式在缓存中有没有
            String express = (String) redisUtils.get("EXPRESS_" + tarGrpId);
            log.info("express:!@#$%"+JSON.toJSONString(express));
            SysParams sysParams = (SysParams) redisUtils.get("EVT_SWITCH_CHECK_LABEL");
            if (sysParams == null) {
                List<SysParams> systemParamList = sysParamsMapper.findParamKeyIn("CHECK_LABEL");
                if (systemParamList.size() > 0) {
                    redisUtils.set("EVT_SWITCH_CHECK_LABEL", systemParamList.get(0));
                }
            }
            String realProdFilter = null;
            try {
                log.info("23456");
                realProdFilter = (String) redisUtils.get("REAL_PROD_FILTER");
                log.info("realProdFilter:!@#$%"+JSON.toJSONString(realProdFilter));
                if (realProdFilter == null) {
                    List<SysParams> sysParamsList = sysParamsMapper.listParamsByKeyForCampaign("REAL_PROD_FILTER");
                    if (sysParamsList != null && sysParamsList.size() > 0) {
                        realProdFilter = sysParamsList.get(0).getParamValue();
                        redisUtils.set("REAL_PROD_FILTER", realProdFilter);
                    }
                }
                if (realProdFilter != null && "1".equals(realProdFilter) && context.get("PROM_LIST")!=null) {
                    List<String> prodList = new ArrayList<>();
                    log.info("111------accNbr --->" + privateParams.get("accNbr"));
                    CacheResultObject<Set<String>> prodInstIdsObject = iCacheProdIndexQryService.qryProdInstIndex2(privateParams.get("accNbr"));
                    if(prodInstIdsObject!=null &&  prodInstIdsObject.getResultObject() !=null ){
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
                                        if(offerInstCacheEntity!=null && offerInstCacheEntity.getResultObject()!=null){
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
                    context.put("PROM_LIST" , productString);
                }
            } catch (Exception e) {
                e.printStackTrace();
                jsonObject.put("hit", "false");
                jsonObject.put("msg", "销售品过滤查询异常"+e.getMessage());
                esHitService.save(jsonObject, IndexList.RULE_MODULE);
                // return Collections.EMPTY_MAP;
                nonPassedMsg.put("rule_" + ruleId, "销售品过滤查询异常");
                return nonPassedMsg;
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
                            // return Collections.EMPTY_MAP;
                            nonPassedMsg.put("rule_" + ruleId, "规则下标签查询失败");
                            return nonPassedMsg;
                        }
                        redisUtils.set("RULE_ALL_LABEL_" + tarGrpId, labelMapList);
                    }

                    if (labelMapList == null || labelMapList.size() <= 0) {
                        log.info("未查询到分群标签:" + privateParams.get("activityId") + "---" + ruleId);
                        jsonObject.put("hit", "false");
                        jsonObject.put("msg", "未查询到分群标签");
                        esHitService.save(jsonObject, IndexList.RULE_MODULE);
                        // return Collections.EMPTY_MAP;
                        nonPassedMsg.put("rule_" + ruleId, "未查询到分群标签");
                        return nonPassedMsg;
                    }

                    //将规则拼装为表达式
                    StringBuilder expressSb = new StringBuilder();
                    expressSb.append("if(");
                    //遍历所有规则
                    for (Map<String, String> labelMap : labelMapList) {
                        if(defaultInfallibleTable.equals(labelMap.get("code"))){
                            // redisUtils.set("LEFT_PARAM_FLAG" + strategyConfId, ruleId);
                            redisUtils.hset("LEFT_PARAM_FLAG" + strategyConfId, ruleId.toString(),"1");
                            flagMap.put(ruleId.toString(), true);
                            log.info(Thread.currentThread().getName() + "flag = true进入...");
                            expressSb.append("true&&");
                            // continue;
                        }
                        String type = labelMap.get("operType");
                        //保存标签的es log
                        lr = new LabelResult();
                        if ("PROM_LIST".equals(labelMap.get("code")) && "1".equals(realProdFilter)) {
                            FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(Long.valueOf(labelMap.get("rightParam")));
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
                    List<Map<String, String>> labelMapList = (List<Map<String, String>>) redisUtils.get("RULE_ALL_LABEL_" + tarGrpId);
                    if (labelMapList == null) {
                        try {
                            labelMapList = tarGrpConditionMapper.selectAllLabelByTarId(tarGrpId);
                        } catch (Exception e) {
                            jsonObject.put("hit", "false");
                            jsonObject.put("msg", "规则下标签查询失败");
                            esHitService.save(jsonObject, IndexList.RULE_MODULE);
                            // return Collections.EMPTY_MAP;
                            nonPassedMsg.put("rule_" + ruleId, "规则下标签查询失败");
                            return nonPassedMsg;
                        }
                        redisUtils.set("RULE_ALL_LABEL_" + tarGrpId, labelMapList);
                    }

                    //遍历所有规则
                    for (Map<String, String> labelMap : labelMapList) {
                        if(defaultInfallibleTable.equals(labelMap.get("code"))){
                            // redisUtils.set("LEFT_PARAM_FLAG" + strategyConfId, ruleId);
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

                    // 异步执行当个标签比较结果
                    new labelResultThread(esJson, labelMapList, context, sysParams, runner, labelResultList).run();

                } catch (Exception e) {
                    e.printStackTrace();
                    jsonObject.put("hit", "false");
                    jsonObject.put("msg", "表达式拼接异常");
                    esHitService.save(jsonObject, IndexList.RULE_MODULE);
                    // return Collections.EMPTY_MAP;
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
                        ruleMap.put("activityId",mktCampaignDO.getInitId().toString()); //活动编码
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
                        ruleMap.put("ruleId",  mktStrategyConfRuleDO.getInitId().toString()); //规则编码
                        ruleMap.put("nowRuleId", ruleId); //新规则编码
                        ruleMap.put("ruleName", ruleName); //规则名称
                        ruleMap.put("promIntegId", promIntegId); // 销售品实例ID
                        ruleMap.put("isMarketRule", flagMap.get(ruleId.toString()) == true ? "0" : "1"); // 是否随销规则标识
                        if (context.get("AREA_ID") != null) {
                            ruleMap.put("areaId", context.get("AREA_ID")); // 落地网格
                        }


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
                                Map<String, Object> channelMap = ChannelTask(evtContactConfId, productList, context, reqId, nonPassedMsg, ruleId);
                                if (channelMap.containsKey("rule_" + ruleId)){
                                    nonPassedMsg.put("rule_" + ruleId, channelMap.remove("rule_" + ruleId));
                                }
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
                                        Map<String, Object> channelMap = ChannelTask(evtContactConfId, productList, context, reqId, nonPassedMsg, ruleId);
                                        if (channelMap != null && !channelMap.isEmpty()) {
                                            if (channelMap.containsKey("rule_" + ruleId)){
                                                nonPassedMsg.put("rule_" + ruleId, channelMap.remove("rule_" + ruleId));
                                            }
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
                        // return Collections.EMPTY_MAP;
                        nonPassedMsg.put("rule_" + ruleId, "规则引擎匹配未通过");
                        return nonPassedMsg;
                    }
                /*} else {
                    jsonObject.put("hit", true);

                    //拼接返回结果
                    ruleMap.put("orderISI", params.get("reqId")); //流水号
                    ruleMap.put("activityId",mktCampaignDO.getInitId().toString()); //活动编码
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
                    ruleMap.put("ruleId",  mktStrategyConfRuleDO.getInitId().toString()); //规则编码
                    ruleMap.put("nowRuleId", ruleId); //新规则编码
                    ruleMap.put("ruleName", ruleName); //规则名称
                    ruleMap.put("promIntegId", promIntegId); // 销售品实例ID
                    ruleMap.put("isMarketRule", flagMap.get(ruleId) == true ? 0 : 1); // 是否随销标志
                    if (context.get("AREA_ID") != null) {
                        ruleMap.put("areaId", context.get("AREA_ID")); // 落地网格
                    }


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
                }*/
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
                String isaleCheck = redisUtils.get("ISALE_CHECK_FLG") == null ? "0" : redisUtils.get("ISALE_CHECK_FLG").toString();
                //isale预校验固定参数
                String loginId = "";
                List<Map<String, String>> sysParam = sysParamsMapper.listParamsByKey("COOL_LOGIN_ID");
                if (sysParam != null && !sysParam.isEmpty()) {
                    loginId = sysParam.get(0).get("value");
                }
//
//                boolean offerVilo = true;
//                if (!taskChlList.isEmpty()) {
//                    List<String> typeList = (List<String>) taskChlList.get(0).get("offerTypeList");
//                    if (typeList.contains("11")) {
//                        offerVilo = false;
//                    }
//                }
                log.info("34567890" +isaleCheck + "   " + loginId);
                //if ("1".equals(isaleCheck) && offerVilo && !loginId.equals("")) {
                if ("1".equals(isaleCheck) && !loginId.equals("")) {
                    Long timeStart = System.currentTimeMillis();
                //    testAddLog(assetRowId, "", "销售品：" + JSON.toJSONString(cpcList.get(0).get("productList")), "", true);
                    log.info("进入受理规则校验，参数：taskChlList = "+ JSON.toJSONString(taskChlList) + ", activityType = " + privateParams.get("activityType") +", integrationId = " + params.get("integrationId") + ", loginId = " + loginId + ", LATN_ID = " + params.get("lanId"));
                    coopruleService.validateProduct(taskChlList, privateParams.get("activityType"), params.get("integrationId"), loginId, params.get("lanId"));
                    Long time = System.currentTimeMillis() - timeStart;
                //    testAddLog(assetRowId, "耗时：" + time + "ms", "", "", true);
                    if (taskChlList == null || taskChlList.isEmpty()) {
                        nonPassedMsg.put("rule_" + ruleId, "");
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
    }

    private Map<String, Object> ChannelTask(Long evtContactConfId, List<Map<String, String>> productList, DefaultContext<String, Object> context, String reqId, Map<String, Object> nonPassedMsg, Long ruleId) {

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
                        // return Collections.EMPTY_MAP;
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
            // return Collections.EMPTY_MAP;
            nonPassedMsg.put("rule_" + ruleId, "渠道生失效时间错误");
            return nonPassedMsg;
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
            // return Collections.EMPTY_MAP;
            nonPassedMsg.put("rule_" + ruleId, "未查询到推送话术");
            return nonPassedMsg;
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
                    // return Collections.EMPTY_MAP;
                    nonPassedMsg.put("rule_" + ruleId, "推荐话术标签替换含有无值的标签");
                    return nonPassedMsg;
                }
            }

            //痛痒点
            if (mktVerbalStr != null) {
                if (subScript(mktVerbalStr).size() > 0) {
//                    System.out.println("推荐指引标签替换含有无值的标签");
                    // return Collections.EMPTY_MAP;
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
                    String checkProduct = filterRule.getChooseProduct();
                    String s = sysParamsService.systemSwitch("PRODUCT_FILTER_SWITCH");
                    if (s != null && s.equals("code")) {
                        checkProduct = filterRule.getChooseProductCode();
                    }
                    lr.setRightOperand(checkProduct);
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