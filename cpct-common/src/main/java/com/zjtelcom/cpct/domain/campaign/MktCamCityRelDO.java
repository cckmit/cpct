package com.zjtelcom.cpct.domain.campaign;

import java.util.Date;

public class MktCamCityRelDO {
    private Long mktCamCityRelId;

    private Long mktCampaignId;

    private Long cityId;

    private Long createStaff;

    private Date createDate;

    private Long updateStaff;

    private Date updateDate;

    public Long getMktCamCityRelId() {
        return mktCamCityRelId;
    }

    public void setMktCamCityRelId(Long mktCamCityRelId) {
        this.mktCamCityRelId = mktCamCityRelId;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
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