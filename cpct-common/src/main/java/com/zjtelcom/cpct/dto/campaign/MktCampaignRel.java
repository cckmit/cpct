package com.zjtelcom.cpct.dto.campaign;

import java.io.Serializable;
import java.util.Date;

public class MktCampaignRel implements Serializable {
    private Long mktCampaignRelId;

    private String relType;

    private Long aMktCampaignId;

    private Long zMktCampaignId;

    private Date effDate;

    private Date expDate;

    private Long applyRegionId;

    private String statusCd;

    private Long createStaff;

    private Long updateStaff;

    private Date createDate;

    private Date statusDate;

    private Date updateDate;

    private String remark;

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

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public Long getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(Long createStaff) {
        this.createStaff = createStaff;
    }

    public Long getUpdateStaff() {
        return updateStaff;
    }

    public void setUpdateStaff(Long updateStaff) {
        this.updateStaff = updateStaff;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}