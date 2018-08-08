package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.List;

public class ProductParam implements Serializable {
    private List<Long> idList;
    private Long campaignId;

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }
}
