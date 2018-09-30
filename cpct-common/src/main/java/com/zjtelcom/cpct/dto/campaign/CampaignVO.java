package com.zjtelcom.cpct.dto.campaign;

import java.io.Serializable;

public class CampaignVO implements Serializable {
    private Long mktCampaignId;
    private String campaignName;

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }
}
