package com.zjtelcom.cpct.dto.grouping;

import java.util.Date;

public class OrgGridRel {
    private Long orgGridRelId;

    private Long orgId;

    private Long mktId;

    private String xAttrib;

    private String xAttribName;

    private Date createDate;

    private Long createStaff;

    private String statusCd;

    private Date statusDate;

    private Date updateDate;

    private Long updateStaff;

    private String remark;

    public Long getOrgGridRelId() {
        return orgGridRelId;
    }

    public void setOrgGridRelId(Long orgGridRelId) {
        this.orgGridRelId = orgGridRelId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getMktId() {
        return mktId;
    }

    public void setMktId(Long mktId) {
        this.mktId = mktId;
    }

    public String getxAttrib() {
        return xAttrib;
    }

    public void setxAttrib(String xAttrib) {
        this.xAttrib = xAttrib;
    }

    public String getxAttribName() {
        return xAttribName;
    }

    public void setxAttribName(String xAttribName) {
        this.xAttribName = xAttribName;
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