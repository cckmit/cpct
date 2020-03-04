package com.zjtelcom.cpct.dubbo.dubboThreadPool.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ctzj.biz.asset.model.dto.AssetDto;
import com.ctzj.biz.asset.model.dto.AssetPromDto;
import com.ctzj.bss.customer.data.carrier.outbound.api.CtgCacheAssetService;
import com.ctzj.bss.customer.data.carrier.outbound.model.ResponseResult;
import com.ctzj.smt.bss.cache.service.api.CacheEntityApi.ICacheIdMappingEntityQryService;
import com.ctzj.smt.bss.cache.service.api.CacheEntityApi.ICacheProdEntityQryService;
import com.ctzj.smt.bss.cache.service.api.CacheEntityApi.ICacheRelEntityQryService;
import com.ctzj.smt.bss.cache.service.api.CacheIndexApi.ICacheOfferRelIndexQryService;
import com.ctzj.smt.bss.cache.service.api.CacheIndexApi.ICacheProdIndexQryService;
import com.ctzj.smt.bss.cache.service.api.model.CacheResultObject;
import com.ctzj.smt.bss.customer.model.dataobject.OfferProdInstRel;
import com.ctzj.smt.bss.customer.model.dataobject.ProdInst;
import com.ctzj.smt.bss.customer.model.dataobject.RowIdMapping;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.dao.campaign.MktCamEvtRelMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.dto.campaign.MktCamEvtRel;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.enums.AreaNameEnum;
import com.zjtelcom.cpct.service.event.EventRedisService;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.es.es.service.EsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;

@Service
@Transactional
public class CustListTaskServiceImpl implements Callable {

    private static final Logger log = LoggerFactory.getLogger(ListMapLabelTaskServiceImpl.class);

    @Autowired(required = false)
    private YzServ yzServ; //因子实时查询dubbo服务

    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper; //事件与活动关联表

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
    private EventRedisService eventRedisService;


    private List<String> campaignList;
    private List<String> initIdList;
    private Long eventId;
    private String landId;
    private String custId;
    private Map<String, String> map;
    private List<Map<String, Object>> evtTriggers;


    public CustListTaskServiceImpl( HashMap<String, Object> hashMap) {
        this.campaignList = (List<String>) hashMap.get("campaignList");
        this.initIdList = (List<String>) hashMap.get("initIdList");
        this.eventId = (Long ) hashMap.get("eventId");
        this.landId = (String) hashMap.get("landId");
        this.custId = (String) hashMap.get("custId");
        this.map = (Map<String, String>) hashMap.get("map");
        this.evtTriggers = (List<Map<String, Object>>) hashMap.get("evtTriggers");
    }


    /**
     * 获取客户清单 Task
     */
    @Override
    public Object call() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Map<String, Object> resultByEventRedis = eventRedisService.getRedis("CAM_EVT_REL_", eventId);
            List<MktCamEvtRel> resultByEvent = new ArrayList<>();
            if (resultByEventRedis != null) {
                resultByEvent = (List<MktCamEvtRel>) resultByEventRedis.get("CAM_EVT_REL_" + eventId);
            }

//                List<MktCamEvtRel> resultByEvent = (List<MktCamEvtRel> ) redisUtils.get("CAM_EVT_REL_" + eventId);
//                if (resultByEvent == null) {
//                    resultByEvent = mktCamEvtRelMapper.qryBycontactEvtId(eventId);
//                    redisUtils.set("CAM_EVT_REL_" + eventId, resultByEvent);
//                }
            //判断有没有客户级活动
            Boolean hasCust = false;  //是否有客户级
            Boolean hasProm = false;  //是否有套餐级
            for (MktCamEvtRel mktCamEvtRel : resultByEvent) {
                if (campaignList.contains(mktCamEvtRel.getMktCampaignId().toString()) && !hasCust && mktCamEvtRel.getLevelConfig() == 1) {
                    hasCust = true;
                } else if(campaignList.contains(mktCamEvtRel.getMktCampaignId().toString()) && !hasProm && mktCamEvtRel.getLevelConfig() == 2){
                    hasProm = true;
                }
            }
            List<String> custRuleIdList = new ArrayList<>();
            List<String> assetRuleIdList = new ArrayList<>();
            List<String> packageRuleIdList = new ArrayList<>();
            List<Map<String, Object>> mapList = mktCamEvtRelMapper.selectRuleIdsByEventId(eventId);
            for (Map<String, Object> ruleMap : mapList) {
                if (campaignList.contains(ruleMap.get("campaignId").toString())) {
                    if ((Integer) ruleMap.get("levelConfig") == 1) {  // 客户级
                        custRuleIdList.add(ruleMap.get("ruleId").toString());
                    } else if((Integer) ruleMap.get("levelConfig") == 2){ // 套餐级
                        packageRuleIdList.add(ruleMap.get("ruleId").toString());
                    } else {
                        assetRuleIdList.add(ruleMap.get("ruleId").toString());  // 资产级
                    }
                }
            }

            JSONObject param = new JSONObject();
            //查询标识
            param.put("c3", landId);
            param.put("queryId", custId);
            param.put("queryNum", "");
            param.put("queryFields", "");
            param.put("type", "4");
            param.put("centerType", "00");

            Map<String, Object> custParamMap = new HashMap<>();
            Map<String, Object> assetParamMap = new HashMap<>();
            Map<String, Object> promParamMap = new HashMap<>();
            JSONArray accArray = new JSONArray();
            List<String> custIdList = new ArrayList<>();
            List<String> assetIdList = new ArrayList<>();
            List<String> promIdList = new ArrayList<>();
            if (custId != null && !"".equals(custId) && hasCust) {
                Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(param));
                if ("0".equals(dubboResult.get("result_code").toString())) {
                    accArray = new JSONArray((List<Object>) dubboResult.get("msgbody"));
                    for (Object o : accArray) {
                        custIdList.add(((Map) o).get("ASSET_INTEG_ID").toString());
                    }
                }
            } else {
                assetIdList.add(map.get("integrationId"));
            }
            if(hasProm){
                List<Map<String, Object>> accNbrMapList = getAccNbrList(map.get("accNbr"));
                for (Map<String, Object> accNbrMap : accNbrMapList) {
                    String assetIntegId = (String) accNbrMap.get("ASSET_INTEG_ID");
                    promIdList.add(assetIntegId);
                }
            }
            custParamMap.put("assetList", custIdList);
            custParamMap.put("ruleList", custRuleIdList);

            assetParamMap.put("assetList", assetIdList);
            assetParamMap.put("ruleList", assetRuleIdList);

            promParamMap.put("assetList", promIdList);
            promParamMap.put("ruleList", packageRuleIdList);

            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("prom", promParamMap);
            paramMap.put("cust", custParamMap);
            paramMap.put("asset", assetParamMap);
            paramMap.put("campaignList", initIdList);

            // 获取本地网的中文名
            String landName = AreaNameEnum.getNameByLandId(Long.valueOf(landId));

            Map<String, Object> paramResultMap = esService.queryCustomer4Event(paramMap);

            List<Map<String, Object>> resultList = new ArrayList<>();
            // 解析
            if ("200".equals(paramResultMap.get("resultCode"))) {
                List<Map<String, Object>> resultMapList = (List<Map<String, Object>>) paramResultMap.get("data");
                if (resultMapList != null && resultMapList.size() > 0) {
                    for (Map<String, Object> resultMap1 : resultMapList) {
                        Map<String, Object> result = new HashMap();
                        List<Map<String, Object>> taskChlList = (List<Map<String, Object>>) ((Map) resultMap1.get("CPC_VALUE")).get("taskChlList");
                        int count = 0;  // 统计符合渠道的个数
                        List<Map<String, Object>> taskChlListNew = new ArrayList<>();
                        for (Map<String, Object> taskChlMap : taskChlList) {
                            if (map.get("channelCode").equals(taskChlMap.get("channelId"))) {
                                count++;
                                taskChlListNew.add(taskChlMap);
                            }
                        }
                        if (count > 0) {
                            ((Map) resultMap1.get("CPC_VALUE")).put("taskChlList", taskChlListNew);
                        }

                        // 销售品过滤
                        String custProdFilter = null;
                        Map<String, Object> custProdFilterRedis = eventRedisService.getRedis("CUST_PROD_FILTER");
                        if (custProdFilterRedis != null) {
                            custProdFilter = (String) custProdFilterRedis.get("CUST_PROD_FILTER");
                        }

//                            String custProdFilter = (String) redisUtils.get("CUST_PROD_FILTER");
//                            //String custProdFilter = null;
//                            if (custProdFilter == null) {
//                                List<SysParams> sysParamsList = sysParamsMapper.listParamsByKeyForCampaign("CUST_PROD_FILTER");
//                                if (sysParamsList != null && sysParamsList.size() > 0) {
//                                    custProdFilter = sysParamsList.get(0).getParamValue();
//                                    redisUtils.set("CUST_PROD_FILTER", custProdFilter);
//                                }
//                            }


                        if (custProdFilter != null && "2".equals(custProdFilter)) {
                            List<Map<String, Object>> taskMapNewList = new ArrayList<>();

                            Map<String, Object> taskMap = (Map<String, Object>) resultMap1.get("CPC_VALUE");

                            List<Map<String, Object>> taskChlNewList = (List<Map<String, Object>>) ((Map) resultMap1.get("CPC_VALUE")).get("taskChlList");

                            String integrationId = (String) taskMap.get("integrationId");

                            // 判断该活动是否配置了销售品过滤
                            Integer mktCampaignId = (Integer) taskMap.get("activityId");

                            Map<String, Object> filterRuleListRedis = eventRedisService.getRedis("FILTER_RULE_LIST_", mktCampaignId.longValue());
                            List<FilterRule> filterRuleList = new ArrayList<>();
                            if (filterRuleListRedis != null) {
                                filterRuleList = (List<FilterRule>) filterRuleListRedis.get("FILTER_RULE_LIST_"+mktCampaignId);
                            }
//                                List<FilterRule> filterRuleList = (List<FilterRule>) redisUtils.get("FILTER_RULE_LIST_" + mktCampaignId);
//                                if (filterRuleList == null) {
//                                    filterRuleList = filterRuleMapper.selectFilterRuleList(Long.valueOf(mktCampaignId));
//                                    redisUtils.set("FILTER_RULE_LIST_" + mktCampaignId, filterRuleList);
//                                }

                            boolean prodConfig = false;
                            boolean pordFilter = true; // true:未包含要过滤的销售品， false：包含要过滤的销售品
                            for (FilterRule filterRule : filterRuleList) {
                                if ("3000".equals(filterRule.getFilterType()) || "3000" == filterRule.getFilterType()) {
                                    prodConfig = true;
                                }
                                if (prodConfig) {
                                    ResponseResult<AssetDto> assetDtoResponseResult = ctgCacheAssetService.queryCachedAssetDetailByIntegId(integrationId, landName);
                                    AssetDto assetDto = assetDtoResponseResult.getData();
                                    List<String> prodStrList = new ArrayList<>();
                                    if (assetDto != null) {
                                        List<AssetPromDto> assetPromDtoList = assetDto.getAssetPromDtoList();
                                        for (AssetPromDto assetPromDto : assetPromDtoList) {
                                            prodStrList.add(assetPromDto.getSelectablePromNum());
                                        }
                                    }
                                    // 获取过滤规则中的销售品集合
                                    List<String> codeList = ChannelUtil.StringToList(filterRule.getChooseProduct());
                                    //存在于校验
                                    if ("1000".equals(filterRule.getOperator())) {  //  1000 - 存在
                                        int sum = 0;
                                        for (String productCode : codeList) {
                                            // 包含销售品过滤
                                            if (prodStrList.contains(productCode)) {
                                                sum++;
                                                break;
                                            }
                                        }
                                        // 若有销售品存在跳过，若没有，直接过滤
                                        if (sum > 0) {
                                            continue;
                                        } else {
                                            pordFilter = false;
                                        }
                                    } else if ("2000".equals(filterRule.getOperator())) {      //  2000 - 不存在
                                        int cou = 0;
                                        for (String productCode : codeList) {
                                            // 不包含销售品过滤
                                            if (prodStrList.contains(productCode)) {
                                                cou++;
                                                break;
                                            }
                                        }
                                        // 若有销售品存在，这直接过滤
                                        if (cou > 0) {
                                            pordFilter = false;
                                            break;
                                        }
                                    }
                                }
                                prodConfig = false;
                            }
                            if (pordFilter) {
                                taskMapNewList.addAll(taskChlNewList);

                            }
                            ((Map) resultMap1.get("CPC_VALUE")).put("taskChlList", taskMapNewList);
                        }

                        List<Map> taskChlCountList = (List<Map>) ((Map) resultMap1.get("CPC_VALUE")).get("taskChlList");
                        // 清单方案放入采集项
                        Map<String, Object> evtContent = (Map<String, Object>) JSON.parse(map.get("evtContent"));
                        for (Map<String, Object> taskChlCountMap : taskChlCountList) {
                            // taskChlCountMap.put("triggers", JSONArray.parse(JSONArray.toJSON(map.get("evtContent")).toString()));
                            List<Map<String, Object>> triggersList = new ArrayList<>();
                            if(evtContent!=null){
                                for (Map.Entry entry : evtContent.entrySet()) {
                                    Map<String, Object> trigger = new HashMap<>();
                                    trigger.put("key", entry.getKey());
                                    trigger.put("value", entry.getValue());
                                    triggersList.add(trigger);
                                }
                                taskChlCountMap.put("triggers", triggersList);
                            }
                        }

                        if (taskChlCountList != null && taskChlCountList.size() > 0) {
                            result.putAll((Map) resultMap1.get("CPC_VALUE"));
                            result.put("orderISI", map.get("reqId"));
                            result.put("skipCheck", "0");
                            result.put("orderPriority", "0");
                            Long activityId = Long.valueOf(resultMap1.get("ACTIVITY_ID").toString());

                            //查询活动信息
                            Map<String, Object> mktCampaignRedis = eventRedisService.getRedis("MKT_CAMPAIGN_", activityId);
                            MktCampaignDO mktCampaignDO = new MktCampaignDO();
                            if (mktCampaignRedis != null) {
                                mktCampaignDO = (MktCampaignDO) mktCampaignRedis.get("MKT_CAMPAIGN_" + activityId);
                            }
//                                MktCampaignDO mktCampaignDO = (MktCampaignDO) redisUtils.get("MKT_CAMPAIGN_" + activityId);
//                                if (mktCampaignDO == null) {
//                                    mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(activityId);
//                                    redisUtils.set("MKT_CAMPAIGN_" + activityId, mktCampaignDO);
//                                }

                            if (mktCampaignDO != null) {
                                if ("1000".equals(mktCampaignDO.getMktCampaignType())) {
                                    result.put("activityType", "0"); //营销
                                } else if ("5000".equals(mktCampaignDO.getMktCampaignType())) {
                                    result.put("activityType", "1"); //服务
                                } else if ("6000".equals(mktCampaignDO.getMktCampaignType())) {
                                    result.put("activityType", "2"); //随销
                                } else {
                                    result.put("activityType", "0"); //活动类型 默认营销
                                }

                                result.put("activityStartTime", simpleDateFormat.format(mktCampaignDO.getPlanBeginTime()));
                                result.put("activityEndTime", simpleDateFormat.format(mktCampaignDO.getPlanEndTime()));
                            } else {
                                result.put("activityType", "");
                                result.put("activityStartTime", "");
                                result.put("activityEndTime", "");
                            }
                            resultList.add(result);
                        }
                    }
                }
            }
            resultMap.put("ruleList", resultList);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception = " + e);
        }
        return resultMap;
    }


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
            if (mainOfferInstId!=null){
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
}
