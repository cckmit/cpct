package com.zjtelcom.cpct.service.impl.api;

import com.zjtelcom.cpct.dao.event.EventMapper;
import com.zjtelcom.cpct.dao.event.EventSceneMapper;
import com.zjtelcom.cpct.dao.event.EvtSceneCamRelMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.event.EventDO;
import com.zjtelcom.cpct.domain.event.EventSceneDO;
import com.zjtelcom.cpct.dto.api.EventApiResultDTO;
import com.zjtelcom.cpct.dto.api.EventReportDTO;
import com.zjtelcom.cpct.service.api.EventApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EventApiServiceImpl implements EventApiService {

    @Autowired
    private EventMapper eventMapper;  //事件总表

    @Autowired
    private EventSceneMapper eventSceneMapper; //事件场景

    @Autowired
    private EvtSceneCamRelMapper evtSceneCamRelMapper; //事件场景与活动关联表

    @Override
    @SuppressWarnings("unchecked")
    public EventApiResultDTO deal(Map<String, Object> map) {
        //初始化返回数据结果
        EventApiResultDTO result = new EventApiResultDTO();
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
        List<EventSceneDO> eventScenes = eventSceneMapper.listEventSences(param);

        //循环事件场景列表 根据事件场景获取活动列表
        List<Long> activityIds = new ArrayList<>();
        for(EventSceneDO eventSceneDO : eventScenes) {
            evtSceneCamRelMapper.selectByEventSceneId(eventSceneDO.getEventSceneId());
        }
        //活动列表
        List<MktCampaignDO> activities = new ArrayList<>();


        //todo 匹配活动规则，筛选出符合规则的活动列表


        return result;
    }
}
