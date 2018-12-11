package com.zjtelcom.cpct.dto.strategy;

import java.util.List;

/**
 * 策略配置详情
 */
public class MktStrategyConfDetail extends MktStrategyConf {

    /**
     * 营销活动标识
     */
    private Long mktCampaignId;

    /**
     * 营销活动名称
     */
    private String mktCampaignName;

    /**
     * 营销活动分类
     */
    private String mktCampaignType;


    /**
     * 策略配置下规则列表
     */
    private List<MktStrategyConfRule> mktStrategyConfRuleList;


    public List<MktStrategyConfRule> getMktStrategyConfRuleList() {
        return mktStrategyConfRuleList;
    }

    public void setMktStrategyConfRuleList(List<MktStrategyConfRule> mktStrategyConfRuleList) {
        this.mktStrategyConfRuleList = mktStrategyConfRuleList;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public String getMktCampaignName() {
        return mktCampaignName;
    }

    public void setMktCampaignName(String mktCampaignName) {
        this.mktCampaignName = mktCampaignName;
    }

    public String getMktCampaignType() {
        return mktCampaignType;
    }

    public void setMktCampaignType(String mktCampaignType) {
        this.mktCampaignType = mktCampaignType;
    }
}