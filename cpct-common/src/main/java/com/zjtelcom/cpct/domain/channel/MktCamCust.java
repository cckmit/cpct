package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;

import java.util.Date;

public class MktCamCust extends BaseEntity {
    private Long mktCamCustId;

    private Long mktCampaignId;//分群模板id

    private String targetObjType;//目标对象类型

    private String targetObjNbr;//目标对象标识

    private String attrValue;//属性值。

    public Long getMktCamCustId() {
        return mktCamCustId;
    }

    public void setMktCamCustId(Long mktCamCustId) {
        this.mktCamCustId = mktCamCustId;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public String getTargetObjType() {
        return targetObjType;
    }

    public void setTargetObjType(String targetObjType) {
        this.targetObjType = targetObjType;
    }

    public String getTargetObjNbr() {
        return targetObjNbr;
    }

    public void setTargetObjNbr(String targetObjNbr) {
        this.targetObjNbr = targetObjNbr;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

}