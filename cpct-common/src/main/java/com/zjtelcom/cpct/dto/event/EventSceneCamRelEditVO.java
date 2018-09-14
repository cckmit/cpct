package com.zjtelcom.cpct.dto.event;

import java.io.Serializable;

public class EventSceneCamRelEditVO implements Serializable {

    private Long eventSceneId;
    private Integer campaignSeq;
    private Long mktCampaignId;
    private Integer levelConfig;

    public Long getEventSceneId() {
        return eventSceneId;
    }

    public void setEventSceneId(Long eventSceneId) {
        this.eventSceneId = eventSceneId;
    }

    public Integer getCampaignSeq() {
        return campaignSeq;
    }

    public void setCampaignSeq(Integer campaignSeq) {
        this.campaignSeq = campaignSeq;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public Integer getLevelConfig() {
        return levelConfig;
    }

    public void setLevelConfig(Integer levelConfig) {
        this.levelConfig = levelConfig;
    }

}
