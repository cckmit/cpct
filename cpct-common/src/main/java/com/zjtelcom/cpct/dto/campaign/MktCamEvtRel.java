package com.zjtelcom.cpct.dto.campaign;

import com.zjtelcom.cpct.BaseEntity;
import java.io.Serializable;

public class MktCamEvtRel extends BaseEntity implements Serializable{

    private static final long serialVersionUID = -6533775184797882441L;
    private Long mktCampEvtRelId;

    private Integer campaignSeq;

    private Long mktCampaignId;

    private Long eventId;

    private Integer levelConfig;

    private Integer whetherConfig;


    public Integer getCampaignSeq() {
        return campaignSeq;
    }

    public void setCampaignSeq(Integer campaignSeq) {
        this.campaignSeq = campaignSeq;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getLevelConfig() {
        return levelConfig;
    }

    public void setLevelConfig(Integer levelConfig) {
        this.levelConfig = levelConfig;
    }

    public Integer getWhetherConfig() {
        return whetherConfig;
    }

    public void setWhetherConfig(Integer whetherConfig) {
        this.whetherConfig = whetherConfig;
    }

    public Long getMktCampEvtRelId() {
        return mktCampEvtRelId;
    }

    public void setMktCampEvtRelId(Long mktCampEvtRelId) {
        this.mktCampEvtRelId = mktCampEvtRelId;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }


}