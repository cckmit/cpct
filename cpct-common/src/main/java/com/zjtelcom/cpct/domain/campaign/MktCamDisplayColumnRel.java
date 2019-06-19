package com.zjtelcom.cpct.domain.campaign;

import com.zjtelcom.cpct.BaseEntity;

public class MktCamDisplayColumnRel extends BaseEntity {
    private Long mktCamDisplayColumnRelId;

    private Long mktCampaignId;

    private Long injectionLabelId;

    private Long displayId;

    private String displayColumnType;

    private String labelDisplayType;

    public Long getMktCamDisplayColumnRelId() {
        return mktCamDisplayColumnRelId;
    }

    public void setMktCamDisplayColumnRelId(Long mktCamDisplayColumnRelId) {
        this.mktCamDisplayColumnRelId = mktCamDisplayColumnRelId;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public Long getInjectionLabelId() {
        return injectionLabelId;
    }

    public void setInjectionLabelId(Long injectionLabelId) {
        this.injectionLabelId = injectionLabelId;
    }

    public Long getDisplayId() {
        return displayId;
    }

    public void setDisplayId(Long displayId) {
        this.displayId = displayId;
    }

    public String getDisplayColumnType() {
        return displayColumnType;
    }

    public void setDisplayColumnType(String displayColumnType) {
        this.displayColumnType = displayColumnType;
    }

    public String getLabelDisplayType() {
        return labelDisplayType;
    }

    public void setLabelDisplayType(String labelDisplayType) {
        this.labelDisplayType = labelDisplayType;
    }
}
