package com.zjtelcom.cpct.service;

import com.zjtelcom.cpct.dto.filter.FilterRuleModel;
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

    private List<FilterRuleModel> filterRuleModelList;

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

    public List<FilterRuleModel> getFilterRuleModelList() {
        return filterRuleModelList;
    }

    public void setFilterRuleModelList(List<FilterRuleModel> filterRuleModelList) {
        this.filterRuleModelList = filterRuleModelList;
    }
}