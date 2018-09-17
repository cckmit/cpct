package com.zjtelcom.cpct.dto.campaign;

import com.zjtelcom.cpct.BaseEntity;
import java.io.Serializable;

public class MktCamEvtRel extends BaseEntity implements Serializable{

    private static final long serialVersionUID = -6533775184797882441L;
    private Long mktCampEvtRelId;

    private Integer campaignSeq;

    private String campaignName;

    private Long mktCampaignId;

    private Long eventId;

    private Integer levelConfig;



    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

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