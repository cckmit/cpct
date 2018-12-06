package com.zjtelcom.cpct.domain.campaign;

import com.zjtelcom.cpct.BaseEntity;

public class MktCamStrategyConfRelDO extends BaseEntity {
    private Long camStrConfRelId;

    private Long mktCampaignId;

    private Long strategyConfId;

    public Long getCamStrConfRelId() {
        return camStrConfRelId;
    }

    public void setCamStrConfRelId(Long camStrConfRelId) {
        this.camStrConfRelId = camStrConfRelId;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public Long getStrategyConfId() {
        return strategyConfId;
    }

    public void setStrategyConfId(Long strategyConfId) {
        this.strategyConfId = strategyConfId;
    }
}