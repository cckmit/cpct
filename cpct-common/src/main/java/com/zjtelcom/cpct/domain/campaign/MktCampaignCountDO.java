package com.zjtelcom.cpct.domain.campaign;

import com.zjtelcom.cpct.BaseEntity;

import java.util.Date;

public class MktCampaignCountDO extends MktCampaignDO{

    private int relCount;

    public int getRelCount() {
        return relCount;
    }

    public void setRelCount(int relCount) {
        this.relCount = relCount;
    }
}