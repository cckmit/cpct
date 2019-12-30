package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.elastic.config.IndexList;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.es.EsHitsService;
import com.zjtelcom.cpct.service.event.EventRedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Calendar.MONTH;

@Service
@Transactional
public class ListResultByEventTaskServiceImpl implements Callable {
    private static final Logger log = LoggerFactory.getLogger(ListResultByEventTaskServiceImpl.class);

    @Autowired
    private EsHitsService esHitService;  //es存储

    @Autowired(required = false)
    private YzServ yzServ; //因子实时查询dubbo服务

    @Autowired
    private EventRedisService eventRedisService;

    private String lanId;
    private String channel;
    private String reqId;
    private String accNbr;
    private Map<String, Object> act;
    private String c4;
    private String custId;



    public ListResultByEventTaskServiceImpl(HashMap<String, Object> hashMap) {
        this.lanId = (String)hashMap.get("lanId");
        this.channel = (String)hashMap.get("channel");
        this.reqId = (String)hashMap.get("reqId");
        this.accNbr = (String)hashMap.get("accNbr");
        this.act = (Map<String, Object>)hashMap.get("act");
        this.c4 = (String)hashMap.get("c4");
        this.custId = (String)hashMap.get("custId");
    }

    @Override
    public Object call() throws Exception {

        Map<String, Object> resultMap = new ConcurrentHashMap<>();

        Map<String, Object> mktCampaignMap = new ConcurrentHashMap<>();

        Map<String, Object> mktCampaignCustMap = new ConcurrentHashMap<>();
        log.info("活动预校验开始************");

        try {
            Long mktCampaginId = (Long) act.get("mktCampaginId");
            //初始化es log
            JSONObject esJson = new JSONObject();
            esJson.put("reqId", reqId);
            esJson.put("activityId", mktCampaginId);
            esJson.put("activityName", act.get("mktCampaginName"));
            esJson.put("activityCode", act.get("mktCampaginNbr"));
            esJson.put("hitEntity", accNbr); //命中对象
            esJson.put("eventCode", act.get("eventCode"));

            //isale
            if ("QD000015".equals(channel)) {
                log.info("活动预校验：channel-----QD000015");
                List<String> strategyTypeList = new ArrayList<>();
                strategyTypeList.add("1000");
                strategyTypeList.add("2000");
                strategyTypeList.add("5000");

                boolean iSRed = false;
                //验证过滤规则时间,默认只查询5000类型的时间段过滤
                Map<String, Object> params = new HashMap<>();
                params.put("strategyTypeList", strategyTypeList);
                List<FilterRule> filterRuleList = new ArrayList<>();
                Map<String, Object> filterRuleRedis = eventRedisService.getRedis("FILTER_RULE_STR_", mktCampaginId,  params);
                if (filterRuleRedis != null) {
                    filterRuleList = (List<FilterRule>) filterRuleRedis.get("FILTER_RULE_STR_" + mktCampaginId);
                }

                for (FilterRule filterRule : filterRuleList) {
                    if ("1000".equals(filterRule.getFilterType()) || "2000".equals(filterRule.getFilterType())) {
                        iSRed = true;
                        break;
                    }
                }
                JSONArray accArrayF = new JSONArray();
                if (iSRed) {
                    JSONObject param = new JSONObject();
                    //查询标识
                    param.put("c3", lanId);
                    param.put("queryId", custId);
                    param.put("queryNum", "");
                    param.put("queryFields", "");
                    param.put("type", "4");
                    param.put("centerType", "00");
                    Map<String, Object> dubboResult_F = new HashMap<>();
                    try {
                        dubboResult_F = yzServ.queryYz(JSON.toJSONString(param));
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("queryYz查询失败："+e,JSON.toJSONString(dubboResult_F));
                    }
                    try {
                        if ("0".equals(dubboResult_F.get("result_code").toString())) {
                            accArrayF = new JSONArray((List<Object>) dubboResult_F.get("msgbody"));
                        }
                    } catch (Exception e) {
                        log.info("dubboResult_F.get(\"result_code\").toString()异常了~~~");
                        e.printStackTrace();
                    }
                }

                try {
                    for (FilterRule filterRule : filterRuleList) {
                        if ("1000".equals(filterRule.getFilterType()) || "2000".equals(filterRule.getFilterType())) {
                            //获取名单
                            String userList = filterRule.getUserList();
                            if (userList != null && !"".equals(userList)) {
                                for (Object ob : accArrayF) {
                                    int index = userList.indexOf(((Map) ob).get("ACC_NBR").toString());
                                    if (index >= 0) {
                                        esJson.put("hit", false);
                                        esJson.put("msg", "红黑名单过滤规则验证被拦截");
                                        esHitService.save(esJson, IndexList.ACTIVITY_MODULE);
                                        return Collections.EMPTY_MAP;
                                    }
                                }
                            }
                        } else if ("5000".equals(filterRule.getFilterType())) {
                            //时间段的格式
                            if (compareHourAndMinute(filterRule)) {
                                log.info("过滤时间段验证被拦截");
                                esJson.put("hit", false);
                                esJson.put("msg", "过滤时间段验证被拦截");
                                esHitService.save(esJson, IndexList.ACTIVITY_MODULE);
                                return Collections.EMPTY_MAP;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.info("filterRuleList 预校验过滤出错！");
                    e.printStackTrace();
                }
            } else {
                try {
                    List<String> strategyTypeList = new ArrayList<>();
                    strategyTypeList.add("1000");
                    strategyTypeList.add("2000");
                    strategyTypeList.add("5000");
                    //验证过滤规则时间,默认只查询5000类型的时间段过滤
                    Map<String, Object> params = new HashMap<>();
                    params.put("strategyTypeList", strategyTypeList);
                    List<FilterRule> filterRuleList = new ArrayList<>();
                    Map<String, Object> filterRuleRedis = eventRedisService.getRedis("FILTER_RULE_STR_", mktCampaginId,  params);
                    if (filterRuleRedis != null) {
                        filterRuleList = (List<FilterRule>) filterRuleRedis.get("FILTER_RULE_STR_" + mktCampaginId);
                    }
                    //List<FilterRule> filterRuleList = filterRuleMapper.selectFilterRuleListByStrategyId(mktCampaginId, strategyTypeList);
                    for (FilterRule filterRule : filterRuleList) {
                        if ("1000".equals(filterRule.getFilterType()) || "2000".equals(filterRule.getFilterType())) {
                            //获取名单
                            String userList = filterRule.getUserList();
                            if (userList != null && !"".equals(userList)) {
                                int index = userList.indexOf(accNbr);
                                if (index >= 0) {
                                    esJson.put("hit", false);
                                    esJson.put("msg", "红黑名单过滤规则验证被拦截");
                                    esHitService.save(esJson, IndexList.ACTIVITY_MODULE);
                                    return Collections.EMPTY_MAP;
                                }
                            }
                        } else if ("5000".equals(filterRule.getFilterType())) {
                            //时间段的格式
                            if (compareHourAndMinute(filterRule)) {
                                log.info("过滤时间段验证被拦截");
                                esJson.put("hit", false);
                                esJson.put("msg", "过滤时间段验证被拦截");
                                esHitService.save(esJson, IndexList.ACTIVITY_MODULE);
                                return Collections.EMPTY_MAP;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.info("filterRuleList else ！预校验过滤出错");
                    e.printStackTrace();
                }
            }


            //查询活动信息
            Map<String, Object> mktCampaignRedis = eventRedisService.getRedis("MKT_CAMPAIGN_", mktCampaginId);
            MktCampaignDO mktCampaign = new MktCampaignDO();
            if (mktCampaignRedis != null) {
                mktCampaign = (MktCampaignDO) mktCampaignRedis.get("MKT_CAMPAIGN_" + mktCampaginId);
            }

            Date now = null;
            try {
                now = new Date();
                //验证活动生效时间
                Date beginTime = mktCampaign.getPlanBeginTime();
                Date endTime = mktCampaign.getPlanEndTime();
                if (now.before(beginTime) || now.after(endTime)) {
                    //当前时间不在活动生效时间内
                    esJson.put("hit", false);
                    esJson.put("msg", "当前时间不在活动生效时间内");
                    log.info("当前时间不在活动生效时间内");
                    esHitService.save(esJson, IndexList.ACTIVITY_MODULE);
                    return Collections.EMPTY_MAP;
                }
            } catch (Exception e) {
                log.info("验证活动生效时间失败");
                e.printStackTrace();
            }

            log.info("预校验还没出错，拿到活动信息");

            // 判断活动状态


            if (!(StatusCode.STATUS_CODE_PUBLISHED.getStatusCode().equals(mktCampaign.getStatusCd())
                    || StatusCode.STATUS_CODE_ADJUST.getStatusCode().equals(mktCampaign.getStatusCd()))) {
                esJson.put("hit", false);
                esJson.put("msg", "活动状态未发布");
//                log.info("活动状态未发布");
                esHitService.save(esJson, IndexList.ACTIVITY_MODULE);
                return Collections.EMPTY_MAP;
            }


            // 判断触发活动类型
            if (!StatusCode.REAL_TIME_CAMPAIGN.getStatusCode().equals(mktCampaign.getTiggerType())
                    && !StatusCode.MIXTURE_CAMPAIGN.getStatusCode().equals(mktCampaign.getTiggerType())) {
                esJson.put("hit", false);
                esJson.put("msg", "活动触发类型不符");
                log.info("活动触发类型不符");
                esHitService.save(esJson, IndexList.ACTIVITY_MODULE);
                return Collections.EMPTY_MAP;
            }

            // 判断活动类型
            if (!StatusCode.AUTONOMICK_CAMPAIGN.getStatusCode().equals(mktCampaign.getMktCampaignCategory())) {
                esJson.put("hit", false);
                esJson.put("msg", "活动类型不符");
                log.info("活动类型不符");
                esHitService.save(esJson, IndexList.ACTIVITY_MODULE);
                return Collections.EMPTY_MAP;
            }

            // 查询策略信息
            Map<String, Object> MktStrategyConfRedis = eventRedisService.getRedis("MKT_CAM_STRATEGY_", mktCampaginId);
            List<MktStrategyConfDO> mktStrategyConfDOS = new ArrayList<>();
            if (MktStrategyConfRedis != null) {
                mktStrategyConfDOS = ( List<MktStrategyConfDO>) MktStrategyConfRedis.get("MKT_CAM_STRATEGY_" + mktCampaginId);
            }

            if (mktStrategyConfDOS == null) {
                esJson.put("hit", false);
                esJson.put("msg", "策略查询失败");
                log.info("策略查询失败");
                esHitService.save(esJson, IndexList.ACTIVITY_MODULE);
                return Collections.EMPTY_MAP;
            }
            List<Map<String, Object>> strategyMapList = new ArrayList<>();
            for (MktStrategyConfDO mktStrategyConf : mktStrategyConfDOS) {

                //初始化es log
                JSONObject esJsonStrategy = new JSONObject();
                esJsonStrategy.put("reqId", reqId);
                esJsonStrategy.put("activityId", mktCampaginId);
                esJsonStrategy.put("hitEntity", accNbr); //命中对象
                esJsonStrategy.put("strategyConfId", mktStrategyConf.getMktStrategyConfId());
                esJsonStrategy.put("strategyConfName", mktStrategyConf.getMktStrategyConfName());

                Map<String, Object> strategyMap = new ConcurrentHashMap<>();
                //验证策略生效时间
                if (!(now.after(mktStrategyConf.getBeginTime()) && now.before(mktStrategyConf.getEndTime()))) {
                    //若当前时间在策略生效时间外
//                    log.info("当前时间不在策略生效时间内");

                    esJson.put("hit", false);
                    esJson.put("msg", "策略未命中");
                    esHitService.save(esJson, IndexList.ACTIVITY_MODULE, reqId + mktCampaginId + accNbr);

                    esJsonStrategy.put("hit", false);
                    esJsonStrategy.put("msg", "当前时间不在策略生效时间内");
                    esHitService.save(esJsonStrategy, IndexList.STRATEGY_MODULE);
                    continue;
                }
                //适用地市校验
                if (mktStrategyConf.getAreaId() != null && !"".equals(mktStrategyConf.getAreaId())) {
                    String[] strArrayCity = mktStrategyConf.getAreaId().split("/");
                    boolean areaCheck = true;
                    for (String str : strArrayCity) {
                        if (c4 != null && c4.equals(str)) {
                            areaCheck = false;
                            break;
                        } else if (lanId != null) {
                            if (lanId.equals(str)) {
                                areaCheck = false;
                                break;
                            }
                        } else {
                            //适用地市获取异常 lanId
//                            log.info("适用地市获取异常");

                            esJson.put("hit", false);
                            esJson.put("msg", "策略未命中");
                            esHitService.save(esJson, IndexList.ACTIVITY_MODULE, reqId + mktCampaginId + accNbr);

                            strategyMap.put("msg", "适用地市获取异常");
                            esJsonStrategy.put("hit", "false");
                            esJsonStrategy.put("msg", "适用地市获取异常");
                            esHitService.save(esJsonStrategy, IndexList.STRATEGY_MODULE);
                        }
                    }
                    if (areaCheck) {

                        esJson.put("hit", false);
                        esJson.put("msg", "策略未命中");
                        esHitService.save(esJson, IndexList.ACTIVITY_MODULE, reqId + mktCampaginId + accNbr);

                        strategyMap.put("msg", "适用地市不符");
                        esJsonStrategy.put("hit", "false");
                        esJsonStrategy.put("msg", "适用地市不符");
                        esHitService.save(esJsonStrategy, IndexList.STRATEGY_MODULE);
                        continue;
                    }
                } else {
                    //适用地市数据异常
//                    log.info("适用地市数据异常");

                    esJson.put("hit", false);
                    esJson.put("msg", "策略未命中");
                    esHitService.save(esJson, IndexList.ACTIVITY_MODULE, reqId + mktCampaginId + accNbr);

                    strategyMap.put("msg", "适用地市数据异常");
                    esJsonStrategy.put("hit", "false");
                    esJsonStrategy.put("msg", "适用地市数据异常");
                    esHitService.save(esJsonStrategy, IndexList.STRATEGY_MODULE);
                    continue;
                }
                log.info("预校验还没出错1");
                // 获取规则
                List<Map<String, Object>> ruleMapList = new ArrayList<>();
                List<MktStrategyConfRuleDO> mktStrategyConfRuleList = new ArrayList<>();
                Map<String, Object> mktRuleListRedis = eventRedisService.getRedis("RULE_LIST_", mktStrategyConf.getMktStrategyConfId());
                if (mktRuleListRedis != null) {
                    mktStrategyConfRuleList = (List<MktStrategyConfRuleDO>) mktRuleListRedis.get("RULE_LIST_" + mktStrategyConf.getMktStrategyConfId());
                }

                try {
                    for (MktStrategyConfRuleDO mktStrategyConfRuleDO : mktStrategyConfRuleList) {
                        Map<String, Object> ruleMap = new ConcurrentHashMap<>();
                        String evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId();
                        //                    if (evtContactConfMapList != null && evtContactConfMapList.size() > 0) {
                        ruleMap.put("ruleId", mktStrategyConfRuleDO.getMktStrategyConfRuleId());
                        ruleMap.put("ruleName", mktStrategyConfRuleDO.getMktStrategyConfRuleName());
                        ruleMap.put("tarGrpId", mktStrategyConfRuleDO.getTarGrpId());
                        ruleMap.put("productId", mktStrategyConfRuleDO.getProductId());
                        ruleMap.put("evtContactConfId", mktStrategyConfRuleDO.getEvtContactConfId());
                        //                        ruleMap.put("evtContactConfMapList", evtContactConfMapList);
                        ruleMapList.add(ruleMap);
                        //                    }
                    }
                } catch (Exception e) {
                    log.error("预校验获取规则查询失败");
                    e.printStackTrace();
                }
                if (ruleMapList != null && ruleMapList.size() > 0) {
                    strategyMap.put("strategyConfId", mktStrategyConf.getMktStrategyConfId());
                    strategyMap.put("strategyConfName", mktStrategyConf.getMktStrategyConfName());
                    strategyMap.put("ruleMapList", ruleMapList);
                    strategyMapList.add(strategyMap);
                }
            }

            log.info("预校验还没出错2");
            List<String> mktCamCodeList = new ArrayList<>();
            Map<String, Object> mktCamCodeListRedis = eventRedisService.getRedis("MKT_CAM_API_CODE_KEY");
            if(mktCamCodeListRedis!=null){
                mktCamCodeList = (List<String>) mktCamCodeListRedis.get("MKT_CAM_API_CODE_KEY");
            }

            if (strategyMapList != null && strategyMapList.size() > 0) {
                // 判断initId是否在清单列表里面
                if (mktCamCodeList.contains(mktCampaign.getInitId().toString())) {
                    // if (mktCamCodeList.contains(mktCampaign.getMktCampaignId().toString())) {
                    mktCampaignCustMap.put("initId", mktCampaign.getInitId());
                    mktCampaignCustMap.put("mktCampaginId", mktCampaginId);
                    mktCampaignCustMap.put("levelConfig", act.get("levelConfig"));
                    mktCampaignCustMap.put("campaignSeq", act.get("campaignSeq"));
                    mktCampaignCustMap.put("strategyMapList", strategyMapList);
                } else {
                    mktCampaignMap.put("mktCampaginId", mktCampaginId);
                    mktCampaignMap.put("levelConfig", act.get("levelConfig"));
                    mktCampaignMap.put("campaignSeq", act.get("campaignSeq"));
                    mktCampaignMap.put("strategyMapList", strategyMapList);
                    mktCampaignMap.put("type", mktCampaign.getMktCampaignType().toString()); // 活动类型 5000
                }
            }
            // 实时
            resultMap.put("mktCampaignMap", mktCampaignMap);
            // 清单
            resultMap.put("mktCampaignCustMap", mktCampaignCustMap);

        } catch (Exception e) {
            log.info("预校验出错",e.getCause());
            log.info("预校验出错",e.toString());
            log.info("预校验出错",e.getMessage());
            e.printStackTrace();
        }
        return resultMap;
    }


    private boolean compareHourAndMinute(FilterRule filterRule) {
        Boolean result = false;
        Calendar cal = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.setTime(filterRule.getDayStart());
        start.set(cal.get(Calendar.YEAR), cal.get(MONTH), cal.get(Calendar.DAY_OF_MONTH));
        Calendar end = Calendar.getInstance();
        end.setTime(filterRule.getDayEnd());
        end.set(cal.get(Calendar.YEAR), cal.get(MONTH), cal.get(Calendar.DAY_OF_MONTH));

        if ((cal.getTimeInMillis() - start.getTimeInMillis()) > 0 && (cal.getTimeInMillis() - end.getTimeInMillis()) < 0) {
            result = true;
        }

        return result;
    }
}
