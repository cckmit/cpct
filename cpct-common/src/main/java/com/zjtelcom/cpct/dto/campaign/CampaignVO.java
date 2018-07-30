package com.zjtelcom.cpct.dto.campaign;

import java.io.Serializable;

public class CampaignVO implements Serializable {
    private Long campaignId;
    private String campaignName;

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }
}
