package com.zjtelcom.cpct.dubbo.service.impl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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


    /**
     * 事件触发接口实现
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map deal(Map<String, Object> map) throws Exception {
        //初始化返回结果
        Map<String, Object> result = new HashMap();

        //获取当前时间
        Date now = new Date();

        //获取事件code
        String eventNbr = (String) map.get("eventId");
        //获取流水号
        String ISI = (String) map.get("ISI");

        //从map中取出参数
//        EventReportDTO eventReportDTO = new EventReportDTO();
//        eventReportDTO.setEventId((String) map.get("eventId")); //事件code
//        eventReportDTO.setC4((String) map.get("C4")); //C4代码
//        eventReportDTO.setChannelId((String) map.get("channelId")); //渠道
//        eventReportDTO.setISI((String) map.get("ISI")); //流水号
//        eventReportDTO.setLanId(Long.valueOf((String) map.get("lanId"))); //本地网标识

        //构造返回参数
        result.put("custId", "客户编码");
        result.put("CPCResultCode", "1");
        result.put("CPCResultMsg", "success");
        result.put("ISI", ISI);

        //获取标签因子集合
        List<Map<String, Object>> labelList = (List<Map<String, Object>>) map.get("triggers");

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
        ContactEvtMatchRul contactEvtMatchRulParam = new ContactEvtMatchRul();
        contactEvtMatchRulParam.setContactEvtId(eventId);
        List<ContactEvtMatchRul> contactEvtMatchRuls = contactEvtMatchRulMapper.listEventMatchRuls(contactEvtMatchRulParam);
        //遍历事件过滤规则匹配
        if (contactEvtMatchRuls != null && contactEvtMatchRuls.size() > 0) {
            //匹配事件过滤规则
            int flag = 0;
            for (ContactEvtMatchRul contactEvtMatchRul : contactEvtMatchRuls) {
                flag = userListMapper.checkRule("", contactEvtMatchRul.getEvtMatchRulId(), null);
                if (flag > 0) {
                    result.put("CPCResultCode", "0");
                    result.put("CPCResultMsg", "事件过滤拦截");
                    return result;
                }
            }
        }

        //根据事件id 查询所有关联的事件场景
//        EventSceneDO param = new EventSceneDO();
//        param.setEventId(eventId);
//        List<EventScene> eventScenes = eventSceneMapper.qryEventSceneByEvtId(eventId);

        //循环事件场景列表 根据事件场景获取活动列表
//        List<Long> activityIds = new ArrayList<>(); //活动id list
//        for (EventScene eventScene : eventScenes) {
//            List<EvtSceneCamRel> evtSceneCamRels = evtSceneCamRelMapper.selectCamsByEvtSceneId(eventScene.getEventSceneId());
//            if (evtSceneCamRels != null && evtSceneCamRels.size() > 0) {
//                for (EvtSceneCamRel evtSceneCamRel : evtSceneCamRels) {
//                    activityIds.add(evtSceneCamRel.getMktCampaignId());
//                }
//            }
//        }

        //根据事件id 查询所有关联活动（根据优先级排序 正序）
        List<Long> activityIds = mktCamEvtRelMapper.listActivityByEventId(eventId);

        //初始化返回结果中的工单信息
        List<Map<String, Object>> orderList = new ArrayList<>();

        //遍历活动id  查询并匹配活动规则 需要根据事件推荐活动数 取前n个活动
        int max = activityIds.size();
        if (recCampaignAmount != 0) {
            //事件推荐活动数
            max = recCampaignAmount;
        }
        for (int j = 0; j < max; j++) {
            //活动id
            Long activityId = activityIds.get(j);

            //返回参数
            Map<String, Object> order = new HashMap<>();

            //初始化规则引擎---------------------------
            ExpressRunner runner = new ExpressRunner();
            DefaultContext<String, Object> context = new DefaultContext<String, Object>();

            //查询活动基本信息
            MktCampaignDO mktCampaign = mktCampaignMapper.selectByPrimaryKey(activityId);

            //返回参数中添加活动信息
            order.put("orderISI", ISI);
            order.put("activityId", mktCampaign.getMktCampaignId().toString());
            order.put("activityName", mktCampaign.getMktCampaignName());
            order.put("activityCode", mktCampaign.getMktActivityNbr());
            order.put("skipCheck", "0");  //todo 不明 案例上有 文档上没有
            order.put("orderPriority", "100");  //todo 不明 案例上有 文档上没有

            //根据活动id获取策略列表
            List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOs = mktCamStrategyConfRelMapper.selectByMktCampaignId(activityId);

            if (mktCamStrategyConfRelDOs != null && mktCamStrategyConfRelDOs.size() > 0) {

                //初始化返回结果中的推荐信息列表
                List<Map<String, Object>> recommendList = new ArrayList<>();

                //遍历策略列表
                for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOs) {

                    //查询策略基本信息
                    MktStrategyConfDO mktStrategyConf = mktStrategyConfMapper.selectByPrimaryKey(mktCamStrategyConfRelDO.getStrategyConfId());

                    //验证策略生效时间
                    if (!(now.after(mktStrategyConf.getBeginTime()) && now.before(mktStrategyConf.getEndTime()))) {
                        //若当前时间在策略生效时间外
                        continue;
                    }

                    //todo 下发地市

                    //todo 判断下发渠道

                    //根据策略id获取策略下规则列表
                    List<MktStrategyConfRuleDO> mktStrategyConfRuleDOS = mktStrategyConfRuleMapper.selectByMktStrategyConfId(mktCamStrategyConfRelDO.getStrategyConfId());

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

                            //  1.判断活动的过滤规则---------------------------
                            //获取过滤规则
                            FilterRuleConfDO filterRuleConfDO = filterRuleConfMapper.selectByPrimaryKey(ruleConfId);
                            String ruleConfIdStr = filterRuleConfDO.getFilterRuleIds();
                            if (ruleConfIdStr != null) {
                                String[] array = ruleConfIdStr.split(",");
                                boolean ruleFilter = true;
                                for (String str : array) {
                                    //获取具体规则
                                    FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(Long.parseLong(str));

                                    //匹配事件过滤规则
                                    int flag = 0;
                                    flag = userListMapper.checkRule("", filterRule.getRuleId(), null);
                                    if (flag > 0) {
                                        ruleFilter = false;
                                    }
                                }
                                //若存在不符合的规则 结束当前规则循环
                                if (!ruleFilter) {
                                    continue;
                                }
                            }

                            //  2.判断活动的客户分群规则---------------------------
                            //查询分群规则list
                            List<TarGrpCondition> tarGrpConditionDOs = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
                            //将规则拼装为表达式
                            StringBuilder express = new StringBuilder();
                            express.append("if(");
                            //遍历所有规则
                            for (int i = 0; i < tarGrpConditionDOs.size(); i++) {
                                String type = tarGrpConditionDOs.get(i).getOperType();
                                Label label = injectionLabelMapper.selectByPrimaryKey(Long.parseLong(tarGrpConditionDOs.get(i).getLeftParam()));
                                express.append("(");
                                express.append(label.getInjectionLabelCode());
                                if ("1000".equals(type)) {
                                    express.append(">");
                                } else if ("2000".equals(type)) {
                                    express.append("<");
                                } else if ("3000".equals(type)) {
                                    express.append("==");
                                } else if ("4000".equals(type)) {
                                    express.append("!=");
                                } else if ("5000".equals(type)) {
                                    express.append(">=");
                                } else if ("6000".equals(type)) {
                                    express.append("<=");
                                }
                                express.append(tarGrpConditionDOs.get(i).getRightParam());
                                express.append(")");
                                if (i + 1 != tarGrpConditionDOs.size()) {
                                    express.append("&&");
                                }
                            }
                            express.append(") {return true} else {return false}");

                            //将标签因子值存入规则引擎上下文（这里目前假定接口传入的标签满足规则运算，不需要额外查询）
                            for (Map<String, Object> objectMap : labelList) {
                                Map<String, String> trigger = (Map<String, String>) objectMap.get("trigger");
                                context.put(trigger.get("key"), trigger.get("value"));
//                                logger.info("key = {},value = {}", trigger.get("key"), trigger.get("value"));
                            }

                            //规则引擎计算
                            RuleResult ruleResult = runner.executeRule(express.toString(), context, true, true);
                            //log输出
//                            logger.info("======================================");
//                            logger.info("事件流水 = {}", ISI);
//                            logger.info("事件ID = {}", eventNbr);
//                            logger.info("活动ID = {}", activityId);
//                            logger.info("express = {}", express);
//                            logger.info("result = {}", ruleResult.getResult());
//                            logger.info("tree = {}", ruleResult.getRule().toTree());
//                            logger.info("trace = {}", ruleResult.getTraceMap());
//                            logger.info("======================================");
                            //判断匹配结果，如匹配则向下进行，如不匹配则continue结束本次循环
                            if (ruleResult.getResult() != null && ((Boolean) ruleResult.getResult())) {

                                //查询销售品列表
                                String[] productArray = productStr.split(",");

                                //初始化返回结果中的销售品条目
                                List<Map<String, String>> productList = new ArrayList<>();
                                for (String str : productArray) {
                                    Map<String, String> product = new HashMap<>();
                                    PpmProduct ppmProduct = ppmProductMapper.selectByPrimaryKey(Long.parseLong(str));

                                    product.put("productCode", ppmProduct.getProductCode());
                                    product.put("productFlag", ""); //todo 不明 案例上有 文档上没有
                                    product.put("productAlias", ""); //todo 不明 案例上有 文档上没有
                                    product.put("productName", ppmProduct.getProductName());
                                    product.put("productType", ppmProduct.getProductType());
                                    productList.add(product);
                                }

                                //获取协同渠道所有id
                                String[] evtContactConfIdArray = evtContactConfIdStr.split(",");
                                //遍历协同渠道
                                for (String str : evtContactConfIdArray) {


                                    //协同渠道规则表id（自建表）
                                    Long evtContactConfId = Long.parseLong(str);

                                    //查询渠道属性
                                    List<MktCamChlConfAttrDO> mktCamChlConfAttrs = mktCamChlConfAttrMapper.selectByEvtContactConfId(evtContactConfId);

                                    boolean checkTime = true;
                                    for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrs) {
                                        //判断渠道生失效时间
                                        if (mktCamChlConfAttrDO.getAttrId() == 1000L) {
                                            if (!now.after(new Date(mktCamChlConfAttrDO.getAttrValue()))) {
                                                checkTime = false;
                                            }
                                        }
                                        if (mktCamChlConfAttrDO.getAttrId() == 1001L) {
                                            if (!now.after(new Date(mktCamChlConfAttrDO.getAttrValue()))) {
                                                checkTime = false;
                                            }
                                        }
                                        //判断接触时间段 todo
//                                        if (mktCamChlConfAttrDO.getAttrId() == 1003L) {
//                                            if (!now.after(new Date(mktCamChlConfAttrDO.getAttrValue()))) {
//                                                checkTime = false;
//                                            }
//                                        }
//                                        if (mktCamChlConfAttrDO.getAttrId() == 1004L) {
//                                            if (!now.after(new Date(mktCamChlConfAttrDO.getAttrValue()))) {
//                                                checkTime = false;
//                                            }
//                                        }
                                    }

                                    if (!checkTime) {
                                        continue;
                                    }

                                    //初始化返回结果推荐信息
                                    Map<String, Object> recommend = new HashMap<>();

                                    //查询渠道信息基本信息
                                    MktCamChlConfDO mktCamChlConf = mktCamChlConfMapper.selectByPrimaryKey(evtContactConfId);

                                    //返回渠道基本信息
                                    recommend.put("channelId", mktCamChlConf.getContactChlId());
                                    recommend.put("pushType", mktCamChlConf.getPushType());
                                    recommend.put("pushContent", ""); //todo 不明

                                    //返回结果中添加销售品信息
                                    recommend.put("productList", productList);


                                    //查询渠道子策略 这里老系统暂时不返回
//                                    List<MktVerbalCondition> mktVerbalConditions = mktVerbalConditionMapper.findConditionListByVerbalId(evtContactConfId);

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
                                            //todo 格式化话术规则 如何返回

                                        }
                                    }
                                    recommend.put("verbal", ""); //todo 待定

                                    recommendList.add(recommend);
                                }
                            }
                        }
                        order.put("recommendList", recommendList);
                    } else {
                        //规则为空
                        System.out.println("规则为空");
                    }
                }
            } else {
                //策略为空
                System.out.println("策略为空");
            }
            orderList.add(order);
        }

        //返回结果
        result.put("orderList", orderList);

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map CalculateCPC(Map<String, Object> map) throws Exception {
        //初始化返回结果
        Map<String, Object> result = new HashMap();

        //获取当前时间
        Date now = new Date();

        //获取事件code
        String eventNbr = (String) map.get("eventId");
        //获取流水号
        String ISI = (String) map.get("ISI");

        //从map中取出参数
//        EventReportDTO eventReportDTO = new EventReportDTO();
//        eventReportDTO.setEventId((String) map.get("eventId")); //事件code
//        eventReportDTO.setC4((String) map.get("C4")); //C4代码
//        eventReportDTO.setChannelId((String) map.get("channelId")); //渠道
//        eventReportDTO.setISI((String) map.get("ISI")); //流水号
//        eventReportDTO.setLanId(Long.valueOf((String) map.get("lanId"))); //本地网标识

        //构造返回参数
        result.put("custId", "客户编码");
        result.put("CPCResultCode", "1");
        result.put("CPCResultMsg", "success");
        result.put("ISI", ISI);

        //获取标签因子集合
        List<Map<String, Object>> labelList = (List<Map<String, Object>>) map.get("triggers");

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
        ContactEvtMatchRul contactEvtMatchRulParam = new ContactEvtMatchRul();
        contactEvtMatchRulParam.setContactEvtId(eventId);
        List<ContactEvtMatchRul> contactEvtMatchRuls = contactEvtMatchRulMapper.listEventMatchRuls(contactEvtMatchRulParam);
        //遍历事件过滤规则匹配
        if (contactEvtMatchRuls != null && contactEvtMatchRuls.size() > 0) {
            //匹配事件过滤规则
            int flag = 0;
            for (ContactEvtMatchRul contactEvtMatchRul : contactEvtMatchRuls) {
                flag = userListMapper.checkRule("", contactEvtMatchRul.getEvtMatchRulId(), null);
                if (flag > 0) {
                    result.put("CPCResultCode", "0");
                    result.put("CPCResultMsg", "事件过滤拦截");
                    return result;
                }
            }
        }

        //根据事件id 查询所有关联活动（根据优先级排序 正序）
        List<Long> activityIds = mktCamEvtRelMapper.listActivityByEventId(eventId);

        //初始化返回结果中的工单信息
        List<Map<String, Object>> orderList = new ArrayList<>();

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
            Future<Map<String, Object>> f = executorService.submit(new ActivityTask(ISI, activityId));
            //将线程处理结果添加到结果集
            threadList.add(f);
        }

      

        //获取结果
        try {
            for (Future<Map<String, Object>> future : threadList) {
                orderList.add(future.get());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            //发生异常关闭线程池
            executorService.shutdown();
        } catch (ExecutionException e) {
            e.printStackTrace();
            //发生异常关闭线程池
            executorService.shutdown();
            return null;
        }

        //关闭线程池
        executorService.shutdown();

        //返回结果
        result.put("orderList", orderList);

        return result;
    }

    @Override
    public void cpc() {

    }
}
