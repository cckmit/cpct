package com.zjtelcom.cpct.domain.campaign;

import com.zjtelcom.cpct.BaseEntity;

import java.util.Date;

public class MktCampaignRelDO extends BaseEntity {
    // 活动关系标识
    private Long mktCampaignRelId;
    // 关系类型
    private String relType;
    // 父活动标识
    private Long aMktCampaignId;
    // 子活动标识
    private Long zMktCampaignId;
    // 关系生效时间
    private Date effDate;
    // 关系失效时间
    private Date expDate;
    // 适用区域范围
    private Long applyRegionId;

    public Long getMktCampaignRelId() {
        return mktCampaignRelId;
    }

    public void setMktCampaignRelId(Long mktCampaignRelId) {
        this.mktCampaignRelId = mktCampaignRelId;
    }

    public String getRelType() {
        return relType;
    }

    public void setRelType(String relType) {
        this.relType = relType;
    }

    public Long getaMktCampaignId() {
        return aMktCampaignId;
    }

    public void setaMktCampaignId(Long aMktCampaignId) {
        this.aMktCampaignId = aMktCampaignId;
    }

    public Long getzMktCampaignId() {
        return zMktCampaignId;
    }

    public void setzMktCampaignId(Long zMktCampaignId) {
        this.zMktCampaignId = zMktCampaignId;
    }

    public Date getEffDate() {
        return effDate;
    }

    public void setEffDate(Date effDate) {
        this.effDate = effDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public Long getApplyRegionId() {
        return applyRegionId;
    }

    public void setApplyRegionId(Long applyRegionId) {
        this.applyRegionId = applyRegionId;
    }

}