package com.zjtelcom.cpct.domain.campaign;

import java.util.Date;

public class MktCamResultRelDO {
    private Long mktCamResultRelId;

    private Long mktCampaignId;

    private Long mktResultId;

    private String status;

    private Long createStaff;

    private Date createDate;

    private Long updateStaff;

    private Date updateDate;

    public Long getMktCamResultRelId() {
        return mktCamResultRelId;
    }

    public void setMktCamResultRelId(Long mktCamResultRelId) {
        this.mktCamResultRelId = mktCamResultRelId;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public Long getMktResultId() {
        return mktResultId;
    }

    public void setMktResultId(Long mktResultId) {
        this.mktResultId = mktResultId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(Long createStaff) {
        this.createStaff = createStaff;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getUpdateStaff() {
        return updateStaff;
    }

    public void setUpdateStaff(Long updateStaff) {
        this.updateStaff = updateStaff;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}