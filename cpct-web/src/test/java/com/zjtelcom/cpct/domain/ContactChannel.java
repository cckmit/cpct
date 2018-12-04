package com.zjtelcom.cpct.domain;

import java.util.Date;

public class ContactChannel {
    private Long contactChlId;

    private String contactChlCode;

    private String contactChlName;

    private String contactChlType;

    private String contactChlDesc;

    private String statusCd;

    private Long createStaff;

    private Long updateStaff;

    private Long regionId;

    private Date statusDate;

    private Date updateDate;

    private Date createDate;

    private String remark;

    public Long getContactChlId() {
        return contactChlId;
    }

    public void setContactChlId(Long contactChlId) {
        this.contactChlId = contactChlId;
    }

    public String getContactChlCode() {
        return contactChlCode;
    }

    public void setContactChlCode(String contactChlCode) {
        this.contactChlCode = contactChlCode;
    }

    public String getContactChlName() {
        return contactChlName;
    }

    public void setContactChlName(String contactChlName) {
        this.contactChlName = contactChlName;
    }

    public String getContactChlType() {
        return contactChlType;
    }

    public void setContactChlType(String contactChlType) {
        this.contactChlType = contactChlType;
    }

    public String getContactChlDesc() {
        return contactChlDesc;
    }

    public void setContactChlDesc(String contactChlDesc) {
        this.contactChlDesc = contactChlDesc;
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

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}