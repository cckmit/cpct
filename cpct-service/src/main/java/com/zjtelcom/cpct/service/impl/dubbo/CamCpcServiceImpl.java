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
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.channel.VerbalVO;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.elastic.config.IndexList;
import com.zjtelcom.cpct.enums.AreaNameEnum;
import com.zjtelcom.cpct.service.dubbo.CamCpcService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zjtelcom.cpct.enums.Operator.BETWEEN;

@Service
public class CamCpcServiceImpl implements CamCpcService {

    @Value("${thread.maxPoolSize}")
    private int maxPoolSize;
    @Value("${table.infallible}")
    private String defaultInfallibleTable;

    private static final Logger log = LoggerFactory.getLogger(CamCpcServiceImpl.class);

    @Autowired
    private EsHitsService esHitService;  //es存储

    @Autowired
    private RedisUtils redisUtils;  // redis方法

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
    private EventRedisService eventRedisService;

    @Autowired
    private CoopruleService coopruleService;

    @Autowired(required = false)
    private YzServ yzServ; //因子实时查询dubbo服务

    Map<String, Boolean> flagMap = new ConcurrentHashMap();

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
        esJson.put("context", JSON.toJSONString(context));


        MktCampaignDO mktCampaign = new MktCampaignDO();
        try {
            //查询活动信息
            Map<String, Object> mktCampaignRedis = eventRedisService.getRedis("MKT_CAMPAIGN_", activityId);
            if (mktCampaignRedis != null) {
                mktCampaign = (MktCampaignDO) mktCampaignRedis.get("MKT_CAMPAIGN_" + activityId);
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
        Map<String, Object> filterRuleIdsRedis = eventRedisService.getRedis("MKT_FILTER_RULE_IDS_", activityId);
        List<Long> filterRuleIds = new ArrayList<>();
        if (filterRuleIdsRedis != null) {
            filterRuleIds = (List<Long>) filterRuleIdsRedis.get("MKT_FILTER_RULE_IDS_" + activityId);
        } else {
            // 过滤规则信息查询失败
            esJson.put("hit", false);
            esJson.put("msg", "过滤规则信息查询失败");
            esHitService.save(esJson, IndexList.ACTIVITY_MODULE, params.get("reqId") + activityId + params.get("accNbr"));
            nonPassedMsg.put("cam_" + activityId, "活动信息查询失败");
            return nonPassedMsg;
        }

        if (filterRuleIds != null && filterRuleIds.size() > 0) {
            //循环并判断过滤规则
            for (Long filterRuleId : filterRuleIds) {
                Map<String, Object> filterRuleRedis = eventRedisService.getRedis("FILTER_RULE_", filterRuleId);
                FilterRule filterRule = null;
                if (filterRuleRedis != null) {
                    filterRule = (FilterRule) filterRuleRedis.get("FILTER_RULE_" + filterRuleId);
                }
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
                            String realProdFilter = null;
                            Map<String, Object> realProdFilterRedis = eventRedisService.getRedis("REAL_PROD_FILTER");
                            if (realProdFilterRedis != null) {
                                realProdFilter = (String) realProdFilterRedis.get("REAL_PROD_FILTER");
                            }
                            Map<String, Object> filterRuleTimeMap = new HashMap<>();
                            // 判断是否进行CRM销售品过滤
                            if (realProdFilter != null && "1".equals(realProdFilter)) {
                                log.info("111------accNbr --->" + privateParams.get("accNbr"));
                                List<String> prodList = new ArrayList<>();
                                CacheResultObject<Set<String>> prodInstIdsObject = iCacheProdIndexQryService.qryProdInstIndex2(privateParams.get("accNbr"));
                                //    log.info("222------prodInstIdsObject --->" + JSON.toJSONString(prodInstIdsObject));
                                if (prodInstIdsObject != null && prodInstIdsObject.getResultObject() != null) {
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
                                                    if (offerInstCacheEntity != null && offerInstCacheEntity.getResultObject() != null) {
                                                        OfferInst offerInst = offerInstCacheEntity.getResultObject();
                                                        //                           log.info("777------offer --->" + JSON.toJSONString(offer));
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
                                                } else {
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
                        Map<String, Object> labelsRedis = eventRedisService.getRedis("FILTER_RULE_DISTURB_" + filterRuleId);
                        List<String> labels = new ArrayList<>();
                        if (labelsRedis != null) {
                            labels = (List<String>) labelsRedis.get("FILTER_RULE_DISTURB_" + filterRuleId);
                            if (labels == null) {
                                //过滤规则信息查询失败
                                esJson.put("hit", false);
                                esJson.put("msg", "过扰规则信息查询失败 byId: " + filterRuleId);
                                esHitService.save(esJson, IndexList.ACTIVITY_MODULE, params.get("reqId") + activityId + privateParams.get("accNbr"));
                                nonPassedMsg.put("cam_" + activityId, "过扰规则信息查询失败 byId: " + filterRuleId);
                                return nonPassedMsg;
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
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("params",params);
        hashMap.put("reqId",params.get("reqId"));
        hashMap.put("privateParams",privateParams);
        hashMap.put("context",context);
        hashMap.put("lanId",lanId);
        hashMap.put("esHitService",esHitService);
        hashMap.put("redisUtils",redisUtils);
        hashMap.put("yzServ",yzServ);
        hashMap.put("iCacheOfferEntityQryService",iCacheOfferEntityQryService);
        hashMap.put("iCacheOfferRelIndexQryService",iCacheOfferRelIndexQryService);
        hashMap.put("iCacheRelEntityQryService",iCacheRelEntityQryService);
        hashMap.put("iCacheProdIndexQryService",iCacheProdIndexQryService);
        hashMap.put("sysParamsService",sysParamsService);
        hashMap.put("coopruleService",coopruleService);
        hashMap.put("eventRedisService",eventRedisService);
        //初始化结果集
        List<Future<Map<String, Object>>> threadList = new ArrayList<>();
        //初始化线程池
//        ExecutorService executorService = Executors.newCachedThreadPool();
        //遍历策略列表
        log.info("strategyMapList = " + JSON.toJSONString(strategyMapList));
        for (Map<String, Object> strategyMap : strategyMapList) {
            Long strategyConfId = (Long) strategyMap.get("strategyConfId");
            String strategyConfName = (String) strategyMap.get("strategyConfName");
            hashMap.put("strategyConfId",strategyConfId);
            hashMap.put("strategyConfName",strategyConfName);
            List<Map<String, Object>> ruleMapList = (List<Map<String, Object>>) strategyMap.get("ruleMapList");
            if (ruleMapList != null && ruleMapList.size() > 0) {
                for (Map<String, Object> ruleMap : ruleMapList) {
                    Long ruleId = (Long) ruleMap.get("ruleId");
                    String ruleName = (String) ruleMap.get("ruleName");
                    Long tarGrpId = (Long) ruleMap.get("tarGrpId");
                    String productId = (String) ruleMap.get("productId");
                    String evtContactConfId = (String) ruleMap.get("evtContactConfId");
                    flagMap.put(ruleId.toString(), false);
                    //修改
                    hashMap.put("tarGrpId",tarGrpId);
                    hashMap.put("productId",productId);
                    hashMap.put("evtContactConfId",evtContactConfId);
                    hashMap.put("ruleId",ruleId);
                    hashMap.put("ruleName",ruleName);

                    Future<Map<String, Object>> f = ThreadPool.submit(new RuleTaskServiceImpl(hashMap));
                    //将线程处理结果添加到结果集
                    threadList.add(f);
                }
            }
        }
        //获取结果
        try {
            for (Future<Map<String, Object>> future : threadList) {
                try {
                    Map<String, Object> futureMap = future.get();
                    if (!futureMap.isEmpty()) {
                        Boolean flag = true;
                        for (String key : futureMap.keySet()) {
                            if (key.contains("rule_")) {
                                flag = false;
                                nonPassedMsg.put(key, futureMap.get(key));
                            }
                        }
                        /*if (futureMap.get("nonPassedMsg") != null) {
                            flag = false;
                            nonPassedMsg.putAll((Map<String, Object>) futureMap.get("nonPassedMsg"));
                        }*/
                        if (flag) ruleList.add(futureMap);
                    }
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
            activity.put("nonPassedMsg", nonPassedMsg);

            boolean isWithDefaultLabel = false;
            for (Map.Entry entry : flagMap.entrySet()) {
                if (true == Boolean.valueOf(entry.getValue().toString())) {
                    isWithDefaultLabel = true;
                    break;
                }
            }

            if (isWithDefaultLabel) {
                // 判断是否有命中
                if (ruleList.size() > 1) {
                    List<String> ruleIdList = new ArrayList<>();
                    for (Map<String, Object> map : ruleList) {
                        Long ruleId2 = Long.valueOf(map.get("nowRuleId") == null ? "0" : map.get("nowRuleId").toString());
                        ruleIdList.add(ruleId2.toString());
                    }

                    // 非固定规则有命中的情况下，从命中列表中移出默认固定规则
                    for (Map<String, Object> strategyMap : strategyMapList) {
                        Long strategyConfId = (Long) strategyMap.get("strategyConfId");
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
                }
                activity.put("ruleList", ruleList);
                Map<String, Object> itgTrigger;
                //查询展示列 （iSale）   todo  展示列的标签未查询到是否影响命中
                //查询展示列 （iSale）   todo  展示列的标签未查询到是否影响命中
                List<Map<String, Object>> iSaleDisplay = new ArrayList<>();
                Map<String, Object> iSaleDisplayRedis = eventRedisService.getRedis("MKT_ISALE_LABEL_", mktCampaign.getIsaleDisplay());
                if (iSaleDisplayRedis != null) {
                    iSaleDisplay = (List<Map<String, Object>>) iSaleDisplayRedis.get("MKT_ISALE_LABEL_" + mktCampaign.getIsaleDisplay());
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
                        if (evtContent != null) {
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
            } else {
                //判断是否有命中
                if (ruleList.size() > 0) {
                    activity.put("ruleList", ruleList);
                    esJson.put("hit", true); //添加命中标识

                    Map<String, Object> itgTrigger;

                    //查询展示列 （iSale）   todo  展示列的标签未查询到是否影响命中
                    List<Map<String, Object>> iSaleDisplay = new ArrayList<>();
                    Map<String, Object> iSaleDisplayRedis = eventRedisService.getRedis("MKT_ISALE_LABEL_", mktCampaign.getIsaleDisplay());
                    if (iSaleDisplayRedis != null) {
                        iSaleDisplay = (List<Map<String, Object>>) iSaleDisplayRedis.get("MKT_ISALE_LABEL_" + mktCampaign.getIsaleDisplay());
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
//            executorService.shutdownNow();
        } finally {
            //关闭线程池
//            executorService.shutdownNow();
        }

        return activity;
    }



}