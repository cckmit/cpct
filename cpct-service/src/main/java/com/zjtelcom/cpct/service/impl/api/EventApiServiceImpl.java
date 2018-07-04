package com.zjtelcom.cpct.service.impl.api;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.rule.RuleResult;
import com.zjtelcom.cpct.dao.campaign.MktCamGrpRulMapper;
import com.zjtelcom.cpct.dao.event.EventMapper;
import com.zjtelcom.cpct.dao.event.EventSceneMapper;
import com.zjtelcom.cpct.dao.event.EvtSceneCamRelMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.event.EventDO;
import com.zjtelcom.cpct.domain.event.EventSceneDO;
import com.zjtelcom.cpct.domain.event.EvtSceneCamRelDO;
import com.zjtelcom.cpct.domain.grouping.TarGrpConditionDO;
import com.zjtelcom.cpct.dto.api.EventApiResultDTO;
import com.zjtelcom.cpct.dto.api.EventReportDTO;
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

    @Override
    @SuppressWarnings("unchecked")
    public Map deal(Map<String, Object> map) throws Exception {
        //初始化返回结果
        Map result = new HashMap();

        //从map中取出参数
        EventReportDTO eventReportDTO = new EventReportDTO();
        eventReportDTO.setEventId((String) map.get("eventId")); //事件code
        eventReportDTO.setC4((String) map.get("C4")); //C4代码
        eventReportDTO.setChannelId((String) map.get("channelId")); //渠道
        eventReportDTO.setISI((String) map.get("ISI")); //流水号
        eventReportDTO.setLanId(Long.valueOf((String) map.get("lanId"))); //本地网标识

        //获取标签因子集合
        List<Map<String, Object>> labelList = (List<Map<String, Object>>) map.get("triggers");

        //获取事件code
        String eventNbr = eventReportDTO.getEventId();

        //根据事件code查询事件信息以及事件场景信息
        EventDO event = eventMapper.getEventByEventNbr(eventNbr);

        //获取事件id
        Long eventId = event.getEventId();

        //根据事件id 查询所有关联的事件场景
        EventSceneDO param = new EventSceneDO();
        param.setEventId(eventId);
        List<EventSceneDO> eventScenes = null;

        //循环事件场景列表 根据事件场景获取活动列表
        List<Long> activityIds = new ArrayList<>();
        for (EventSceneDO eventSceneDO : eventScenes) {
            List<EvtSceneCamRelDO> evtSceneCamRelDOS = evtSceneCamRelMapper.selectCamsByEvtSceneId(eventSceneDO.getEventSceneId());
            if (evtSceneCamRelDOS != null && evtSceneCamRelDOS.size() > 0) {
                for (EvtSceneCamRelDO evtSceneCamRelDO : evtSceneCamRelDOS) {
                    activityIds.add(evtSceneCamRelDO.getMktCampaignId());
                }
            }
        }
        //初始化参数
        List<MktCampaignDO> activities = new ArrayList<>(); //活动列表

        //遍历活动id  查询并匹配活动规则
        for (Long activityId : activityIds) {
            //初始化规则引擎
            ExpressRunner runner = new ExpressRunner();
            DefaultContext<String, Object> context = new DefaultContext<String, Object>();

            //查询规则
            List<TarGrpConditionDO> tarGrpConditionDOS = tarGrpConditionMapper.selectByActivityId(activityId);

            //将规则拼装为表达式
            StringBuilder express = new StringBuilder();
            express.append("if(");
            //遍历所有规则
            Map<String, String> rules = new HashMap();
            for (TarGrpConditionDO tarGrpConditionDO : tarGrpConditionDOS) {
                if ("1000".equals(tarGrpConditionDO.getLeftParamType())) {
                    StringBuilder rule = new StringBuilder();
                    rule.append(tarGrpConditionDO.getLeftParam());
                    if ("1000".equals(tarGrpConditionDO.getOperType())) {
                        rule.append(">");
                    } else if ("2000".equals(tarGrpConditionDO.getOperType())) {
                        rule.append("<");
                    } else if ("3000".equals(tarGrpConditionDO.getOperType())) {
                        rule.append("==");
                    }
                    rule.append(tarGrpConditionDO.getRightParam());
                    rules.put(tarGrpConditionDO.getLeftParam(), rule.toString());
                } else if("2000".equals(tarGrpConditionDO.getLeftParamType())) {

                }


            }
            express.append(") {return true;} else {return false;}");

//            express.append("if(INTEGRATION_ID == '1-U1D134B') {return true;} else {return false;}");

            //将标签因子值存入规则引擎上下文（这里目前假定接口传入的标签满足规则运算，不需要额外查询）
            for (Map<String, Object> objectMap : labelList) {
                Map<String, String> trigger = (Map<String, String>) objectMap.get("trigger");
                context.put(trigger.get("key"), trigger.get("value"));
            }

            //规则引擎计算
//            Object r = runner.execute(express.toString(), context, null, true, true);
//            result.put("规则引擎计算结果", r);

            //log模式
            RuleResult ruleResult = runner.executeRule(express.toString(), context, true, true);
            result.put("规则引擎计算结果", ruleResult.getResult());

            logger.info("======================================");
            logger.info("事件流水 = {}", eventReportDTO.getISI());
            logger.info("事件ID = {}", eventReportDTO.getEventId());
            logger.info("活动ID = {}", activityId);
            logger.info("express = {}", express);
            logger.info("result = {}", ruleResult.getResult());
            logger.info("tree = {}", ruleResult.getRule().toTree());
            logger.info("trace = {}", ruleResult.getTraceMap());
            logger.info("======================================");


        }

        //返回结果
        result.put("code", 1);

        return result;
    }
}
