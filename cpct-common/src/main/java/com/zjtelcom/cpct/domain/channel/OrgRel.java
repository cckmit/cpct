package com.zjtelcom.cpct.domain.channel;

import java.util.Date;

public class OrgRel {
    private Long orgRelId;

    private Long aOrgId;

    private Long zOrgId;

    private String orgRelType;

    private Date effDate;

    private Date expDate;

    private String statusCd;

    private Date statusDate;

    private Date createDate;

    private Long createStaff;

    private Date updateDate;

    private Long updateStaff;

    private String remark;

    public Long getOrgRelId() {
        return orgRelId;
    }

    public void setOrgRelId(Long orgRelId) {
        this.orgRelId = orgRelId;
    }

    public Long getaOrgId() {
        return aOrgId;
    }

    public void setaOrgId(Long aOrgId) {
        this.aOrgId = aOrgId;
    }

    public Long getzOrgId() {
        return zOrgId;
    }

    public void setzOrgId(Long zOrgId) {
        this.zOrgId = zOrgId;
    }

    public String getOrgRelType() {
        return orgRelType;
    }

    public void setOrgRelType(String orgRelType) {
        this.orgRelType = orgRelType;
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

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(Long createStaff) {
        this.createStaff = createStaff;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Long getUpdateStaff() {
        return updateStaff;
    }

    public void setUpdateStaff(Long updateStaff) {
        this.updateStaff = updateStaff;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}