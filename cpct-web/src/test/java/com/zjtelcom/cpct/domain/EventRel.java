package com.zjtelcom.cpct.domain;

import java.util.Date;

public class EventRel {
    private Long complexEvtRelaId;

    private Long aEvtId;

    private Long zEvtId;

    private String sort;

    private String statusCd;

    private Long createStaff;

    private Long updateStaff;

    private Date createDate;

    private Date statusDate;

    private Date updateDate;

    private String remark;

    public Long getComplexEvtRelaId() {
        return complexEvtRelaId;
    }

    public void setComplexEvtRelaId(Long complexEvtRelaId) {
        this.complexEvtRelaId = complexEvtRelaId;
    }

    public Long getaEvtId() {
        return aEvtId;
    }

    public void setaEvtId(Long aEvtId) {
        this.aEvtId = aEvtId;
    }

    public Long getzEvtId() {
        return zEvtId;
    }

    public void setzEvtId(Long zEvtId) {
        this.zEvtId = zEvtId;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
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