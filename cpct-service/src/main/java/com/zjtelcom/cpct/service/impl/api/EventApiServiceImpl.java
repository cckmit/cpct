package com.zjtelcom.cpct.service.impl.api;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.rule.RuleResult;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.event.ContactEvtMatchRulMapper;
import com.zjtelcom.cpct.dao.event.EventMapper;
import com.zjtelcom.cpct.dao.event.EventSceneMapper;
import com.zjtelcom.cpct.dao.event.EvtSceneCamRelMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.event.EventDO;
import com.zjtelcom.cpct.domain.event.EventSceneDO;
import com.zjtelcom.cpct.domain.event.EvtSceneCamRelDO;
import com.zjtelcom.cpct.domain.grouping.TarGrpConditionDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.dto.api.EventApiResultDTO;
import com.zjtelcom.cpct.dto.api.EventReportDTO;
import com.zjtelcom.cpct.dto.campaign.FilterRuleConf;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;
import com.zjtelcom.cpct.dto.campaign.MktCampaign;
import com.zjtelcom.cpct.dto.channel.MktScript;
import com.zjtelcom.cpct.dto.event.ContactEvtMatchRul;
import com.zjtelcom.cpct.dto.event.EventScene;
import com.zjtelcom.cpct.dto.event.EvtSceneCamRel;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRuleRel;
import com.zjtelcom.cpct.request.event.QryEventSceneListReq;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.api.EventApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EventApiServiceImpl extends BaseService implements EventApiService {

    @Autowired
    private EventMapper eventMapper;  //事件总表

    @Autowired
    private ContactEvtMatchRulMapper contactEvtMatchRulMapper; //事件过滤规则

    @Autowired
    private EventSceneMapper eventSceneMapper; //事件场景

    @Autowired
    private EvtSceneCamRelMapper evtSceneCamRelMapper; //事件场景与活动关联表

    @Autowired
    private MktCampaignMapper mktCampaignMapper; //活动基本信息

    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper; //分群规则条件表

    @Autowired
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper; //活动策略

    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;//策略规则

    @Autowired
    private FilterRuleMapper filterRuleMapper; //过滤规则

    @Autowired
    private FilterRuleConfMapper filterRuleConfMapper; //过滤规则与策略规则关联表

    @Autowired
    private PpmProductMapper ppmProductMapper; //销售品

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


    /**
     * 事件触发接口实现
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map deal(Map<String, Object> map) throws Exception {
        //初始化返回结果
        Map<String, Object> result = new HashMap();

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
        EventDO event = eventMapper.getEventByEventNbr(eventNbr);

        //获取事件id
        Long eventId = event.getEventId();

        //获取事件过滤规则
        ContactEvtMatchRul contactEvtMatchRul = new ContactEvtMatchRul();
        contactEvtMatchRul.setContactEvtId(eventId);
        List<ContactEvtMatchRul> contactEvtMatchRuls = contactEvtMatchRulMapper.listEventMatchRuls(contactEvtMatchRul);
        //遍历事件过滤规则匹配
        if (contactEvtMatchRuls != null && contactEvtMatchRuls.size() > 0) {
            //todo 匹配事件过滤规则
            System.out.println("匹配事件过滤规则");
        }

        //根据事件id 查询所有关联的事件场景
        EventSceneDO param = new EventSceneDO();
        param.setEventId(eventId);
        List<EventScene> eventScenes = eventSceneMapper.qryEventSceneByEvtId(eventId);

        //循环事件场景列表 根据事件场景获取活动列表  todo 目前是根据事件场景获取所有活动
        List<Long> activityIds = new ArrayList<>(); //活动id list
        for (EventScene eventScene : eventScenes) {
            List<EvtSceneCamRel> evtSceneCamRels = evtSceneCamRelMapper.selectCamsByEvtSceneId(eventScene.getEventSceneId());
            if (evtSceneCamRels != null && evtSceneCamRels.size() > 0) {
                for (EvtSceneCamRel evtSceneCamRel : evtSceneCamRels) {
                    activityIds.add(evtSceneCamRel.getMktCampaignId());
                }
            }
        }

        //初始化返回结果中的工单信息
        List<Map<String, Object>> orderList = new ArrayList<>();

        //遍历活动id  查询并匹配活动规则
        for (Long activityId : activityIds) {

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

                            //  1.判断活动的过滤规则
                            //获取过滤规则
                            FilterRuleConfDO filterRuleConfDO = filterRuleConfMapper.selectByPrimaryKey(ruleConfId);
                            String ruleConfIdStr = filterRuleConfDO.getFilterRuleIds();
                            if (ruleConfIdStr != null) {
                                String[] array = ruleConfIdStr.split(",");
                                boolean ruleFilter = true;
                                for (String str : array) {
                                    //获取具体规则
                                    FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(Long.parseLong(str));
                                    //todo 计算过滤规则，入不符合直接pass
                                    if (false) {
                                        ruleFilter = false;
                                    }
                                }
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
                                logger.info("key = {},value = {}", trigger.get("key"), trigger.get("value"));
                            }

                            //规则引擎计算
                            RuleResult ruleResult = runner.executeRule(express.toString(), context, true, true);
                            //log输出
                            logger.info("======================================");
                            logger.info("事件流水 = {}", ISI);
                            logger.info("事件ID = {}", eventNbr);
                            logger.info("活动ID = {}", activityId);
                            logger.info("express = {}", express);
                            logger.info("result = {}", ruleResult.getResult());
                            logger.info("tree = {}", ruleResult.getRule().toTree());
                            logger.info("trace = {}", ruleResult.getTraceMap());
                            logger.info("======================================");
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

                                    //初始化返回结果推荐信息
                                    Map<String, Object> recommend = new HashMap<>();

                                    recommend.put("productList", productList);

                                    Long evtContactConfId = Long.parseLong(str);
                                    //查询渠道信息基本信息
                                    MktCamChlConfDO mktCamChlConf = mktCamChlConfMapper.selectByPrimaryKey(evtContactConfId);

                                    recommend.put("channelId", mktCamChlConf.getContactChlId());
                                    recommend.put("pushType", mktCamChlConf.getPushType());
                                    recommend.put("pushContent", ""); //todo 不明

                                    //查询渠道属性
//                                    List<MktCamChlConfAttrDO> mktCamChlConfAttrs = mktCamChlConfAttrMapper.selectByEvtContactConfId(evtContactConfId);

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
        result.put("code", 1);
        result.put("orderList", orderList);

        return result;
    }
}
