package com.zjtelcom.cpct.service.impl.event;

import com.zjtelcom.cpct.dao.campaign.MktCamEvtRelMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtItemMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyFilterRuleRelMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.EventItem;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.campaign.MktCamEvtRel;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.service.channel.SearchLabelService;
import com.zjtelcom.cpct.service.event.EventRedisService;
import com.zjtelcom.cpct.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/12/10 11:09
 * @version: V1.0
 */
@Service
@Transactional
public class EventRedisServiceImpl implements EventRedisService {

    @Autowired
    private RedisUtils redisUtils;  // redis方法

    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper;
    @Autowired
    private FilterRuleMapper filterRuleMapper;
    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    @Autowired
    private MktStrategyConfMapper mktStrategyConfMapper;
    @Autowired
    private ContactChannelMapper contactChannelMapper;
    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;
    @Autowired
    private SysParamsMapper sysParamsMapper;
    @Autowired
    private ContactEvtItemMapper contactEvtItemMapper;
    @Autowired
    private SearchLabelService searchLabelService;
    @Autowired
    private ContactEvtMapper contactEvtMapper;
    @Autowired
    private MktStrategyFilterRuleRelMapper mktStrategyFilterRuleRelMapper;

    /**
     * 获取缓存，若没有缓存则查询数据库并存入缓存
     *
     * @param key    索引的前部分key
     * @param id     索引的后部分id,若没有则传0
     * @param params 其它参数
     * @return
     */
    @Override
    public Map<String, Object> getRedis(String key, Long id, Map<String, Object> params) {
        Map<String, Object> resutlt = new HashMap<>();
        Object o = new Object();
        if (id != 0) {
            o = redisUtils.get(key + id);
        } else {
            o = redisUtils.get(key);
        }
        if (o != null) {
            resutlt.put(key + id, o);
        } else {
            // 活动和事件的关联关系
            if ("CAM_IDS_EVT_REL_".equals(key)) {
                List<Map<String, Object>> mktCampaginIdList = mktCamEvtRelMapper.listActivityByEventId(id);
                redisUtils.set(key + id, mktCampaginIdList);
                resutlt.put(key + id, mktCampaginIdList);
            } else if ("FILTER_RULE_STR_".equals(key)) { // 过滤规则
                List<String> strategyTypeList = (List<String>) params.get("strategyTypeList");
                List<FilterRule> filterRuleList = filterRuleMapper.selectFilterRuleListByStrategyId(id, strategyTypeList);
                redisUtils.set("FILTER_RULE_STR_" + id, filterRuleList);
                resutlt.put(key + id, filterRuleList);
            } else if ("MKT_CAMPAIGN_".equals(key)) {  // 活动基本信息
                MktCampaignDO mktCampaign = mktCampaignMapper.selectByPrimaryKey(id);
                redisUtils.set(key + id, mktCampaign);
                resutlt.put(key + id, mktCampaign);
            } else if ("MKT_STRATEGY_".equals(key)) {    //  通过活动查询相关联的策略
                List<MktStrategyConfDO> mktStrategyConfDOS = mktStrategyConfMapper.selectByCampaignId(id);
                redisUtils.set(key + id, mktStrategyConfDOS);
                resutlt.put(key + id, mktStrategyConfDOS);
            } else if ("CHANNEL_CODE_LIST_".equals(key)) {
                String channelsIdStr = (String) params.get("channelsId");
                if (channelsIdStr != null && !"".equals(channelsIdStr)) {
                    String[] strArrayChannelsId = channelsIdStr.split("/");
                    List<Long> channelsIdList = new ArrayList<>();
                    if (strArrayChannelsId != null && !"".equals(strArrayChannelsId[0])) {
                        for (String channelsId : strArrayChannelsId) {
                            channelsIdList.add(Long.valueOf(channelsId));
                        }
                    }
                    List<String> channelCodeList = contactChannelMapper.selectChannelCodeByPrimaryKey(channelsIdList);
                    redisUtils.set(key + id, channelCodeList);
                    resutlt.put(key + id, channelCodeList);
                }
            } else if ("RULE_LIST_".equals(key)) {   // 过滤规则
                List<MktStrategyConfRuleDO> mktStrategyConfRuleList = (List<MktStrategyConfRuleDO>) mktStrategyConfRuleMapper.selectByMktStrategyConfId(id);
                redisUtils.setRedis(key + id, mktStrategyConfRuleList);
                resutlt.put(key + id, mktStrategyConfRuleList);
            } else if ("MKT_CAM_API_CODE_KEY".equals(key)) {    // 走清单活动列表
                List<String> mktCamCodeList = new ArrayList<>();
                List<SysParams> sysParamsList = sysParamsMapper.listParamsByKeyForCampaign("MKT_CAM_API_CODE");
                for (SysParams sysParams : sysParamsList) {
                    mktCamCodeList.add(sysParams.getParamValue());
                }
                redisUtils.set("MKT_CAM_API_CODE_KEY", mktCamCodeList);
                resutlt.put("MKT_CAM_API_CODE_KEY", mktCamCodeList);
            } else if ("EVENT_ITEM_".equals(key)) {  // 事件采集项
                List<EventItem> contactEvtItems = contactEvtItemMapper.listEventItem(id);
                redisUtils.setRedis(key + id, contactEvtItems);
                resutlt.put(key + id, contactEvtItems);
            } else if ("EVT_ALL_LABEL_".equals(key)) {  // 事件下所有标签
                Map<String, String> mktAllLabels = searchLabelService.labelListByEventId(id);  //查询事件下使用的所有标签
                if (null != mktAllLabels) {
                    redisUtils.set("EVT_ALL_LABEL_" + id, mktAllLabels);
                    resutlt.put("EVT_ALL_LABEL_" + id, mktAllLabels);
                }
            } else if("EVENT_".equals(key)){  // 事件
                ContactEvt event = contactEvtMapper.getEventByEventNbr(id.toString());
                redisUtils.set(key + id, event);
                resutlt.put(key + id, event);
            } else if("CAM_EVT_REL_".equals(key)){   // 活动事件关系
                List<MktCamEvtRel> resultByEvent = mktCamEvtRelMapper.qryBycontactEvtId(id);
                redisUtils.set(key + id, resultByEvent);
                resutlt.put(key + id, resultByEvent);
            } else if("CUST_PROD_FILTER".equals(key)){ // 清单销售品过滤
                List<SysParams> sysParamsList = sysParamsMapper.listParamsByKeyForCampaign("CUST_PROD_FILTER");
                if (sysParamsList != null && sysParamsList.size() > 0) {
                    String custProdFilter = sysParamsList.get(0).getParamValue();
                    redisUtils.set(key, custProdFilter);
                    resutlt.put(key, custProdFilter);
                }
            } else if("FILTER_RULE_LIST_".equals(key)){ // 过滤规则集合
                List<FilterRule> filterRuleList = filterRuleMapper.selectFilterRuleList(id);
                redisUtils.set(key + id, filterRuleList);
                resutlt.put(key + id, filterRuleList);
            } else if("MKT_FILTER_RULE_IDS_".equals(key)){ // 过滤规则Id
                List<Long> filterRuleIds = mktStrategyFilterRuleRelMapper.selectByStrategyId(id);
                redisUtils.set(key + id, filterRuleIds);
                resutlt.put(key + id, filterRuleIds);
            } else if("FILTER_RULE_".equals(key)){ // 过滤规则
                FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(id);
                redisUtils.set(key + id, filterRule);
                resutlt.put(key + id, filterRule);
            } else if("REAL_PROD_FILTER".equals(key)){ // 清单销售品过滤
                List<SysParams> sysParamsList = sysParamsMapper.listParamsByKeyForCampaign("REAL_PROD_FILTER");
                if (sysParamsList != null && sysParamsList.size() > 0) {
                    String realProdFilter = sysParamsList.get(0).getParamValue();
                    redisUtils.set(key, realProdFilter);
                    resutlt.put(key, realProdFilter);
                }
            }
        }
        return resutlt;
    }
}