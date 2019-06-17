package com.zjtelcom.cpct.dubbo.model;


import com.zjtelcom.cpct.dto.filter.FilterRuleModel;

import java.util.ArrayList;

/**
 * 策略配置详情
 */
public class MktStrategyConfResp extends MktStrategyConf {

    private Long mktCampaignId;

    /**
     * 策略配置下规则列表
     */
    private ArrayList<MktStrConfRuleResp> mktStrConfRuleRespList;

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public ArrayList<MktStrConfRuleResp> getMktStrConfRuleRespList() {
        return mktStrConfRuleRespList;
    }

    public void setMktStrConfRuleRespList(ArrayList<MktStrConfRuleResp> mktStrConfRuleRespList) {
        this.mktStrConfRuleRespList = mktStrConfRuleRespList;
    }

}