package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ctzj.bss.customer.data.carrier.outbound.api.CtgCacheAssetService;
import com.ctzj.smt.bss.cache.service.api.CacheEntityApi.ICacheIdMappingEntityQryService;
import com.ctzj.smt.bss.cache.service.api.CacheEntityApi.ICacheProdEntityQryService;
import com.ctzj.smt.bss.cache.service.api.CacheEntityApi.ICacheRelEntityQryService;
import com.ctzj.smt.bss.cache.service.api.CacheIndexApi.ICacheOfferRelIndexQryService;
import com.ctzj.smt.bss.cache.service.api.CacheIndexApi.ICacheProdIndexQryService;
import com.ctzj.smt.bss.cache.service.api.model.CacheResultObject;
import com.ctzj.smt.bss.customer.model.dataobject.OfferProdInstRel;
import com.ctzj.smt.bss.customer.model.dataobject.ProdInst;
import com.ctzj.smt.bss.customer.model.dataobject.RowIdMapping;
import com.ql.util.express.DefaultContext;
import com.telin.dubbo.service.QueryBindByAccCardService;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.channel.EventItem;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dubbo.service.*;
import com.zjtelcom.cpct.elastic.config.IndexList;
import com.zjtelcom.cpct.service.es.EsHitsService;
import com.zjtelcom.cpct.service.event.EventRedisService;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.ThreadPool;
import com.zjtelcom.es.es.service.EsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@Transactional
public class CpcServiceImpl implements CpcService {

    private static final Logger log = LoggerFactory.getLogger(CpcServiceImpl.class);
    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper; //事件与活动关联表

    @Autowired
    private EsHitsService esHitService;  //es存储

    @Autowired(required = false)
    private YzServ yzServ; //因子实时查询dubbo服务

    @Autowired(required = false)
    private CamApiService camApiService; // 活动任务

    @Autowired(required = false)
    private CamApiSerService camApiSerService; // 服务活动任务

    @Autowired(required = false)
    private EsService esService;

    @Autowired(required = false)
    private CtgCacheAssetService ctgCacheAssetService;// 销售品过滤方法

    @Autowired(required = false)
    private ICacheProdIndexQryService iCacheProdIndexQryService;

    @Autowired(required = false)
    private ICacheProdEntityQryService iCacheProdEntityQryService;

    @Autowired(required = false)
    private ICacheOfferRelIndexQryService iCacheOfferRelIndexQryService;

    @Autowired(required = false)
    private ICacheRelEntityQryService iCacheRelEntityQryService;

    @Autowired(required = false)
    private ICacheIdMappingEntityQryService iCacheIdMappingEntityQryService;

    @Autowired
    private SpecialService specialService;

    @Autowired
    private EventRedisService eventRedisService;


    @Override
    public Map<String, Object> cpc(Map<String, String> map) {
        //记录开始时间
        long begin = System.currentTimeMillis();
        log.info("事件计算流程开始:" + map.get("eventCode") + "***" + map.get("reqId"));

        //初始化返回结果中的工单信息
        List<Map<String, Object>> activityList = new ArrayList<>();

        //初始化返回结果
        Map<String, Object> result = new HashMap();

        //构造返回结果
        String custId = map.get("custId");
        result.put("reqId", map.get("reqId"));
        result.put("custId", custId);
        result.put("taskList", activityList);
        Map<String, Object> evtContent = (Map<String, Object>) JSON.parse(map.get("evtContent"));
        List<Map<String, Object>> triggersList = new ArrayList<>();
        if (evtContent != null && !evtContent.isEmpty()) {
            for (Map.Entry entry : evtContent.entrySet()) {
                Map<String, Object> trigger = new HashMap<>();
                trigger.put("key", entry.getKey());
                trigger.put("value", entry.getValue());
                triggersList.add(trigger);
            }
            result.put("triggers", triggersList);
        }

        //初始化es log
        JSONObject esJson = new JSONObject();
        esJson.put("reqId", map.get("reqId"));
        //初始化入参出参的es log
        JSONObject paramsJson = new JSONObject();
        paramsJson.put("reqId", map.get("reqId"));
        paramsJson.put("intoParams", map);  //保存入参
        //es log
        esJson.putAll(map);
        //如果没有接入时间  自己补上
        try {
            if (map.get("evtCollectTime") == null || "".equals(map.get("evtCollectTime"))) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                esJson.put("evtCollectTime", simpleDateFormat.format(new Date()));
            } else {
                esJson.put("evtCollectTime", map.get("evtCollectTime"));
            }
        } catch (Exception e) {
            log.error("事件接入时间入参异常:" + map.get("reqId"));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            esJson.put("evtCollectTime", simpleDateFormat.format(new Date()));
        }

        try {
            //事件采集项标签集合(事件采集项标签优先规则)
            Map<String, String> labelItems = new HashMap<>();
            //解析事件采集项
            JSONObject evtParams = JSONObject.parseObject(map.get("evtContent"));
            //获取C4的数据用于过滤
            String c4 = null;
            if (evtParams != null) {
                c4 = (String) evtParams.get("C4");
            }
            // 事件验证
            Map<String, Object> eventResult = eventVerify(result, map, esJson, labelItems, evtParams, begin);

            if (eventResult != null && CommonConstant.DUBBO_FAILURE.equals(eventResult.get("CPCResultCode"))) {
                return result;
            }
            ContactEvt event = (ContactEvt) eventResult.get("event");
            List<Map<String, Object>> evtTriggers = (List<Map<String, Object>>) eventResult.get("evtTriggers");
            Long eventId = event.getContactEvtId();

            //获取事件推荐活动数
            int recCampaignAmount = 0;
            String recCampaignAmountStr = event.getRecCampaignAmount();
            if (recCampaignAmountStr != null && !"".equals(recCampaignAmountStr)) {
                recCampaignAmount = Integer.parseInt(recCampaignAmountStr);
            }

            //添加内置标签
            setInlayLabel(labelItems);

            Map<String, Object> params = new HashMap<>();
            params.put("eventCode", map.get("eventCode"));
            params.put("labelItems", labelItems);
            params.put("inParams", map);

            // 根据事件编码进行特殊业务处理代码
            specialService.deal(params);

            List<Map<String, Object>> resultByEvent = null;
            try {
                //事件下所有活动的规则预校验，返回初步可命中活动
                resultByEvent = getResultByEvent(eventId, map.get("eventCode"), map.get("lanId"), map.get("channelCode"), map.get("reqId"), map.get("accNbr"), c4, map.get("custId"));
            } catch (Exception e) {
                esJson.put("hit", false);
                esJson.put("success", true);
                esJson.put("msg", "预校验返回异常");
                esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));
                e.printStackTrace();
            }

            if (resultByEvent == null || resultByEvent.size() <= 0) {
                log.info("预校验为空");
                long cost = System.currentTimeMillis() - begin;
                esJson.put("timeCost", cost);
                esJson.put("hit", false);
                esJson.put("success", true);
                esJson.put("msg", "活动均未命中");
                esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));

                //事件采集项没有客户编码
                result.put("CPCResultCode", "1000");
                result.put("CPCResultMsg", "succes 预校验为空");
                result.put("taskList", activityList);
                paramsJson.put("backParams", result);
                esHitService.save(paramsJson, IndexList.PARAMS_MODULE, map.get("reqId"));
                log.info("事件计算流程结束:" + map.get("eventCode") + "***" + map.get("reqId") + "（" + (System.currentTimeMillis() - begin) + "）");
                return result;
            }

            //查询事件下使用的所有标签
            DefaultContext<String, Object> context = new DefaultContext<String, Object>();
            Map<String, String> mktAllLabels = new HashMap<>();
            Map<String, Object> eventLabelRedis = eventRedisService.getRedis("EVT_ALL_LABEL_", eventId);
            if (eventLabelRedis != null) {
                mktAllLabels = (Map<String, String>) eventLabelRedis.get("EVT_ALL_LABEL_" + eventId);
                if (mktAllLabels == null) {
                    esJson.put("hit", false);
                    esJson.put("msg", "获取事件下所有标签异常");
                    esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));
                    return Collections.EMPTY_MAP;
                }
            }
            // 过滤事件采集项中的标签
            Map<String, String> mktAllLabel = filterLabel(labelItems, mktAllLabels);

            //判断有没有客户级活动
            Boolean hasCust = false;  //是否有客户级
            Boolean hasPackage = false;  //是否有套餐
            Boolean successCust = false;  //客户级查询是否成功
            Boolean successPackage = false;  //套餐级查询是否成功
            for (Map<String, Object> activeMap : resultByEvent) {
                if (((Map) activeMap.get("mktCampaignMap")) != null && !((Map) activeMap.get("mktCampaignMap")).isEmpty()) {
                    if ((Integer) ((Map<String, Object>) activeMap.get("mktCampaignMap")).get("levelConfig") == 1) {  // 客户级
                        hasCust = true;
                    } else if ((Integer) ((Map<String, Object>) activeMap.get("mktCampaignMap")).get("levelConfig") == 2) { // 套餐级
                        hasPackage = true;
                    }
                }
            }

            List<DefaultContext<String, Object>> resultMapList = new ArrayList<>();
            List<Map<String, Object>> accNbrMapList = new ArrayList<>();
            JSONArray accArray = new JSONArray();
            // 是客户级的
            if (hasCust || hasPackage) {
                if (custId == null || "".equals(custId)) {
                    //保存es log
                    long cost = System.currentTimeMillis() - begin;
                    esJson.put("timeCost", cost);
                    esJson.put("hit", false);
                    esJson.put("success", true);
                    esJson.put("msg", "客户级活动，事件采集项未包含客户编码");
                    esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));

                    log.error("采集项未包含客户编码:" + map.get("reqId"));

                    //事件采集项没有客户编码
                    result.put("CPCResultCode", "1000");
                    result.put("CPCResultMsg", "采集项未包含客户编码");
                    return result;

                } else if (hasCust) {
                    JSONObject param = new JSONObject();
                    //查询标识
                    param.put("c3", map.get("lanId"));
                    param.put("queryId", map.get("custId"));
                    param.put("queryNum", "");
                    param.put("queryFields", "");
                    param.put("type", "4");
                    param.put("centerType", "00");
                    try {
                        Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(param));
                        if ("0".equals(dubboResult.get("result_code").toString())) {
                            accArray = new JSONArray((List<Object>) dubboResult.get("msgbody"));
                            successCust = true;
                        }

                        // 查询客户级的标签
                        Map<String, String> privateParams = new HashMap<>();
                        privateParams.put("isCust", "1");
                        privateParams.put("accNbr", map.get("accNbr"));
                        privateParams.put("integrationId", map.get("integrationId"));
                        privateParams.put("custId", map.get("custId"));

                        // 客户级
                        List<Future<DefaultContext<String, Object>>> futureList = new ArrayList<>();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("mktAllLabel", mktAllLabel);
                        hashMap.put("map", map);
                        hashMap.put("context", context);
                        hashMap.put("esJson", esJson);
                        hashMap.put("esHitService", esHitService);
                        hashMap.put("yzServ", yzServ);
                        //多线程获取资产级标签，并加上客户级标签
                        for (Object o : accArray) {
                            hashMap.put("o", o);
                            Future<DefaultContext<String, Object>> future = ThreadPool.submit(new ListMapLabelTaskServiceImpl(hashMap));
                            futureList.add(future);
                        }

                        for (Future<DefaultContext<String, Object>> future : futureList) {
                            if (future.get() != null && !future.get().isEmpty()) {
                                DefaultContext<String, Object> reultMap = future.get();
                                resultMapList.add(reultMap);
                            }
                        }

                    } catch (Exception e) {
                        log.error("Exception = " + e);
                    }
                    // 判断是否存在套餐级
                    if (hasPackage) {
                        accNbrMapList = getAccNbrList(map.get("accNbr"));
                        successPackage = true;
                    }
                } else if (hasPackage) {
                    accNbrMapList = getAccNbrList(map.get("accNbr"));
                    successPackage = true;
                    // 查询标签
                    try {
                        // 客户级
                        List<Future<DefaultContext<String, Object>>> futureList = new ArrayList<>();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("mktAllLabel", mktAllLabel);
                        hashMap.put("map", map);
                        hashMap.put("context", context);
                        hashMap.put("esJson", esJson);
                        hashMap.put("labelItems", labelItems);
                        hashMap.put("esHitService", esHitService);
                        hashMap.put("yzServ", yzServ);
                        //多线程获取资产级标签，并加上客户级标签
                        for (Object o : accNbrMapList) {
                            hashMap.put("o", o);
                            Future<DefaultContext<String, Object>> future = ThreadPool.submit(new ListMapLabelTaskServiceImpl(hashMap));
                            futureList.add(future);
                        }

                        for (Future<DefaultContext<String, Object>> future : futureList) {
                            if (future.get() != null && !future.get().isEmpty()) {
                                DefaultContext<String, Object> reultMap = future.get();
                                resultMapList.add(reultMap);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                //资产级
                Map<String, String> privateParams = new HashMap<>();
                privateParams.put("isCust", "1"); //资产级
                privateParams.put("accNbr", map.get("accNbr"));
                privateParams.put("integrationId", map.get("integrationId"));// 资产集成编码
                privateParams.put("custId", map.get("custId"));

                DefaultContext<String, Object> reultMap = new DefaultContext<>();
                Map<String, Object> assetLabelMap = getAssetAndPromLabel(mktAllLabel, map, privateParams, context, esJson, labelItems);
                if (assetLabelMap != null) {
                    reultMap.putAll(assetLabelMap);
                } else {
                    return null;
                }
                resultMapList.add(reultMap);
            }


            //初始化结果集
            List<Future<Map<String, Object>>> threadList = new ArrayList<>();
            // 启动线程走清单流程
            List<String> mktCampaginIdList = new ArrayList<>();
            List<String> initIdList = new ArrayList<>();
            for (Map<String, Object> activeMap : resultByEvent) {
                if (activeMap.get("mktCampaignCustMap") != null && !((Map<String, Object>) activeMap.get("mktCampaignCustMap")).isEmpty()) {
                    mktCampaginIdList.add(((Map<String, Object>) activeMap.get("mktCampaignCustMap")).get("mktCampaginId").toString());
                    initIdList.add(((Map<String, Object>) activeMap.get("mktCampaignCustMap")).get("initId").toString());
                }
            }
            if (mktCampaginIdList != null && mktCampaginIdList.size() > 0) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("mktCampaginIdList", mktCampaginIdList);
                hashMap.put("initIdList", initIdList);
                hashMap.put("eventId", eventId);
                String lanId = map.get("lanId");
                hashMap.put("lanId", lanId);
                hashMap.put("custId", custId);
                hashMap.put("map", map);
                hashMap.put("evtTriggers", evtTriggers);
                hashMap.put("yzServ", yzServ);
                hashMap.put("mktCamEvtRelMapper", mktCamEvtRelMapper);
                hashMap.put("esService", esService);
                hashMap.put("ctgCacheAssetService", ctgCacheAssetService);
                hashMap.put("iCacheProdIndexQryService", iCacheProdIndexQryService);
                hashMap.put("iCacheProdEntityQryService", iCacheProdEntityQryService);
                hashMap.put("iCacheOfferRelIndexQryService", iCacheOfferRelIndexQryService);
                hashMap.put("iCacheRelEntityQryService", iCacheRelEntityQryService);
                hashMap.put("iCacheIdMappingEntityQryService", iCacheIdMappingEntityQryService);
                hashMap.put("eventRedisService", eventRedisService);
                Future<Map<String, Object>> f = ThreadPool.submit(new CustListTaskServiceImpl(hashMap));
                threadList.add(f);
            }

            //判断是否全部为资产级
            boolean isAllAsset = false;
            for (Map<String, Object> activeMap : resultByEvent) {
                if (((Map<String, Object>) activeMap.get("mktCampaignMap")) != null && !((Map<String, Object>) activeMap.get("mktCampaignMap")).isEmpty()) {
                    if ((Integer) ((Map<String, Object>) activeMap.get("mktCampaignMap")).get("levelConfig") == 1) { // 1为客户级
                        isAllAsset = false;
                        break;
                    } else if ((Integer) ((Map<String, Object>) activeMap.get("mktCampaignMap")).get("levelConfig") == 0) {  // 0为资产级
                        isAllAsset = true;
                    }
                }
            }
            //ActivityTaskServiceImpl 线程参数 拼接
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("map", map);
            hashMap.put("labelItems", labelItems);
            hashMap.put("evtTriggers", evtTriggers);
            hashMap.put("camApiService", camApiService);
            hashMap.put("camApiSerService", camApiSerService);

            // 全部为资产级时直接遍历活动
            if (isAllAsset) {
                for (Map<String, Object> resultMap : resultByEvent) {
                    //资产级
                    Map<String, Object> activeMap = (Map<String, Object>) resultMap.get("mktCampaignMap");
                    if (activeMap != null && !activeMap.isEmpty()) {
                        Map<String, String> privateParams = new HashMap<>();
                        privateParams.put("isCust", "1"); //是否是客户级
                        privateParams.put("accNbr", map.get("accNbr"));
                        privateParams.put("integrationId", map.get("integrationId"));
                        privateParams.put("custId", map.get("custId"));
                        privateParams.put("orderPriority", activeMap.get("campaignSeq") == null ? "0" : activeMap.get("campaignSeq").toString());
                        //资产级
                        Long mktCampaginId = (Long) activeMap.get("mktCampaginId");
                        String type = (String) activeMap.get("type");
                        List<Map<String, Object>> strategyMapList = (List<Map<String, Object>>) activeMap.get("strategyMapList");
                        DefaultContext<String, Object> defaultContext = new DefaultContext<>();
                        if (resultMapList != null && resultMapList.size() > 0) {
                            defaultContext = resultMapList.get(0);
                        }
                        //线程参数
                        hashMap.put("mktCampaginId", mktCampaginId);
                        hashMap.put("type", type);
                        hashMap.put("privateParams", privateParams);
                        hashMap.put("strategyMapList", strategyMapList);
                        hashMap.put("defaultContext", defaultContext);
                        Future<Map<String, Object>> f = ThreadPool.submit(new ActivityTaskServiceImpl(hashMap));
                        //将线程处理结果添加到结果集
                        threadList.add(f);
                    }
                }
            } else {
                //遍历活动
                for (Map<String, Object> resultMap : resultByEvent) {
                    //提交线程
                    Map<String, Object> activeMap = (Map<String, Object>) resultMap.get("mktCampaignMap");
                    if (activeMap != null && !activeMap.isEmpty()) {
                        if ((Integer) activeMap.get("levelConfig") == 1) { //判断是客户级
                            //客户级
                            if (successCust) {
                                for (DefaultContext<String, Object> o : resultMapList) {
                                    //客户级下，循环资产级
                                    Map<String, String> privateParams = new HashMap<>();
                                    privateParams.put("isCust", "0"); //是客户级
                                    privateParams.put("accNbr", o.get("accNbr").toString());
                                    privateParams.put("integrationId", o.get("integrationId").toString());
                                    privateParams.put("custId", map.get("custId"));
                                    //活动优先级为空的时候默认0
                                    privateParams.put("orderPriority", activeMap.get("campaignSeq") == null ? "0" : activeMap.get("campaignSeq").toString());

                                    Long mktCampaginId = (Long) activeMap.get("mktCampaginId");
                                    String type = (String) activeMap.get("type");
                                    List<Map<String, Object>> strategyMapList = (List<Map<String, Object>>) activeMap.get("strategyMapList");
                                    //线程参数
                                    hashMap.put("mktCampaginId", mktCampaginId);
                                    hashMap.put("type", type);
                                    hashMap.put("privateParams", privateParams);
                                    hashMap.put("strategyMapList", strategyMapList);
                                    hashMap.put("defaultContext", o);
                                    Future<Map<String, Object>> f = ThreadPool.submit(new ActivityTaskServiceImpl(hashMap));
                                    //将线程处理结果添加到结果集
                                    threadList.add(f);
                                }
                            } else {
                                log.error("客户级资产查询出错:" + map.get("reqId"));
                                esJson.put("reqId", map.get("reqId"));
                                esJson.put("activityId", activeMap.get("mktCampaginId"));
                                esJson.put("hitEntity", map.get("accNbr")); //命中对象
                                esJson.put("hit", false);
                                esJson.put("msg", "客户级资产查询出错");
                                esHitService.save(esJson, IndexList.ACTIVITY_MODULE, map.get("reqId") + activeMap.get("mktCampaginId") + map.get("accNbr"));
                            }
                        } else if ((Integer) activeMap.get("levelConfig") == 2) { // 判断是套餐级别
                            //套餐级
                            if (successPackage) {
                                for (DefaultContext<String, Object> o : resultMapList) {
                                    for (Map<String, Object> accNbrMap : accNbrMapList) {
                                        if (o.get("accNbr").toString().equals(accNbrMap.get("ACC_NBR"))) {
                                            //客户级下，循环资产级
                                            Map<String, String> privateParams = new HashMap<>();
                                            privateParams.put("isCust", "0"); //是客户级
                                            privateParams.put("accNbr", o.get("accNbr").toString());
                                            privateParams.put("integrationId", o.get("integrationId").toString());
                                            privateParams.put("custId", map.get("custId"));
                                            //活动优先级为空的时候默认0
                                            privateParams.put("orderPriority", activeMap.get("campaignSeq") == null ? "0" : activeMap.get("campaignSeq").toString());
                                            //线程参数
                                            Long mktCampaginId = (Long) activeMap.get("mktCampaginId");
                                            String type = (String) activeMap.get("type");
                                            List<Map<String, Object>> strategyMapList = (List<Map<String, Object>>) activeMap.get("strategyMapList");
                                            //线程参数
                                            hashMap.put("mktCampaginId", mktCampaginId);
                                            hashMap.put("type", type);
                                            hashMap.put("privateParams", privateParams);
                                            hashMap.put("strategyMapList", strategyMapList);
                                            hashMap.put("defaultContext", o);
                                            Future<Map<String, Object>> f = ThreadPool.submit(new ActivityTaskServiceImpl(hashMap));
                                            //将线程处理结果添加到结果集
                                            threadList.add(f);
                                        }
                                    }
                                }
                            } else {
                                log.error("套餐级资产查询出错:" + map.get("reqId"));
                                esJson.put("reqId", map.get("reqId"));
                                esJson.put("activityId", activeMap.get("mktCampaginId"));
                                esJson.put("hitEntity", map.get("accNbr"));  //命中对象
                                esJson.put("hit", false);
                                esJson.put("msg", "客户级资产查询出错");
                                esHitService.save(esJson, IndexList.ACTIVITY_MODULE, map.get("reqId") + activeMap.get("mktCampaginId") + map.get("accNbr"));

                            }
                        } else {
                            //资产级
                            for (DefaultContext<String, Object> o : resultMapList) {
                                String assetId = o.get("integrationId").toString();
                                // 判断资产编码是否与接入的一致
                                if (assetId.equals(map.get("integrationId"))) {
                                    Map<String, String> privateParams = new HashMap<>();
                                    privateParams.put("isCust", "1"); //是否是客户级
                                    privateParams.put("accNbr", map.get("accNbr"));
                                    privateParams.put("integrationId", map.get("integrationId"));
                                    privateParams.put("custId", map.get("custId"));
                                    privateParams.put("orderPriority", activeMap.get("campaignSeq") == null ? "0" : activeMap.get("campaignSeq").toString());
                                    //线程参数
                                    Long mktCampaginId = (Long) activeMap.get("mktCampaginId");
                                    String type = (String) activeMap.get("type");
                                    List<Map<String, Object>> strategyMapList = (List<Map<String, Object>>) activeMap.get("strategyMapList");
                                    //线程参数
                                    hashMap.put("mktCampaginId", mktCampaginId);
                                    hashMap.put("type", type);
                                    hashMap.put("privateParams", privateParams);
                                    hashMap.put("strategyMapList", strategyMapList);
                                    hashMap.put("defaultContext", o);
                                    //资产级
                                    Future<Map<String, Object>> f = ThreadPool.submit(new ActivityTaskServiceImpl(hashMap));
                                    //将线程处理结果添加到结果集
                                    threadList.add(f);
                                }
                            }
                        }
                    }
                }
            }


            //获取结果
            try {
                Map<String, Object> nonPassedMsg = new HashMap<>();
                for (Future<Map<String, Object>> future : threadList) {
                    Boolean flag = true;
                    if (future.get() != null && !future.get().isEmpty()) {
                        Map<String, Object> map1 = future.get();
                        for (String s : map1.keySet()) {
                            if (s.contains("cam_")) {
                                flag = false;
                                nonPassedMsg.put(s, map1.get(s));
                            }
                        }
                        if (map1.get("nonPassedMsg") != null) {
                            Object nonPassedMsg1 = map1.get("nonPassedMsg");
                            nonPassedMsg.putAll((Map<String, Object>) nonPassedMsg1);
                            // flag = false;
                        }
                        if (flag) {
                            // 命中活动
                            if (future.get() != null && future.get().get("ruleList") != null) {
                                activityList.addAll((List<Map<String, Object>>) (future.get().get("ruleList")));
                            }
                        }
                    }
                }
                result.put("nonPassedMsg", JSON.toJSONString(nonPassedMsg));
            } catch (ExecutionException e) {
                e.printStackTrace();
                return Collections.EMPTY_MAP;
            }

            // 判断事件推荐活动数，按照优先级排序
            if (activityList.size() > 0 && recCampaignAmount > 0 && recCampaignAmount < activityList.size()) {
                Collections.sort(activityList, new Comparator<Map<String, Object>>() {
                    public int compare(Map o1, Map o2) {
                        if (!o1.containsKey("orderPriority")) {
                            o1.put("orderPriority", "0");
                        }
                        if (!o2.containsKey("orderPriority")) {
                            o2.put("orderPriority", "0");
                        }
                        return Integer.parseInt((String) o2.get("orderPriority")) - Integer.parseInt((String) o1.get("orderPriority"));
                    }
                });

                String orderPriorityLast = (String) activityList.get(recCampaignAmount - 1).get("orderPriority");

                for (int i = recCampaignAmount; i < activityList.size(); i++) {
                    if (!orderPriorityLast.equals(activityList.get(i).get("orderPriority"))) {
                        //es log
                        esJson.put("msg", "推荐数：" + i + ",命中数：" + activityList.size());
                        //事件推荐活动数
                        activityList = activityList.subList(0, i);
                    }
                }
            }

            //返回结果
            result.put("taskList", activityList); //协同回调结果

            if (activityList.size() > 0) {
                //构造返回参数
                result.put("CPCResultCode", CommonConstant.DUBBO_SUCCESS);
                result.put("CPCResultMsg", "success");

                StringBuilder actStr = new StringBuilder();
                for (Map<String, Object> actMap : activityList) {
                    actStr.append(actMap.get("activityId")).append(",");
                }
                esJson.put("hit", true);
                esJson.put("hitDetail", actStr.toString());
            } else {
                result.put("CPCResultCode", CommonConstant.DUBBO_FAILURE);
                result.put("CPCResultMsg", "success");
                esJson.put("hit", false);
            }
            result.put("reqId", map.get("reqId"));
            result.put("custId", custId);
            paramsJson.put("backParams", result);
            esHitService.save(paramsJson, IndexList.PARAMS_MODULE, map.get("reqId"));

            //es log
            long cost = System.currentTimeMillis() - begin;
            esJson.put("timeCost", cost);
            esJson.put("success", true);
            esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));
        } catch (Exception e) {
            log.info("策略中心计算异常");
            log.error("Exception = ", e);
            esJson.put("errorMsg", e.getMessage());
            esHitService.save(paramsJson, IndexList.PARAMS_MODULE, map.get("reqId"));

            long cost = System.currentTimeMillis() - begin;
            esJson.put("timeCost", cost);
            esJson.put("hit", false);
            esJson.put("success", true);
            esJson.put("msg", "策略中心计算异常" + e.getMessage() + e.toString());
            esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));

            //构造返回参数
            result.put("CPCResultCode", "1000");
            result.put("CPCResultMsg", "策略中心计算异常" + e.getMessage() + e.toString());
            result.put("reqId", map.get("reqId"));
            result.put("custId", custId);
            return result;
        }
        log.info("事件计算流程结束:" + map.get("eventCode") + "***" + map.get("reqId") + "（" + (System.currentTimeMillis() - begin) + "）");
        return result;
    }


    /**
     * 过滤事件采集项中的标签
     *
     * @param labelItems  事件采集项标签集合
     * @param mktAllLabels  事件下所有的标签
     * @return
     */
    private Map<String, String> filterLabel(Map<String, String> labelItems, Map<String, String> mktAllLabels) {
        Map<String, String> mktAllLabel = new HashMap<>();
        Iterator<Map.Entry<String, String>> iterator = labelItems.entrySet().iterator();
        List<String> assetLabelList = new ArrayList<>();
        List<String> promLabelList = new ArrayList<>();
        List<String> custLabelList = new ArrayList<>();
        if (mktAllLabels.get("assetLabels") != null && !"".equals(mktAllLabels.get("assetLabels"))) {
            assetLabelList = ChannelUtil.StringToList(mktAllLabels.get("assetLabels"));
            // ASSI_PROM_INTEG_ID标签
            assetLabelList.add("ASSI_PROM_INTEG_ID");
        }
        if (mktAllLabels.get("promLabels") != null && !"".equals(mktAllLabels.get("promLabels"))) {
            promLabelList = ChannelUtil.StringToList(mktAllLabels.get("promLabels"));
        }
        if (mktAllLabels.get("custLabels") != null && !"".equals(mktAllLabels.get("custLabels"))) {
            custLabelList = ChannelUtil.StringToList(mktAllLabels.get("custLabels"));
        }

        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (assetLabelList.contains(entry.getKey())) {
                assetLabelList.remove(entry.getKey());
            } else if (promLabelList.contains(entry.getKey())) {
                promLabelList.remove(entry.getKey());
            } else if (custLabelList.contains(entry.getKey())) {
                custLabelList.remove(entry.getKey());
            }
        }

        List<String> labelList = new ArrayList<>();
        labelList.addAll(assetLabelList);
        labelList.addAll(promLabelList);
        labelList.addAll(custLabelList);
        // 添加落地网格AREA_ID标签
        labelList.add("AREA_ID");

        if (assetLabelList != null && assetLabelList.size() > 0) {
            mktAllLabel.put("assetLabels", ChannelUtil.StringList2String(labelList));
        }
        return mktAllLabel;
    }


    /**
     * todo 以下方法 可抽 业务工具方法
     * 添加内置的标签并赋值
     *
     * @param map
     */
    private void setInlayLabel(Map<String, String> map) {
        Calendar calendar = Calendar.getInstance();
        map.put("CPCP_IN_EVENT_YEAR", String.valueOf(calendar.get(Calendar.YEAR)));
        map.put("CPCP_IN_EVENT_MONTH", String.valueOf(calendar.get(Calendar.MONTH) + 1));
        map.put("CPCP_IN_EVENT_DAY", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
    }


    /**
     * 判断是否存在套餐级
     *
     * @param accNbr 资产号码
     * @return
     */
    private List<Map<String, Object>> getAccNbrList(String accNbr) {
        List<String> accNbrList = new ArrayList<>();
        List<Map<String, Object>> accNbrMapList = new ArrayList<>();
        // 根据accNum查询prodInstId
        CacheResultObject<Set<String>> prodInstIdsObject = iCacheProdIndexQryService.qryProdInstIndex2(accNbr);
        if (prodInstIdsObject != null && prodInstIdsObject.getResultObject() != null) {
            Long mainOfferInstId = null;
            Set<String> prodInstIds = prodInstIdsObject.getResultObject();
            for (String prodInstId : prodInstIds) {
                // 查询产品实例实体缓存 取主产品（1000）的一个
                CacheResultObject<ProdInst> prodInstCacheEntity = iCacheProdEntityQryService.getProdInstCacheEntity(prodInstId);
                if (prodInstCacheEntity != null && prodInstCacheEntity.getResultObject() != null && "1000".equals(prodInstCacheEntity.getResultObject().getProdUseType())) {
                    mainOfferInstId = prodInstCacheEntity.getResultObject().getMainOfferInstId();
                    break;
                }
            }
            // 根据offerInstId和statusCd查询offerProdInstRelId
            if (mainOfferInstId != null) {
                CacheResultObject<Set<String>> setCacheResultObject = iCacheOfferRelIndexQryService.qryOfferProdInstRelIndex1(mainOfferInstId.toString(), "1000");
                if (setCacheResultObject != null && setCacheResultObject.getResultObject() != null && setCacheResultObject.getResultObject().size() > 0) {
                    Set<String> offerProdInstRelIds = setCacheResultObject.getResultObject();
                    for (String offerProdInstRelId : offerProdInstRelIds) {
                        CacheResultObject<OfferProdInstRel> offerProdInstRelCacheEntity = iCacheRelEntityQryService.getOfferProdInstRelCacheEntity(offerProdInstRelId);
                        if (offerProdInstRelCacheEntity != null && offerProdInstRelCacheEntity.getResultObject() != null) {
                            Long prodInstIdNew = offerProdInstRelCacheEntity.getResultObject().getProdInstId();
                            CacheResultObject<ProdInst> prodInstCacheEntityNew = iCacheProdEntityQryService.getProdInstCacheEntity(prodInstIdNew.toString());
                            if (prodInstCacheEntityNew != null && prodInstCacheEntityNew.getResultObject() != null) {
                                final CacheResultObject<RowIdMapping> prodInstIdMappingCacheEntity = iCacheIdMappingEntityQryService.getProdInstIdMappingCacheEntity(prodInstIdNew.toString());
                                if (prodInstIdMappingCacheEntity != null && prodInstIdMappingCacheEntity.getResultObject() != null) {
                                    Map<String, Object> accNbrMap = new HashMap<>();
                                    accNbrMap.put("ACC_NBR", prodInstCacheEntityNew.getResultObject().getAccNum());
                                    accNbrMap.put("ASSET_INTEG_ID", prodInstIdMappingCacheEntity.getResultObject().getCrmRowId());
                                    accNbrMapList.add(accNbrMap);
                                }
                            }
                        }
                    }
                }
            }
        }
        log.info("10101010------accNbrMapList --->" + JSON.toJSONString(accNbrMapList));
        return accNbrMapList;
    }

    /**
     * 事件下活动校验
     *
     * @param eventId
     * @param lanId
     * @param channel
     * @return
     */
    private List<Map<String, Object>> getResultByEvent(Long eventId, String eventCode, String lanId, String channel, String reqId, String accNbr, String c4, String custId) {
        Map<String, Object> redis = eventRedisService.getRedis("CAM_IDS_EVT_REL_", eventId);
        List<Map<String, Object>> mktCampaginIdList = new ArrayList<>();
        if (redis != null) {
            mktCampaginIdList = (List<Map<String, Object>>) redis.get("CAM_IDS_EVT_REL_" + eventId);

        }
        List<Future<Map<String, Object>>> futureList = new ArrayList<>();
        List<Map<String, Object>> resultMapList = new ArrayList<>();
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("lanId", lanId);
            hashMap.put("channel", channel);
            hashMap.put("reqId", reqId);
            hashMap.put("accNbr", accNbr);
            hashMap.put("c4", c4);
            hashMap.put("custId", custId);
            hashMap.put("eventRedisService", eventRedisService);
            hashMap.put("yzServ", yzServ);
            hashMap.put("esHitService", esHitService);

            for (Map<String, Object> act : mktCampaginIdList) {
                act.put("eventCode", eventCode);
                hashMap.put("act", act);
                Future<Map<String, Object>> future = ThreadPool.submit(new ListResultByEventTaskServiceImpl(hashMap));
                futureList.add(future);
            }
            if (futureList != null && futureList.size() > 0) {
                for (Future<Map<String, Object>> future : futureList) {
                    try {
                        Map<String, Object> resultMap = future.get();
                        if (resultMap != null && !resultMap.isEmpty()) {
                            resultMapList.add(resultMap);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            log.error("[op:getResultByEvent] failed to getResultByEvent by eventId = {}, lanId = {}, channel = {}, Expection = ", eventId, lanId, channel, e);
        }
        return resultMapList;
    }

    // 处理资产级标签和销售品级标签
    private DefaultContext<String, Object> getAssetAndPromLabel(Map<String, String> mktAllLabel, Map<String, String> params, Map<String, String> privateParams, DefaultContext<String, Object> context, JSONObject esJson, Map<String, String> labelItems) {
        //资产级标签
        DefaultContext<String, Object> contextNew = new DefaultContext<String, Object>();
        if (mktAllLabel.get("assetLabels") != null && !"".equals(mktAllLabel.get("assetLabels"))) {
            JSONObject assParam = new JSONObject();
            assParam.put("queryNum", privateParams.get("accNbr"));
            assParam.put("c3", params.get("lanId"));
            assParam.put("queryId", privateParams.get("integrationId"));
            assParam.put("type", "1");
            assParam.put("queryFields", mktAllLabel.get("assetLabels"));
            assParam.put("centerType", "00");

            //因子查询-----------------------------------------------------
            Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(assParam));
            if ("0".equals(dubboResult.get("result_code").toString())) {
                JSONObject body = new JSONObject((HashMap) dubboResult.get("msgbody"));
                //ES log 标签实例
                //拼接规则引擎上下文
                for (Map.Entry<String, Object> entry : body.entrySet()) {
                    //添加到上下文
                    contextNew.put(entry.getKey(), entry.getValue());
                }
            } else {
                log.info("查询资产标签失败");
                esJson.put("hit", "false");
                esJson.put("msg", "查询资产标签失败");
                esHitService.save(esJson, IndexList.EVENT_MODULE, params.get("reqId"));
                return null;
            }
        }

        contextNew.putAll(labelItems);   //添加事件采集项中作为标签使用的实例
        contextNew.putAll(context);      // 客户级标签
        contextNew.put("integrationId", privateParams.get("integrationId"));
        contextNew.put("accNbr", privateParams.get("accNbr"));
        return contextNew;
    }


    /**
     * 事件验证
     *
     * @param result     结果
     * @param map        入参map
     * @param esJson     日志
     * @param labelItems 采集项标签集合
     * @param evtParams  事件采集项
     * @param begin      开始时间
     * @return
     */
    private Map<String, Object> eventVerify(Map<String, Object> result, Map<String, String> map, JSONObject esJson, Map<String, String> labelItems, JSONObject evtParams, long begin) {
        //事件验证开始↓↓↓↓↓↓↓↓↓↓↓↓↓l
        Map<String, Object> eventResult = new HashMap<>();
        //根据事件code查询事件信息
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("eventCode", map.get("eventCode"));
        Map<String, Object> eventRedis = eventRedisService.getRedis("EVENT_", paramMap);
        ContactEvt event = new ContactEvt();
        if (eventRedis != null) {
            event = (ContactEvt) eventRedis.get("EVENT_" + map.get("eventCode"));
        }
        if (event == null) {
            esJson.put("hit", false);
            esJson.put("success", true);
            esJson.put("msg", "未找到相关事件");
            esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));
            log.error("未找到相关事件:" + map.get("reqId"));
            result.put("CPCResultCode", "1000");
            result.put("CPCResultMsg", "未找到相关事件");
            eventResult.putAll(result);
            return eventResult;
        }
        //获取事件id
        Long eventId = event.getContactEvtId();
        esJson.put("eventId", eventId);
        //验证事件状态
        if (!"1000".equals(event.getStatusCd())) {
            esJson.put("hit", false);
            esJson.put("success", true);
            esJson.put("msg", "事件已关闭");
            esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));
            log.info("事件已关闭:" + map.get("reqId"));
            result.put("CPCResultCode", "1000");
            result.put("CPCResultMsg", "事件已关闭");
            eventResult.putAll(result);
            return eventResult;
        }
        //验证事件渠道
        Map<String, Object> eventChannelMap = eventChannelVerify(result, map, esJson);
        if (eventChannelMap != null) {
            if (CommonConstant.DUBBO_FAILURE.equals(eventChannelMap.get("CPCResultCode"))) {
                return eventChannelMap;
            }
        }

        //验证事件采集项
        List<Map<String, Object>> evtTriggers = new ArrayList<>();
        Map<String, Object> eventItemsMap = eventItemsVerify(result, eventId, evtParams, labelItems, evtTriggers, begin, esJson, map);
        if (eventItemsMap != null) {
            if (CommonConstant.DUBBO_FAILURE.equals(eventItemsMap.get("CPCResultCode"))) {
                return eventItemsMap;
            }
        }
        eventResult.put("event", event);
        eventResult.put("evtTriggers", evtTriggers);
        return eventResult;

        //!!!验证事件规则命中 todo 12.23 无用修改为注释
//                Map<String, Object> stringObjectMap = matchRulCondition(eventId, labelItems, map);
//                if (!stringObjectMap.get("code").equals("success")) {
//
//                    log.error("事件规则未命中:" + map.get("reqId"));
//
//                    //判断不符合条件 直接返回不命中
//                    result.put("CPCResultMsg", stringObjectMap.get("result"));
//                    result.put("CPCResultCode", "1000");
//                    esJson.put("hit", false);
//                    esJson.put("success", true);
//                    esJson.put("msg", stringObjectMap.get("result"));
//                    esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));
//                    return result;
//                }
    }

    /**
     * 事件渠道验证
     *
     * @param map
     * @param esJson
     * @return
     */
    private Map<String, Object> eventChannelVerify(Map<String, Object> result, Map<String, String> map, JSONObject esJson) {
        try {
            List<String> list = new ArrayList<>();
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("eventCode", map.get("eventCode"));
            Map<String, Object> channelCodeRedis = eventRedisService.getRedis("CHANNEL_CODE_LIST_", paramMap);
            if (channelCodeRedis != null) {
                list = (List<String>) channelCodeRedis.get("CHANNEL_CODE_LIST_" + map.get("eventCode"));
            }
            if (list.isEmpty() || !list.contains(map.get("channelCode"))) {
                esJson.put("hit", false);
                esJson.put("success", true);
                esJson.put("msg", "接入渠道不符");
                esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));
                log.info("接入渠道不符:" + map.get("reqId"));
                result.put("CPCResultCode", "1000");
                result.put("CPCResultMsg", "接入渠道不符");
                return result;
            }
        } catch (Exception e) {
            esJson.put("hit", false);
            esJson.put("success", true);
            esJson.put("msg", "接入渠道查询异常");
            esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));
            e.printStackTrace();
            return result;
        }
        return result;
    }


    /**
     * 事件采集项验证
     *
     * @param eventId     事件Id
     * @param evtParams   事件采集项
     * @param labelItems  采集项标签集合
     * @param evtTriggers 事件采集项
     * @param begin       开始时间
     * @param esJson      日志
     * @param map
     * @return
     */
    private Map<String, Object> eventItemsVerify(Map<String, Object> result, Long eventId, JSONObject evtParams, Map<String, String> labelItems, List<Map<String, Object>> evtTriggers, Long begin, JSONObject esJson, Map<String, String> map) {
        List<EventItem> contactEvtItems = new ArrayList<>();
        Map<String, Object> evtItemsRedis = eventRedisService.getRedis("EVENT_ITEM_", eventId);
        if (evtItemsRedis != null) {
            contactEvtItems = (List<EventItem>) evtItemsRedis.get("EVENT_ITEM_" + eventId);
        }
        Map<String, Object> trigger;
        StringBuilder stringBuilder = new StringBuilder();
        for (EventItem contactEvtItem : contactEvtItems) {
            if (evtParams != null && evtParams.containsKey(contactEvtItem.getEvtItemCode())) {
                //筛选出作为标签使用的事件采集项
                if ("0".equals(contactEvtItem.getIsLabel())) {
                    labelItems.put(contactEvtItem.getEvtItemCode(), evtParams.getString(contactEvtItem.getEvtItemCode()));
                }
                trigger = new HashMap<>();
                trigger.put("key", contactEvtItem.getEvtItemCode());
                trigger.put("value", evtParams.get(contactEvtItem.getEvtItemCode()));
                evtTriggers.add(trigger);
            } else {
                //记录缺少的事件采集项
                stringBuilder.append(contactEvtItem.getEvtItemCode()).append("、");
            }
        }

        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        //事件采集项返回参数
        if (stringBuilder.length() > 0) {
            //保存es log
            long cost = System.currentTimeMillis() - begin;
            esJson.put("timeCost", cost);
            esJson.put("hit", false);
            esJson.put("success", true);
            esJson.put("msg", "事件采集项验证失败，缺少：" + stringBuilder.toString());
            esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));
            log.error(map.get("reqId") + "事件采集项验证失败:" + map.get("reqId"));
            result.put("CPCResultCode", "1000");
            result.put("CPCResultMsg", "事件采集项验证失败，缺少：" + stringBuilder.toString());
            return result;
        }
        return result;
    }

}
