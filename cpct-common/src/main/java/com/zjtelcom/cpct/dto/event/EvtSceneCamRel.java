package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.BaseEntity;
import java.io.Serializable;


public class EvtSceneCamRel extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 3081148801958220910L;
    private Long sceneCamRelId;
    private Long eventSceneId;
    private Integer campaignSeq;
    private Long mktCampaignId;
    private Integer levelConfig;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getLevelConfig() {
        return levelConfig;
    }

    public void setLevelConfig(Integer levelConfig) {
        this.levelConfig = levelConfig;
    }

    public Long getSceneCamRelId() {
        return sceneCamRelId;
    }

    public void setSceneCamRelId(Long sceneCamRelId) {
        this.sceneCamRelId = sceneCamRelId;
    }

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


}