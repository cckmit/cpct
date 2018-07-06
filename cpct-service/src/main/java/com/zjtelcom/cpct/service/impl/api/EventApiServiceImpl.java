package com.zjtelcom.cpct.service.impl.api;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.rule.RuleResult;
import com.zjtelcom.cpct.dao.campaign.MktCamGrpRulMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamStrategyConfRelMapper;
import com.zjtelcom.cpct.dao.event.EventMapper;
import com.zjtelcom.cpct.dao.event.EventSceneMapper;
import com.zjtelcom.cpct.dao.event.EvtSceneCamRelMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamRecomCalcRelDO;
import com.zjtelcom.cpct.domain.campaign.MktCamStrategyConfRelDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.event.EventDO;
import com.zjtelcom.cpct.domain.event.EventSceneDO;
import com.zjtelcom.cpct.domain.event.EvtSceneCamRelDO;
import com.zjtelcom.cpct.domain.grouping.TarGrpConditionDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.dto.api.EventApiResultDTO;
import com.zjtelcom.cpct.dto.api.EventReportDTO;
import com.zjtelcom.cpct.dto.event.EventScene;
import com.zjtelcom.cpct.dto.event.EvtSceneCamRel;
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
    private EventSceneMapper eventSceneMapper; //事件场景

    @Autowired
    private EvtSceneCamRelMapper evtSceneCamRelMapper; //事件场景与活动关联表

    @Autowired
    private MktCamGrpRulMapper mktCamGrpRulMapper; //活动分群规则关联表

    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper; //分群规则条件表

    @Autowired
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper; //策略

    @Autowired
    private MktStrategyConfRuleRelMapper mktStrategyConfRuleRelMapper;//策略规则

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

        //根据事件code查询事件信息以及事件场景信息
        EventDO event = eventMapper.getEventByEventNbr(eventNbr);

        //获取事件id
        Long eventId = event.getEventId();

        //根据事件id 查询所有关联的事件场景
        EventSceneDO param = new EventSceneDO();
        param.setEventId(eventId);
        List<EventScene> eventScenes = eventSceneMapper.qryEventSceneByEvtId(eventId);

        //循环事件场景列表 根据事件场景获取活动列表  todo 目前是根据事件场景过去所有活动
        List<Long> activityIds = new ArrayList<>(); //活动id list
        for (EventScene eventScene : eventScenes) {
            List<EvtSceneCamRel> evtSceneCamRels = evtSceneCamRelMapper.selectCamsByEvtSceneId(eventScene.getEventSceneId());
            if (evtSceneCamRels != null && evtSceneCamRels.size() > 0) {
                for (EvtSceneCamRel evtSceneCamRel : evtSceneCamRels) {
                    activityIds.add(evtSceneCamRel.getMktCampaignId());
                }
            }
        }
        //初始化参数
        List<MktCampaignDO> activities = new ArrayList<>(); //活动列表

        //遍历活动id  查询并匹配活动规则
        for (Long activityId : activityIds) {

            //初始化规则引擎---------------------------
            ExpressRunner runner = new ExpressRunner();
            DefaultContext<String, Object> context = new DefaultContext<String, Object>();

            //根据活动id获取策略列表
            List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOs = mktCamStrategyConfRelMapper.selectByMktCampaignId(activityId);

            if (mktCamStrategyConfRelDOs != null && mktCamStrategyConfRelDOs.size() > 0) {
                //遍历策略列表
                for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOs) {

                    //根据策略id获取策略下规则列表
                    List<MktStrategyConfRuleRelDO> mktStrategyConfRuleRelDOS = mktStrategyConfRuleRelMapper.selectByMktStrategyConfId(mktCamStrategyConfRelDO.getStrategyConfId());
                    //遍历规则列表
                    if (mktStrategyConfRuleRelDOS != null && mktStrategyConfRuleRelDOS.size() > 0) {
                        for (MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO : mktStrategyConfRuleRelDOS) {




                        }
                    } else {
                        //规则为空
                    }
                }
            } else {
                //策略为空
            }


            //  1.判断活动的过滤规则
            //查询活动过滤规则

            //拼接表达式

            //规则引擎匹配

            //判断匹配结果，如匹配则向下进行，如不匹配则continue结束本次循环

            //  2.判断活动的客户分群规则---------------------------
            //查询分群规则list
            List<TarGrpConditionDO> tarGrpConditionDOS = tarGrpConditionMapper.selectByActivityId(activityId);

            //将规则拼装为表达式
            StringBuilder express = new StringBuilder();
            express.append("if(");
            //遍历所有规则
            Map<String, String> rules = new HashMap();
            for (int i = 0; i < tarGrpConditionDOS.size(); i++) {
                StringBuilder rule = new StringBuilder();
                String type = tarGrpConditionDOS.get(i).getOperType();
                rule.append(tarGrpConditionDOS.get(i).getLeftParam());
                if ("1000".equals(type)) {
                    rule.append(">");
                } else if ("2000".equals(type)) {
                    rule.append("<");
                } else if ("3000".equals(type)) {
                    rule.append("==");
                } else if ("4000".equals(type)) {
                    rule.append(">=");
                } else if ("5000".equals(type)) {
                    rule.append("<=");
                }
                rule.append(tarGrpConditionDOS.get(i).getRightParam());
                if (i + 1 != tarGrpConditionDOS.size()) {
                    rule.append("&&");
                }
                rules.put(tarGrpConditionDOS.get(i).getLeftParam(), rule.toString());
            }
            express.append(") {return true;} else {return false;}");
//            express.append("if(INTEGRATION_ID == '1-U1D134B') {return true;} else {return false;}");

            //将标签因子值存入规则引擎上下文（这里目前假定接口传入的标签满足规则运算，不需要额外查询）
            for (Map<String, Object> objectMap : labelList) {
                Map<String, String> trigger = (Map<String, String>) objectMap.get("trigger");
                context.put(trigger.get("key"), trigger.get("value"));
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
//            if (ruleResult.getResult() != null && ruleResult.getResult() == true) {
//                //根据活动id查询出活动信息以及渠道信息
//
//            }


        }

        //返回结果
        result.put("code", 1);

        return result;
    }
}
