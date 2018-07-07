package com.zjtelcom.cpct.dto.campaign;

import com.zjtelcom.cpct.BaseEntity;
import java.io.Serializable;

public class MktCamEvtRel extends BaseEntity implements Serializable{

    private static final long serialVersionUID = -6533775184797882441L;
    private Long mktCampEvtRelId;

    private Long mktCampaignId;

    private Long eventId;

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