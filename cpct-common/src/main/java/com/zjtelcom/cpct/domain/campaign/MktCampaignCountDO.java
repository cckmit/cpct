package com.zjtelcom.cpct.domain.campaign;

import com.zjtelcom.cpct.BaseEntity;

import java.util.Date;

public class MktCampaignCountDO extends MktCampaignDO{

    private int relCount;

    /**
     * 父活动的Id
     */
    private Long preMktCampaignId;

    public int getRelCount() {
        return relCount;
    }

    public void setRelCount(int relCount) {
        this.relCount = relCount;
    }

    public Long getPreMktCampaignId() {
        return preMktCampaignId;
    }

    public void setPreMktCampaignId(Long preMktCampaignId) {
        this.preMktCampaignId = preMktCampaignId;
    }
}