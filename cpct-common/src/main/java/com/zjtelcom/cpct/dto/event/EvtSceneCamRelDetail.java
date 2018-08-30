package com.zjtelcom.cpct.dto.event;

import java.io.Serializable;

public class EvtSceneCamRelDetail extends EvtSceneCamRel implements Serializable {
    private String campaignName;


    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }
}
