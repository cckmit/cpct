package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.rule.RuleResult;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMatchRulMapper;
import com.zjtelcom.cpct.dao.event.EventSceneMapper;
import com.zjtelcom.cpct.dao.event.EvtSceneCamRelMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.user.UserListMapper;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.ContactEvtMatchRul;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.dubbo.service.EventApiService;
import com.zjtelcom.cpct.dubbo.task.ActivityTask;
import com.zjtelcom.cpct.elastic.config.IndexList;
import com.zjtelcom.cpct.elastic.service.EsService;
import com.zjtelcom.cpct.util.HttpUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;

@Service
//@Transactional
public class EventApiServiceImpl implements EventApiService {

    @Autowired(required = false)
    private ContactEvtMapper contactEvtMapper; //事件总表

    @Autowired(required = false)
    private ContactEvtMatchRulMapper contactEvtMatchRulMapper; //事件过滤规则

    @Autowired(required = false)
    private EventSceneMapper eventSceneMapper; //事件场景

    @Autowired(required = false)
    private MktCamEvtRelMapper mktCamEvtRelMapper; //事件与活动关联表

    @Autowired(required = false)
    private MktCampaignMapper mktCampaignMapper; //活动基本信息

    @Autowired(required = false)
    private UserListMapper userListMapper; //过滤规则（红名单、黑名单数据）

    @Autowired(required = false)
    private TarGrpConditionMapper tarGrpConditionMapper; //分群规则条件表

    @Autowired(required = false)
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper; //活动策略关联

    @Autowired(required = false)
    private MktStrategyConfMapper mktStrategyConfMapper; //策略基本信息

    @Autowired(required = false)
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;//策略规则

    @Autowired(required = false)
    private FilterRuleMapper filterRuleMapper; //过滤规则

    @Autowired(required = false)
    private FilterRuleConfMapper filterRuleConfMapper; //过滤规则与策略规则关联表

    @Autowired(required = false)
    private PpmProductMapper ppmProductMapper; //销售品

    @Autowired(required = false)
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper; //协同渠道配置基本信息

    @Autowired(required = false)
    private MktCamChlConfMapper mktCamChlConfMapper; //协同渠道配置的渠道

    @Autowired(required = false)
    private MktVerbalConditionMapper mktVerbalConditionMapper; //规则存储公共表（此处查询协同渠道子策略规则和话术规则）

    @Autowired(required = false)
    private MktCamScriptMapper mktCamScriptMapper; //营销脚本

    @Autowired(required = false)
    private MktVerbalMapper mktVerbalMapper; //话术

    @Autowired(required = false)
    private InjectionLabelMapper injectionLabelMapper; //标签因子

    @Autowired(required = false)
    private EsService esService;  //es存储

    @Autowired(required = false)
    private RedisUtils redisUtils;  // redis方法

    /**
     * 事件触发接口实现
     */
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public Map deal(Map<String, Object> map) {
//
//        long begin = System.currentTimeMillis();
//
//        //初始化返回结果
//        Map<String, Object> result = new HashMap();
//
//        //获取当前时间
//        Date now = new Date();
//
//        //获取事件code
//        String eventNbr = (String) map.get("eventId");
//        //获取流水号
//        String ISI = (String) map.get("ISI");
//
//        //从map中取出参数
////        EventReportDTO eventReportDTO = new EventReportDTO();
////        eventReportDTO.setEventId((String) map.get("eventId")); //事件code
////        eventReportDTO.setC4((String) map.get("C4")); //C4代码
////        eventReportDTO.setChannelId((String) map.get("channelId")); //渠道
////        eventReportDTO.setISI((String) map.get("ISI")); //流水号
////        eventReportDTO.setLanId(Long.valueOf((String) map.get("lanId"))); //本地网标识
//
//        //构造返回参数
//        result.put("custId", "客户编码");
//        result.put("CPCResultCode", "1");
//        result.put("CPCResultMsg", "success");
//        result.put("ISI", ISI);
//
//        long cost = System.currentTimeMillis() - begin;
//        JSONObject jsonObject=new JSONObject();
//        jsonObject.put("timeCost",cost);
//        jsonObject.put("ISI",ISI);
////        jsonObject.put("eventId",eventId);
////        jsonObject.put("activityList",activityList);
//        esService.save(jsonObject,IndexList.EVENT_MODULE);
//
//        //获取标签因子集合
//        List<Map<String, Object>> labelList = (List<Map<String, Object>>) map.get("triggers");
//
//        //根据事件code查询事件信息
//        ContactEvt event = contactEvtMapper.getEventByEventNbr(eventNbr);
//
//        //获取事件id
//        Long eventId = event.getContactEvtId();
//        //获取事件推荐活动数
//        int recCampaignAmount;
//        String recCampaignAmountStr = event.getRecCampaignAmount();
//        if (recCampaignAmountStr == null || "".equals(recCampaignAmountStr)) {
//            recCampaignAmount = 0;
//        } else {
//            recCampaignAmount = Integer.parseInt(recCampaignAmountStr);
//        }
//
//        //获取事件过滤规则
//        ContactEvtMatchRul contactEvtMatchRulParam = new ContactEvtMatchRul();
//        contactEvtMatchRulParam.setContactEvtId(eventId);
//        List<ContactEvtMatchRul> contactEvtMatchRuls = contactEvtMatchRulMapper.listEventMatchRuls(contactEvtMatchRulParam);
//        //遍历事件过滤规则匹配
//        if (contactEvtMatchRuls != null && contactEvtMatchRuls.size() > 0) {
//            //匹配事件过滤规则
//            int flag = 0;
//            for (ContactEvtMatchRul contactEvtMatchRul : contactEvtMatchRuls) {
//                flag = userListMapper.checkRule("", contactEvtMatchRul.getEvtMatchRulId(), null);
//                if (flag > 0) {
//                    result.put("CPCResultCode", "0");
//                    result.put("CPCResultMsg", "事件过滤拦截");
//                    return result;
//                }
//            }
//        }
//
//        //根据事件id 查询所有关联的事件场景
////        EventSceneDO param = new EventSceneDO();
////        param.setEventId(eventId);
////        List<EventScene> eventScenes = eventSceneMapper.qryEventSceneByEvtId(eventId);
//
//        //循环事件场景列表 根据事件场景获取活动列表
////        List<Long> activityIds = new ArrayList<>(); //活动id list
////        for (EventScene eventScene : eventScenes) {
////            List<EvtSceneCamRel> evtSceneCamRels = evtSceneCamRelMapper.selectCamsByEvtSceneId(eventScene.getEventSceneId());
////            if (evtSceneCamRels != null && evtSceneCamRels.size() > 0) {
////                for (EvtSceneCamRel evtSceneCamRel : evtSceneCamRels) {
////                    activityIds.add(evtSceneCamRel.getMktCampaignId());
////                }
////            }
////        }
//
//        //根据事件id 查询所有关联活动（根据优先级排序 正序）
//        List<Long> activityIds = mktCamEvtRelMapper.listActivityByEventId(eventId);
//
//        //初始化返回结果中的工单信息
//        List<Map<String, Object>> orderList = new ArrayList<>();
//
//        //遍历活动id  查询并匹配活动规则 需要根据事件推荐活动数 取前n个活动
//        int max = activityIds.size();
//        if (recCampaignAmount != 0) {
//            //事件推荐活动数
//            max = recCampaignAmount;
//        }
//        for (int j = 0; j < max; j++) {
//            //活动id
//            Long activityId = activityIds.get(j);
//
//            //返回参数
//            Map<String, Object> order = new HashMap<>();
//
//            //初始化规则引擎---------------------------
//            ExpressRunner runner = new ExpressRunner();
//            DefaultContext<String, Object> context = new DefaultContext<String, Object>();
//
//            //查询活动基本信息
//            MktCampaignDO mktCampaign = mktCampaignMapper.selectByPrimaryKey(activityId);
//
//            //返回参数中添加活动信息
//            order.put("orderISI", ISI);
//            order.put("activityId", mktCampaign.getMktCampaignId().toString());
//            order.put("activityName", mktCampaign.getMktCampaignName());
//            order.put("activityCode", mktCampaign.getMktActivityNbr());
//            order.put("skipCheck", "0");  //todo 不明 案例上有 文档上没有
//            order.put("orderPriority", "100");  //todo 不明 案例上有 文档上没有
//
//            //根据活动id获取策略列表
//            List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOs = mktCamStrategyConfRelMapper.selectByMktCampaignId(activityId);
//
//            if (mktCamStrategyConfRelDOs != null && mktCamStrategyConfRelDOs.size() > 0) {
//
//                //初始化返回结果中的推荐信息列表
//                List<Map<String, Object>> recommendList = new ArrayList<>();
//
//                //遍历策略列表
//                for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOs) {
//
//                    //查询策略基本信息
//                    MktStrategyConfDO mktStrategyConf = mktStrategyConfMapper.selectByPrimaryKey(mktCamStrategyConfRelDO.getStrategyConfId());
//
//                    //验证策略生效时间
//                    if (!(now.after(mktStrategyConf.getBeginTime()) && now.before(mktStrategyConf.getEndTime()))) {
//                        //若当前时间在策略生效时间外
//                        continue;
//                    }
//
//                    //todo 下发地市
//
//                    //todo 判断下发渠道
//
//                    //根据策略id获取策略下规则列表
//                    List<MktStrategyConfRuleDO> mktStrategyConfRuleDOS = mktStrategyConfRuleMapper.selectByMktStrategyConfId(mktCamStrategyConfRelDO.getStrategyConfId());
//
//                    //遍历规则列表
//                    if (mktStrategyConfRuleDOS != null && mktStrategyConfRuleDOS.size() > 0) {
//                        for (MktStrategyConfRuleDO mktStrategyConfRuleDO : mktStrategyConfRuleDOS) {
//
//                            //获取分群id
//                            Long tarGrpId = mktStrategyConfRuleDO.getTarGrpId();
//                            //获取销售品
//                            String productStr = mktStrategyConfRuleDO.getProductId();
//                            //过滤规则id
//                            Long ruleConfId = mktStrategyConfRuleDO.getRuleConfId();
//                            //协同渠道配置id
//                            String evtContactConfIdStr = mktStrategyConfRuleDO.getEvtContactConfId();
//
//                            //  1.判断活动的过滤规则---------------------------
//                            //获取过滤规则
//                            FilterRuleConfDO filterRuleConfDO = filterRuleConfMapper.selectByPrimaryKey(ruleConfId);
//                            String ruleConfIdStr = filterRuleConfDO.getFilterRuleIds();
//                            if (ruleConfIdStr != null) {
//                                String[] array = ruleConfIdStr.split(",");
//                                boolean ruleFilter = true;
//                                for (String str : array) {
//                                    //获取具体规则
//                                    FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(Long.parseLong(str));
//
//                                    //匹配事件过滤规则
//                                    int flag = 0;
//                                    flag = userListMapper.checkRule("", filterRule.getRuleId(), null);
//                                    if (flag > 0) {
//                                        ruleFilter = false;
//                                    }
//                                }
//                                //若存在不符合的规则 结束当前规则循环
//                                if (!ruleFilter) {
//                                    continue;
//                                }
//                            }
//
//                            //  2.判断活动的客户分群规则---------------------------
//                            //查询分群规则list
//                            List<TarGrpCondition> tarGrpConditionDOs = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
//                            //将规则拼装为表达式
//                            StringBuilder express = new StringBuilder();
//                            express.append("if(");
//                            //遍历所有规则
//                            for (int i = 0; i < tarGrpConditionDOs.size(); i++) {
//                                String type = tarGrpConditionDOs.get(i).getOperType();
//                                Label label = injectionLabelMapper.selectByPrimaryKey(Long.parseLong(tarGrpConditionDOs.get(i).getLeftParam()));
//                                express.append("(");
//                                express.append(label.getInjectionLabelCode());
//                                if ("1000".equals(type)) {
//                                    express.append(">");
//                                } else if ("2000".equals(type)) {
//                                    express.append("<");
//                                } else if ("3000".equals(type)) {
//                                    express.append("==");
//                                } else if ("4000".equals(type)) {
//                                    express.append("!=");
//                                } else if ("5000".equals(type)) {
//                                    express.append(">=");
//                                } else if ("6000".equals(type)) {
//                                    express.append("<=");
//                                }
//                                express.append(tarGrpConditionDOs.get(i).getRightParam());
//                                express.append(")");
//                                if (i + 1 != tarGrpConditionDOs.size()) {
//                                    express.append("&&");
//                                }
//                            }
//                            express.append(") {return true} else {return false}");
//
//                            //将标签因子值存入规则引擎上下文（这里目前假定接口传入的标签满足规则运算，不需要额外查询）
//                            for (Map<String, Object> objectMap : labelList) {
//                                Map<String, String> trigger = (Map<String, String>) objectMap.get("trigger");
//                                context.put(trigger.get("key"), trigger.get("value"));
////                                logger.info("key = {},value = {}", trigger.get("key"), trigger.get("value"));
//                            }
//
//                            //规则引擎计算
//                            RuleResult ruleResult = new RuleResult();
//                            try {
//                                ruleResult = runner.executeRule(express.toString(), context, true, true);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            //log输出
////                            logger.info("======================================");
////                            logger.info("事件流水 = {}", ISI);
////                            logger.info("事件ID = {}", eventNbr);
////                            logger.info("活动ID = {}", activityId);
////                            logger.info("express = {}", express);
////                            logger.info("result = {}", ruleResult.getResult());
////                            logger.info("tree = {}", ruleResult.getRule().toTree());
////                            logger.info("trace = {}", ruleResult.getTraceMap());
////                            logger.info("======================================");
//                            //判断匹配结果，如匹配则向下进行，如不匹配则continue结束本次循环
//                            if (ruleResult.getResult() != null && ((Boolean) ruleResult.getResult())) {
//
//                                //查询销售品列表
//                                String[] productArray = productStr.split(",");
//
//                                //初始化返回结果中的销售品条目
//                                List<Map<String, String>> productList = new ArrayList<>();
//                                for (String str : productArray) {
//                                    Map<String, String> product = new HashMap<>();
//                                    PpmProduct ppmProduct = ppmProductMapper.selectByPrimaryKey(Long.parseLong(str));
//
//                                    product.put("productCode", ppmProduct.getProductCode());
//                                    product.put("productFlag", ""); //todo 不明 案例上有 文档上没有
//                                    product.put("productAlias", ""); //todo 不明 案例上有 文档上没有
//                                    product.put("productName", ppmProduct.getProductName());
//                                    product.put("productType", ppmProduct.getProductType());
//                                    productList.add(product);
//                                }
//
//                                //获取协同渠道所有id
//                                String[] evtContactConfIdArray = evtContactConfIdStr.split(",");
//                                //遍历协同渠道
//                                for (String str : evtContactConfIdArray) {
//
//
//                                    //协同渠道规则表id（自建表）
//                                    Long evtContactConfId = Long.parseLong(str);
//
//                                    //查询渠道属性
//                                    List<MktCamChlConfAttrDO> mktCamChlConfAttrs = mktCamChlConfAttrMapper.selectByEvtContactConfId(evtContactConfId);
//
//                                    boolean checkTime = true;
//                                    for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrs) {
//                                        //判断渠道生失效时间
//                                        if (mktCamChlConfAttrDO.getAttrId() == 1000L) {
//                                            if (!now.after(new Date(mktCamChlConfAttrDO.getAttrValue()))) {
//                                                checkTime = false;
//                                            }
//                                        }
//                                        if (mktCamChlConfAttrDO.getAttrId() == 1001L) {
//                                            if (!now.after(new Date(mktCamChlConfAttrDO.getAttrValue()))) {
//                                                checkTime = false;
//                                            }
//                                        }
//                                        //判断接触时间段 todo
////                                        if (mktCamChlConfAttrDO.getAttrId() == 1003L) {
////                                            if (!now.after(new Date(mktCamChlConfAttrDO.getAttrValue()))) {
////                                                checkTime = false;
////                                            }
////                                        }
////                                        if (mktCamChlConfAttrDO.getAttrId() == 1004L) {
////                                            if (!now.after(new Date(mktCamChlConfAttrDO.getAttrValue()))) {
////                                                checkTime = false;
////                                            }
////                                        }
//                                    }
//
//                                    if (!checkTime) {
//                                        continue;
//                                    }
//
//                                    //初始化返回结果推荐信息
//                                    Map<String, Object> recommend = new HashMap<>();
//
//                                    //查询渠道信息基本信息
//                                    MktCamChlConfDO mktCamChlConf = mktCamChlConfMapper.selectByPrimaryKey(evtContactConfId);
//
//                                    //返回渠道基本信息
//                                    recommend.put("channelId", mktCamChlConf.getContactChlId());
//                                    recommend.put("pushType", mktCamChlConf.getPushType());
//                                    recommend.put("pushContent", ""); //todo 不明
//
//                                    //返回结果中添加销售品信息
//                                    recommend.put("productList", productList);
//
//
//                                    //查询渠道子策略 这里老系统暂时不返回
////                                    List<MktVerbalCondition> mktVerbalConditions = mktVerbalConditionMapper.findConditionListByVerbalId(evtContactConfId);
//
//                                    //查询脚本
//                                    CamScript camScript = mktCamScriptMapper.selectByConfId(evtContactConfId);
//                                    if (camScript != null) {
//                                        recommend.put("reason", camScript.getScriptDesc());
//                                    }
//
//                                    //查询话术
//                                    List<MktVerbal> mktVerbals = mktVerbalMapper.findVerbalListByConfId(evtContactConfId);
//                                    if (mktVerbals != null && mktVerbals.size() > 0) {
//                                        //todo  多个如何返回
//                                        recommend.put("keyNote", mktVerbals.get(0).getScriptDesc());
//                                    }
//
//                                    if (mktVerbals != null && mktVerbals.size() > 0) {
//                                        for (MktVerbal mktVerbal : mktVerbals) {
//                                            //查询话术规则
//                                            List<MktVerbalCondition> channelConditionList = mktVerbalConditionMapper.findChannelConditionListByVerbalId(mktVerbal.getVerbalId());
//                                            //todo 格式化话术规则 如何返回
//
//                                        }
//                                    }
//                                    recommend.put("verbal", ""); //todo 待定
//
//                                    recommendList.add(recommend);
//                                }
//                            }
//                        }
//                        order.put("recommendList", recommendList);
//                    } else {
//                        //规则为空
//                        System.out.println("规则为空");
//                    }
//                }
//            } else {
//                //策略为空
//                System.out.println("策略为空");
//            }
//            orderList.add(order);
//        }
//
//        //返回结果
//        result.put("orderList", orderList);
//
//        return result;
//    }

//    @Override
//    @SuppressWarnings("unchecked")
//    public Map deal(Map<String, Object> map) {
//        //初始化返回结果
//        Map<String, Object> result = new HashMap();
//
//        //获取当前时间
//        Date now = new Date();
//
//        //获取事件code
//        String eventNbr = (String) map.get("eventId");
//        //获取流水号
//        String ISI = (String) map.get("ISI");
//
//        //从map中取出参数
////        EventReportDTO eventReportDTO = new EventReportDTO();
////        eventReportDTO.setEventId((String) map.get("eventId")); //事件code
////        eventReportDTO.setC4((String) map.get("C4")); //C4代码
////        eventReportDTO.setChannelId((String) map.get("channelId")); //渠道
////        eventReportDTO.setISI((String) map.get("ISI")); //流水号
////        eventReportDTO.setLanId(Long.valueOf((String) map.get("lanId"))); //本地网标识
//
//        //构造返回参数
//        result.put("custId", "客户编码");
//        result.put("CPCResultCode", "1");
//        result.put("CPCResultMsg", "success");
//        result.put("ISI", ISI);
//
//        //获取标签因子集合
//        List<Map<String, Object>> labelList = (List<Map<String, Object>>) map.get("triggers");
//
//        //根据事件code查询事件信息
//        ContactEvt event = contactEvtMapper.getEventByEventNbr(eventNbr);
//
//        //获取事件id
//        Long eventId = event.getContactEvtId();
//        //获取事件推荐活动数
//        int recCampaignAmount;
//        String recCampaignAmountStr = event.getRecCampaignAmount();
//        if (recCampaignAmountStr == null || "".equals(recCampaignAmountStr)) {
//            recCampaignAmount = 0;
//        } else {
//            recCampaignAmount = Integer.parseInt(recCampaignAmountStr);
//        }
//
//        //获取事件过滤规则
//        ContactEvtMatchRul contactEvtMatchRulParam = new ContactEvtMatchRul();
//        contactEvtMatchRulParam.setContactEvtId(eventId);
//        List<ContactEvtMatchRul> contactEvtMatchRuls = contactEvtMatchRulMapper.listEventMatchRuls(contactEvtMatchRulParam);
//        //遍历事件过滤规则匹配
//        if (contactEvtMatchRuls != null && contactEvtMatchRuls.size() > 0) {
//            //匹配事件过滤规则
//            int flag = 0;
//            for (ContactEvtMatchRul contactEvtMatchRul : contactEvtMatchRuls) {
//                flag = userListMapper.checkRule("", contactEvtMatchRul.getEvtMatchRulId(), null);
//                if (flag > 0) {
//                    result.put("CPCResultCode", "0");
//                    result.put("CPCResultMsg", "事件过滤拦截");
//                    return result;
//                }
//            }
//        }
//
//        //根据事件id 查询所有关联活动（根据优先级排序 正序）
//        List<Long> activityIds = mktCamEvtRelMapper.listActivityByEventId(eventId);
//
//        //初始化返回结果中的工单信息
//        List<Map<String, Object>> orderList = new ArrayList<>();
//
//        //遍历活动id  查询并匹配活动规则 需要根据事件推荐活动数 取前n个活动
//        int max = activityIds.size();
//        if (recCampaignAmount != 0) {
//            //事件推荐活动数
//            if (activityIds.size() > recCampaignAmount) {
//                max = recCampaignAmount;
//            }
//        }
//
//
//        //初始化结果集
//        List<Future<Map<String, Object>>> threadList = new ArrayList<>();
//        //初始化线程池
//        ExecutorService executorService = Executors.newCachedThreadPool();
//
//        //遍历活动
//        for (int j = 0; j < max; j++) {
//
//            //活动id
//            Long activityId = activityIds.get(j);
//            //提交线程
//            Future<Map<String, Object>> f = executorService.submit(new ActivityTask(ISI, activityId));
//            //将线程处理结果添加到结果集
//            threadList.add(f);
//        }
//
//
//
//        //获取结果
//        try {
//            for (Future<Map<String, Object>> future : threadList) {
//                orderList.add(future.get());
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            //发生异常关闭线程池
//            executorService.shutdown();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//            //发生异常关闭线程池
//            executorService.shutdown();
//            return null;
//        }
//
//        //关闭线程池
//        executorService.shutdown();
//
//        //返回结果
//        result.put("orderList", orderList);
//
//        return result;
//    }

//    @Override
//    public void cpc() {
//
//    }
    @Override
    @SuppressWarnings("unchecked")
    public Map deal(Map<String, Object> map) {
        long begin = System.currentTimeMillis();
        //初始化返回结果
        Map<String, Object> result = new HashMap();
        //获取事件code
        String eventNbr = (String) map.get("eventId");
        //获取流水号
        String ISI = (String) map.get("ISI");
        //c3
        String c3 = (String) map.get("C3");
        //本地网
        String lanId = (String) map.get("lanId");

        //构造返回参数
        result.put("custId", "客户编码");
        result.put("CPCResultCode", "1");
        result.put("CPCResultMsg", "success");
        result.put("ISI", ISI);

        //获取标签因子集合
        List<Map<String, Object>> labelList = (List<Map<String, Object>>) map.get("triggers");

        //获取资产编码号，资产号
        String integrationId = "";  //资产集成编码
        String accNbr = "";   //资产号
        for (Map<String, Object> label : labelList) {
            Map<String, String> lab = (Map<String, String>) label.get("trigger");
            if ("INTEGRATION_ID".equals(lab.get("key"))) {
                integrationId = lab.get("value");
                System.out.println("INTEGRATION_ID=" + integrationId);
            }
            if ("ACC_NBR".equals(lab.get("key"))) {
                accNbr = lab.get("value");
                System.out.println("ACC_NBR=" + accNbr);
            }
        }

        //根据事件code查询事件信息
        ContactEvt event = contactEvtMapper.getEventByEventNbr(eventNbr);
        //获取事件id
        Long eventId = event.getContactEvtId();
        //获取事件推荐活动数
        int recCampaignAmount;
        String recCampaignAmountStr = event.getRecCampaignAmount();
        if (recCampaignAmountStr == null || "".equals(recCampaignAmountStr)) {
            recCampaignAmount = 0;
        } else {
            recCampaignAmount = Integer.parseInt(recCampaignAmountStr);
        }
        //获取事件过滤规则
//        ContactEvtMatchRul contactEvtMatchRulParam = new ContactEvtMatchRul();
//        contactEvtMatchRulParam.setContactEvtId(eventId);
//        List<ContactEvtMatchRul> contactEvtMatchRuls = contactEvtMatchRulMapper.listEventMatchRuls(contactEvtMatchRulParam);
//        //遍历事件过滤规则匹配
//        if (contactEvtMatchRuls != null && contactEvtMatchRuls.size() > 0) {
//            //匹配事件过滤规则
//            int flag = 0;
//            for (ContactEvtMatchRul contactEvtMatchRul : contactEvtMatchRuls) {
//                flag = userListMapper.checkRule("", contactEvtMatchRul.getEvtMatchRulId(), null);
//                if (flag > 0) {
//                    result.put("CPCResultCode", "0");
//                    result.put("CPCResultMsg", "事件过滤拦截");
//                    return result;
//                }
//            }
//        }
        //根据事件id 查询所有关联活动（根据优先级排序 正序）
        List<Long> activityIds = mktCamEvtRelMapper.listActivityByEventId(eventId);
        //初始化返回结果中的工单信息
        List<Map<String, Object>> activityList = new ArrayList<>();

        //遍历活动id  查询并匹配活动规则 需要根据事件推荐活动数 取前n个活动
        int max = activityIds.size();
        if (recCampaignAmount != 0) {
            //事件推荐活动数
            if (activityIds.size() > recCampaignAmount) {
                max = recCampaignAmount;
            }
        }
        //初始化结果集
        List<Future<Map<String, Object>>> threadList = new ArrayList<>();
        //初始化线程池
        ExecutorService executorService = Executors.newCachedThreadPool();

        //遍历活动
        for (int j = 0; j < max; j++) {
            //活动id
            Long activityId = activityIds.get(j);
            //提交线程
            Future<Map<String, Object>> f = executorService.submit(new ActivityTask(ISI, activityId, eventNbr, integrationId, accNbr, c3));
            //将线程处理结果添加到结果集
            threadList.add(f);
        }
        //获取结果
        try {
            for (Future<Map<String, Object>> future : threadList) {
                if (!future.get().isEmpty()) {
                    activityList.add(future.get());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            //发生异常关闭线程池
            executorService.shutdown();
        } catch (ExecutionException e) {
            e.printStackTrace();
            //发生异常关闭线程池
            executorService.shutdown();
            return Collections.EMPTY_MAP;
        }
        //关闭线程池
        executorService.shutdown();
        long cost = System.currentTimeMillis() - begin;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("timeCost", cost);
        jsonObject.put("ISI", ISI);
        jsonObject.put("eventId", eventId);
        jsonObject.put("activityList", activityList);
        jsonObject.put("integrationId", integrationId);
        jsonObject.put("accNbr", accNbr);
        esService.save(jsonObject, IndexList.EVENT_MODULE);
        //返回结果
        result.put("activityList", activityList); //协同回调结果
        return result;
    }

    /**
     * 获取策略列表
     */
    class ActivityTask implements Callable<Map<String, Object>> {
        private String ISI;
        private String c3;
        private Long activityId;
        private String eventId;
        private String integrationId;
        private String accNbr;

        public ActivityTask(String ISI, Long activityId, String eventId, String integrationId, String accNbr, String c3) {
            this.ISI = ISI;
            this.c3 = c3;
            this.activityId = activityId;
            this.eventId = eventId;
            this.integrationId = integrationId;
            this.accNbr = accNbr;
        }

        @Override
        public Map<String, Object> call() {
            Map<String, Object> activity = new HashMap<>();
            List<Map<String, Object>> strageyList = new ArrayList<>();
            //查询活动基本信息
            MktCampaignDO mktCampaign = mktCampaignMapper.selectByPrimaryKey(activityId);
            //返回参数中添加活动信息
            activity.put("ISI", ISI);
            activity.put("activityId", mktCampaign.getMktCampaignId().toString());
            activity.put("activityName", mktCampaign.getMktCampaignName());
            activity.put("activityCode", mktCampaign.getMktActivityNbr());
            activity.put("skipCheck", "0");  //todo 不明 案例上有 文档上没有
            activity.put("orderPriority", "100");  //todo 不明 案例上有 文档上没有


            //根据活动id获取策略列表
            List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOs = mktCamStrategyConfRelMapper.selectByMktCampaignId(activityId);

            //初始化结果集
            List<Future<Map<String, Object>>> threadList = new ArrayList<>();
            //初始化线程池
            ExecutorService executorService = Executors.newCachedThreadPool();
            //遍历策略列表
            for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOs) {
                //提交线程
                Future<Map<String, Object>> f = executorService.submit(new StrategyTask(mktCamStrategyConfRelDO.getStrategyConfId(), ISI, c3, String.valueOf(activityId), eventId, integrationId, accNbr));
                //将线程处理结果添加到结果集
                threadList.add(f);
            }
            //获取结果
            try {
                for (Future<Map<String, Object>> future : threadList) {
                    if (!future.get().isEmpty()) {
                        strageyList.add(future.get());
                    }
                }
                activity.put("strategyList", strageyList);
                //todo 其他字段
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("orderISI", ISI);
                jsonObject.put("integrationId", integrationId);
                jsonObject.put("accNbr", accNbr);
                jsonObject.put("activityId", mktCampaign.getMktCampaignId().toString());
                jsonObject.put("activityName", mktCampaign.getMktCampaignName());
                jsonObject.put("activityCode", mktCampaign.getMktActivityNbr());
                jsonObject.put("strategyList", activity.get("strategyList"));
                esService.save(jsonObject, IndexList.ACTIVITY_MODULE);
            } catch (Exception e) {
                e.printStackTrace();

                //发生异常关闭线程池
                executorService.shutdown();
            } /*catch (ExecutionException e) {
                e.printStackTrace();
                //发生异常关闭线程池
                //  executorService.shutdown();
                //  return null;
            }*/
            //关闭线程池
            executorService.shutdown();
            return activity;
        }
    }

    /**
     * 获取返回规则列表
     */
    class StrategyTask implements Callable<Map<String, Object>> {
        private Long strategyConfId; //策略配置id
        private String ISI;
        private String c3;
        private String activityId;
        private String eventId;
        private String integrationId;
        private String accNbr;


        public StrategyTask(Long strategyConfId, String ISI, String c3, String activityId, String eventId, String integrationId, String accNbr) {
            this.strategyConfId = strategyConfId;
            this.ISI = ISI;
            this.c3 = c3;
            this.activityId = activityId;
            this.eventId = eventId;
            this.integrationId = integrationId;
            this.accNbr = accNbr;
        }

        @Override
        public Map<String, Object> call() {
            //获取当前时间
            Date now = new Date();
            Map<String, Object> strategyMap = new HashMap<>();
            //初始化返回结果中的推荐信息列表
            List<Map<String, Object>> ruleList = new ArrayList<>();
            //查询策略基本信息
            MktStrategyConfDO mktStrategyConf = mktStrategyConfMapper.selectByPrimaryKey(strategyConfId);
            // 获取策略名称
            String strategyConfName = mktStrategyConf.getMktStrategyConfName();
            strategyMap.put("mktStrategyConfName", strategyConfName);
            //验证策略生效时间
            if (!(now.after(mktStrategyConf.getBeginTime()) && now.before(mktStrategyConf.getEndTime()))) {
                //若当前时间在策略生效时间外
                return Collections.EMPTY_MAP;
            }
            //todo 下发地市
            //todo 判断下发渠道
            //根据策略id获取策略下发规则列表
            List<MktStrategyConfRuleDO> mktStrategyConfRuleDOS = mktStrategyConfRuleMapper.selectByMktStrategyConfId(strategyConfId);
            //遍历规则↓↓↓↓↓↓↓↓↓↓
            //初始化结果集
            List<Future<Map<String, Object>>> threadList = new ArrayList<>();
            //初始化线程池
            ExecutorService executorService = Executors.newCachedThreadPool();
            //遍历规则列表
            if (mktStrategyConfRuleDOS != null && mktStrategyConfRuleDOS.size() > 0) {
                for (MktStrategyConfRuleDO mktStrategyConfRuleDO : mktStrategyConfRuleDOS) {
                    //获取分群id
                    Long tarGrpId = mktStrategyConfRuleDO.getTarGrpId();
                    //获取销售品
                    String productStr = mktStrategyConfRuleDO.getProductId();
                    //过滤规则id
                    Long ruleConfId = mktStrategyConfRuleDO.getRuleConfId();
                    //协同渠道配置id
                    String evtContactConfIdStr = mktStrategyConfRuleDO.getEvtContactConfId();

                    Long mktStrategyConfRuleId = mktStrategyConfRuleDO.getMktStrategyConfRuleId();

                    String mktStrategyConfRuleName = mktStrategyConfRuleDO.getMktStrategyConfRuleName();
                    //提交线程
                    Future<Map<String, Object>> f = executorService.submit(new RuleTask(strategyConfId, ISI, c3, tarGrpId, productStr, ruleConfId, evtContactConfIdStr, eventId, activityId, mktStrategyConfRuleId, mktStrategyConfRuleName, integrationId, accNbr));
                    //将线程处理结果添加到结果集
                    threadList.add(f);
                }
            }
            //获取结果
            try {
                for (Future<Map<String, Object>> future : threadList) {
                    if (!future.get().isEmpty()) {
                        ruleList.add(future.get());
                    }
                }
                strategyMap.put("ruleList", ruleList);
            } catch (InterruptedException e) {
                e.printStackTrace();
                //发生异常关闭线程池
                executorService.shutdown();
            } catch (ExecutionException e) {
                e.printStackTrace();
                //发生异常关闭线程池
                executorService.shutdown();
                // return null;
            }
            //关闭线程池
            executorService.shutdown();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("activityId", activityId);
            jsonObject.put("strategyConfId", strategyConfId);
            jsonObject.put("strategyConfName", strategyConfName);
            jsonObject.put("eventId", eventId);
            jsonObject.put("ISI", ISI);
            jsonObject.put("ruleList", ruleList);
            jsonObject.put("integrationId", integrationId);
            jsonObject.put("accNbr", accNbr);
            esService.save(jsonObject, IndexList.STRATEGY_MODULE);
            return strategyMap;
        }
    }

    /**
     * 获取规则列表
     */
    class RuleTask implements Callable<Map<String, Object>> {
        private Long strategyConfId; //策略配置id
        private Long tarGrpId;
        private String productStr;
        private Long ruleConfId;
        private String evtContactConfIdStr;
        private String ISI;
        private String c3;
        private String eventId;
        private String activityId;
        private String integrationId;
        private String accNbr;
        private Long ruleId;
        private String ruleName;

        public RuleTask(Long strategyConfId, String ISI, String c3, Long tarGrpId, String productStr, Long ruleConfId, String evtContactConfIdStr, String eventId, String activityId, Long mktStrategyConfRuleId, String mktStrategyConfRuleName, String integrationId, String accNbr) {
            this.strategyConfId = strategyConfId;
            this.tarGrpId = tarGrpId;
            this.ISI = ISI;
            this.c3 = c3;
            this.productStr = productStr;
            this.ruleConfId = ruleConfId;
            this.evtContactConfIdStr = evtContactConfIdStr;
            this.activityId = activityId;
            this.eventId = eventId;
            this.ruleId = mktStrategyConfRuleId;
            this.ruleName = mktStrategyConfRuleName;
            this.integrationId = integrationId;
            this.accNbr = accNbr;
        }

        @Override
        public Map<String, Object> call() throws Exception {
            Map<String, Object> ruleMap = new HashMap<>();
            //初始化返回结果中的推荐信息列表
            List<Map<String, Object>> channelList = new ArrayList<>();
            //  1.判断过滤规则---------------------------
            //获取过滤规则
//            FilterRuleConfDO filterRuleConfDO = filterRuleConfMapper.selectByPrimaryKey(ruleConfId);
//            String ruleConfIdStr = filterRuleConfDO.getFilterRuleIds();
//            if (ruleConfIdStr != null) {
//                String[] array = ruleConfIdStr.split(",");
//                boolean ruleFilter = true;
//                for (String str : array) {
//                    //获取具体规则
//                    FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(Long.parseLong(str));
//
//                    //匹配事件过滤规则
//                    int flag = 0;
//                    if (filterRule != null) {
//                        flag = userListMapper.checkRule("", filterRule.getRuleId(), null);
//                        if (flag > 0) {
//                            ruleFilter = false;
//                        }
//                    }
//                }
//                //若存在不符合的规则 结束当前规则循环
//                if (!ruleFilter) {
//                    return Collections.EMPTY_MAP;
//                }
//            }
            //  2.判断客户分群规则---------------------------
            //判断匹配结果，如匹配则向下进行，如不匹配则continue结束本次循环
            //拼装redis key
            String key = "EVENT_RULE_" + activityId + "_" + strategyConfId + "_" + ruleId;

            ExpressRunner runner = new ExpressRunner();
            DefaultContext<String, Object> context = new DefaultContext<String, Object>();

            //查询标签实例数据
            String httpResultStr;
            String url = "http://134.96.216.156:8110/in"; //标签查询地址
            //构造查询参数值
            JSONObject param = new JSONObject();
            //查询标识
            param.put("reqId", "ISI_0605202636_887_44_732");
            param.put("queryNum", accNbr);
            param.put("c3", c3);
            param.put("queryId", integrationId);
            //查询标签列表
            Map<String, String> queryFields = new HashMap<>();
//            String labelsStr = (String) redisUtils.get(key + "label");
            List<LabelResult> labelResultList = (List<LabelResult>) redisUtils.get(key + "_LABEL");
            if (labelResultList == null || labelResultList.size() <= 0) {
                //redis中没有，从数据库查询标签
                List<TarGrpCondition> tarGrpConditionDOs = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
                //遍历所有规则
                for (int i = 1; i <= tarGrpConditionDOs.size(); i++) {
                    Label label = injectionLabelMapper.selectByPrimaryKey(Long.parseLong(tarGrpConditionDOs.get(i).getLeftParam()));
                    queryFields.put(String.valueOf(i), label.getInjectionLabelCode());
                }
            } else {
                //redis中获取标签
                for (int i = 1; i <= labelResultList.size(); i++) {
                    queryFields.put(String.valueOf(i), labelResultList.get(i - 1).getLabelCode());
                }
            }
//            queryFields.put("1", "FREE_CALL_TYPE");
//            queryFields.put("2", "PROM_AGREE_EXP_MONS");
//            queryFields.put("3", "IS_FREE_CALL");
            param.put("queryFields", queryFields);

            String paramStr = param.toString();
            System.out.println("param " + param.toString());
            //验证post回调结果
            httpResultStr = HttpUtil.post(url, paramStr);
            if (httpResultStr == null || "".equals(httpResultStr)) {
                System.out.println("查询标签出错");
            }
            JSONObject jsonobj = new JSONObject();
            //解析返回结果
            JSONObject httpResult = JSONObject.parseObject(httpResultStr);
            if (httpResult.getInteger("result_code") == 0) {
                JSONObject body = httpResult.getJSONObject("msgbody");
                //ES log 标签实例

                jsonobj.put("ISI", ISI);
                jsonobj.put("eventId", eventId);
                jsonobj.put("activityId", activityId);
                if (ruleConfId != null) {
                    jsonobj.put("ruleConfId", ruleConfId);
                }
                jsonobj.put("ruleId", ruleId);
                jsonobj.put("integrationId", integrationId);
                jsonobj.put("accNbr", accNbr);
                jsonobj.put("strategyConfId", strategyConfId);
                //todo 存标签
                //jsonObject.put("target", body);
                //jsonobj.put("labelResultList", JSONArray.toJSON(labelResultList));

                //拼接规则引擎上下文
                for (Map.Entry<String, Object> entry : body.entrySet()) {
                    //添加到时上下文
                    context.put(entry.getKey(), entry.getValue());
                }
                context.put("PROM_TYPE", "乐享4G套餐");
                System.out.println("查询标签成功:" + context.toString());
            } else {
                System.out.println("查询标签失败:" + httpResult.getString("result_msg"));
            }


            //判断redis中是否存在
            String express = "";
            if (redisUtils.exists(key)) {
                express = (String) redisUtils.get(key);
            } else {
                //若redis中不存在key，则从数据库中查询并拼装表达式
                //查询分群规则list
                List<TarGrpCondition> tarGrpConditionDOs = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
                //将规则拼装为表达式
                StringBuilder expressSb = new StringBuilder();
                expressSb.append("if(");
                //遍历所有规则
                for (int i = 0; i < tarGrpConditionDOs.size(); i++) {


                    String type = tarGrpConditionDOs.get(i).getOperType();
                    StringBuilder express1 = new StringBuilder();
                    Label label = injectionLabelMapper.selectByPrimaryKey(Long.parseLong(tarGrpConditionDOs.get(i).getLeftParam()));
                    if ("7100".equals(type)) {
                        expressSb.append("!");
                    }
                    expressSb.append("((");

                    express1.append("if(");
                    express1.append("(");

                    expressSb.append(label.getInjectionLabelCode()).append(")");
                    express1.append(label.getInjectionLabelCode()).append(")");
//                    expressSb.append("\"").append(label.getInjectionLabelCode()).append("\"");
                    if ("1000".equals(type)) {
                        expressSb.append(" > ");
                        express1.append(" > ");
                    } else if ("2000".equals(type)) {
                        expressSb.append(" < ");
                        express1.append(" < ");
                    } else if ("3000".equals(type)) {
                        expressSb.append(" == ");
                        express1.append(" == ");
                    } else if ("4000".equals(type)) {
                        expressSb.append(" != ");
                        express1.append(" != ");
                    } else if ("5000".equals(type)) {
                        expressSb.append(" >= ");
                        express1.append(" >= ");
                    } else if ("6000".equals(type)) {
                        expressSb.append(" <= ");
                        express1.append(" <= ");
                    } else if ("7000".equals(type) || "7100".equals(type)) {
                        expressSb.append(" in ");
                        express1.append(" in ");
                    }

                    if ("7000".equals(type) || "7100".equals(type)) {
                        String[] strArray = tarGrpConditionDOs.get(i).getRightParam().split(",");
                        expressSb.append("(");
                        express1.append("(");
                        for (int j = 0; j < strArray.length; j++) {
                            expressSb.append("\"").append(strArray[j]).append("\"");
                            express1.append("\"").append(strArray[j]).append("\"");
                            if (j != strArray.length - 1) {
                                expressSb.append(",");
                                express1.append(",");
                            }
                        }
                        expressSb.append(")");
                        express1.append(")");
//                        expressSb.append("(").append("\"").append(tarGrpConditionDOs.get(i).getRightParam()).append("\"").append(")");//  真实值
                    } else {
//                        expressSb.append(tarGrpConditionDOs.get(i).getRightParam());//  真实值
                        expressSb.append("\"").append(tarGrpConditionDOs.get(i).getRightParam()).append("\"");//  真实值
                        express1.append("\"").append(tarGrpConditionDOs.get(i).getRightParam()).append("\"");//  真实值
                    }


                    expressSb.append(")");
                    express1.append(") {return true} else {return false}");
                    System.out.println(express1.toString());
                    RuleResult ruleResult1 = runner.executeRule(express1.toString(), context, true, true);

                    for (LabelResult labelResult : labelResultList) {
                        if (label.getInjectionLabelCode().equals(labelResult.getLabelCode())) {

                            if (null != ruleResult1.getResult()) {
                                labelResult.setResult((Boolean) ruleResult1.getResult());
                            } else {
                                labelResult.setResult(false);
                            }

                        }
                    }
                    jsonobj.put("labelResultList", JSONArray.toJSON(labelResultList));
//
// executeSingleExpress(tarGrpConditionDOs,
//                            context, runner, i, type,
//                            label, express);
                    if (i + 1 != tarGrpConditionDOs.size()) {
                        expressSb.append("&&");
                    }
                }
                expressSb.append(") {return true} else {return false}");
                express = expressSb.toString();
            }

            esService.save(jsonobj, IndexList.Label_MODULE);
            try {
                //规则引擎计算
                System.out.println(express);
                RuleResult ruleResult = runner.executeRule(express, context, true, true);


//                RuleResult ruleResult = runner.executeRule(express, context, true, true);

                System.out.println("result=" + ruleResult.getResult());
                System.out.println("Tree=" + ruleResult.getRule().toTree());
                System.out.println("TraceMap=" + ruleResult.getTraceMap());
                //初始化返回结果中的销售品条目
                List<Map<String, String>> productList = new ArrayList<>();
                if (ruleResult != null) {
                    //查询销售品列表
                    if (productStr!=null && !"".equals(productStr)){
                        String[] productArray = productStr.split(",");
                        for (String str : productArray) {
                            Map<String, String> product = new HashMap<>();
                            PpmProduct ppmProduct = ppmProductMapper.selectByPrimaryKey(Long.parseLong(str));
                            product.put("productCode", ppmProduct.getProductCode());
                            product.put("productFlag", ""); //todo 不明 案例上有 文档上没有
                            product.put("productAlias", ""); //todo 不明 案例上有 文档上没有
                            product.put("productName", ppmProduct.getProductName());
                            product.put("productType", ppmProduct.getProductType());
                            System.out.println("*********************product --->>>" + JSON.toJSON(product));
                            productList.add(product);
                        }
                    }

                    if (ruleResult.getResult() == null) {
                        ruleResult.setResult(false);
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("express", express);
                    jsonObject.put("ISI", ISI);
                    jsonObject.put("eventId", eventId);
                    jsonObject.put("activityId", activityId);
                    jsonObject.put("ruleConfId", ruleConfId);
                    jsonObject.put("strategyConfId", strategyConfId);
                    jsonObject.put("ruleId", ruleId);
                    jsonObject.put("ruleName", ruleName);
                    jsonObject.put("productStr", productStr);
                    jsonObject.put("evtContactConfIdStr", evtContactConfIdStr);
                    jsonObject.put("tarGrpId", tarGrpId);
                    jsonObject.put("result", ruleResult.getResult()); //看是否命中
                    jsonObject.put("productList", productList);
                    esService.save(jsonObject, IndexList.RULE_MODULE);
                    //获取协同渠道所有id
                    String[] evtContactConfIdArray = evtContactConfIdStr.split("/");
                    //初始化结果集
                    List<Future<Map<String, Object>>> threadList = new ArrayList<>();
                    //初始化线程池
                    ExecutorService executorService = Executors.newCachedThreadPool();
                    //遍历协同渠道
                    for (String str : evtContactConfIdArray) {
                        //协同渠道规则表id（自建表）
                        Long evtContactConfId = Long.parseLong(str);
                        //提交线程
                        Future<Map<String, Object>> f = executorService.submit(new ChannelTask(evtContactConfId, productList));
                        //将线程处理结果添加到结果集
                        threadList.add(f);
                    }
                    //获取结果
                    try {
                        for (Future<Map<String, Object>> future : threadList) {
                            if (!future.get().isEmpty()) {
                                channelList.add(future.get());
                            }
                        }
                        ruleMap.put("channelList", channelList);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        //发生异常关闭线程池
                        executorService.shutdown();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        //发生异常关闭线程池
                        executorService.shutdown();
                    }
                    //关闭线程池
                    executorService.shutdown();
                } else {
                    //判断失败 返回
                    return Collections.EMPTY_MAP;
                }
            } catch (Exception e) {
                e.printStackTrace();
                //todo 异常处理
            }
            return ruleMap;
        }

        private void executeSingleExpress(List<TarGrpCondition> tarGrpConditionDOs, DefaultContext context, ExpressRunner runner, int i, String type, Label label, String express) {
            StringBuilder singleExpress = new StringBuilder();
            singleExpress.append("if(");
            singleExpress.append("(");
            singleExpress.append(label.getInjectionLabelCode());
            if ("1000".equals(type)) {
                singleExpress.append(">");
            } else if ("2000".equals(type)) {
                singleExpress.append("<");
            } else if ("3000".equals(type)) {
                singleExpress.append("==");
            } else if ("4000".equals(type)) {
                singleExpress.append("!=");
            } else if ("5000".equals(type)) {
                singleExpress.append(">=");
            } else if ("6000".equals(type)) {
                singleExpress.append("<=");
            }
            singleExpress.append(tarGrpConditionDOs.get(i).getRightParam());
            singleExpress.append(")");
            singleExpress.append(") {return true} else {return false}");
            try {
                RuleResult ruleResult = runner.executeRule(singleExpress.toString(), context, true, true);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("express", express);
                jsonObject.put("ISI", ISI);
                jsonObject.put("eventId", eventId);
                jsonObject.put("activityId", activityId);
                jsonObject.put("ruleConfId", ruleConfId);
                jsonObject.put("strategyConfId", strategyConfId);
                jsonObject.put("productStr", productStr);
                jsonObject.put("evtContactConfIdStr", evtContactConfIdStr);
                jsonObject.put("tarGrpId", tarGrpId);
                jsonObject.put("result", ruleResult.getResult()); //看是否命中
                jsonObject.put("labelCode", label.getInjectionLabelCode());
                jsonObject.put("labelName", label.getInjectionLabelName());
                esService.save(jsonObject, IndexList.Label_MODULE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class ChannelTask implements Callable<Map<String, Object>> {

        //策略配置id
        private Long evtContactConfId;

        private List<Map<String, String>> productList;

        public ChannelTask(Long evtContactConfId, List<Map<String, String>> productList) {
            this.evtContactConfId = evtContactConfId;
            this.productList = productList;
        }

        @Override
        public Map<String, Object> call() {
            Date now = new Date();
            //初始化返回结果推荐信息
            Map<String, Object> recommend = new HashMap<>();
            //查询渠道属性
            List<MktCamChlConfAttrDO> mktCamChlConfAttrs = mktCamChlConfAttrMapper.selectByEvtContactConfId(evtContactConfId);
            boolean checkTime = true;
            for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrs) {
                //判断渠道生失效时间
                if (mktCamChlConfAttrDO.getAttrId() == 1000L) {
                    if (!now.after(new Date(Long.parseLong(mktCamChlConfAttrDO.getAttrValue())))) {
                        checkTime = false;
                    }
                }
                if (mktCamChlConfAttrDO.getAttrId() == 1001L) {
                    if (now.after(new Date(Long.parseLong(mktCamChlConfAttrDO.getAttrValue())))) {
                        checkTime = false;
                    }
                }
            }

            if (!checkTime) {
                return Collections.EMPTY_MAP;
            }

            //查询渠道信息基本信息
            MktCamChlConfDO mktCamChlConf = mktCamChlConfMapper.selectByPrimaryKey(evtContactConfId);

            //返回渠道基本信息
            recommend.put("channelId", mktCamChlConf.getContactChlId());
            recommend.put("pushType", mktCamChlConf.getPushType());
            recommend.put("pushContent", ""); //todo 不明

            //返回结果中添加销售品信息
            recommend.put("productList", productList);
            //查询渠道子策略 这里老系统暂时不返回
//              List<MktVerbalCondition> mktVerbalConditions = mktVerbalConditionMapper.findConditionListByVerbalId(evtContactConfId);
            //查询脚本
            CamScript camScript = mktCamScriptMapper.selectByConfId(evtContactConfId);
            if (camScript != null) {
                recommend.put("reason", camScript.getScriptDesc());
            }
            //查询话术
            List<MktVerbal> mktVerbals = mktVerbalMapper.findVerbalListByConfId(evtContactConfId);
            if (mktVerbals != null && mktVerbals.size() > 0) {
                //todo  多个如何返回
                recommend.put("keyNote", mktVerbals.get(0).getScriptDesc());
            }
            if (mktVerbals != null && mktVerbals.size() > 0) {
                for (MktVerbal mktVerbal : mktVerbals) {
                    //查询话术规则
                    List<MktVerbalCondition> channelConditionList = mktVerbalConditionMapper.findChannelConditionListByVerbalId(mktVerbal.getVerbalId());
                    //todo 格式化话术规则 如何返回  可能需要判断规则
                }
            }
            recommend.put("verbal", ""); //todo 待定

            return recommend;
        }
    }
}
