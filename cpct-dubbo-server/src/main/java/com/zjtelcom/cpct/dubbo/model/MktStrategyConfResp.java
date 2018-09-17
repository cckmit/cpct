package com.zjtelcom.cpct.dubbo.model;

import com.zjtelcom.cpct.domain.SysArea;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConf;

import java.util.List;

/**
 * 策略配置详情
 */
public class MktStrategyConfResp extends MktStrategyConf {

    private Long mktCampaignId;

    /**
     * 策略配置下规则列表
     */
    private List<MktStrConfRuleResp> mktStrConfRuleRespList;

    /**
     * 策略适用地址
     */
    private List<SysArea> areaList;

    /**
     * 过滤规则
     */
    private List<FilterRule> filterRuleList;

    /**
     * 策略适用渠道
     */
    private List<Channel> channelsList;

    public List<MktStrConfRuleResp> getMktStrConfRuleRespList() {
        return mktStrConfRuleRespList;
    }

    public void setMktStrConfRuleRespList(List<MktStrConfRuleResp> mktStrConfRuleRespList) {
        this.mktStrConfRuleRespList = mktStrConfRuleRespList;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public List<SysArea> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<SysArea> areaList) {
        this.areaList = areaList;
    }

    public List<FilterRule> getFilterRuleList() {
        return filterRuleList;
    }

    public void setFilterRuleList(List<FilterRule> filterRuleList) {
        this.filterRuleList = filterRuleList;
    }

    public List<Channel> getChannelsList() {
        return channelsList;
    }

    public void setChannelsList(List<Channel> channelsList) {
        this.channelsList = channelsList;
    }
}