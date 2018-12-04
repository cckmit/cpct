package com.zjtelcom.cpct.domain;

import java.util.Date;

public class EventType {
    private Long evtTypeId;

    private String evtTypeNbr;

    private String evtTypeName;

    private Long parEvtTypeId;

    private String evtTypeDesc;

    private String statusCd;

    private Long createStaff;

    private Long updateStaff;

    private Date createDate;

    private Date statusDate;

    private Date updateDate;

    private String remark;

    public Long getEvtTypeId() {
        return evtTypeId;
    }

    public void setEvtTypeId(Long evtTypeId) {
        this.evtTypeId = evtTypeId;
    }

    public String getEvtTypeNbr() {
        return evtTypeNbr;
    }

    public void setEvtTypeNbr(String evtTypeNbr) {
        this.evtTypeNbr = evtTypeNbr;
    }

    public String getEvtTypeName() {
        return evtTypeName;
    }

    public void setEvtTypeName(String evtTypeName) {
        this.evtTypeName = evtTypeName;
    }

    public Long getParEvtTypeId() {
        return parEvtTypeId;
    }

    public void setParEvtTypeId(Long parEvtTypeId) {
        this.parEvtTypeId = parEvtTypeId;
    }

    public String getEvtTypeDesc() {
        return evtTypeDesc;
    }

    public void setEvtTypeDesc(String evtTypeDesc) {
        this.evtTypeDesc = evtTypeDesc;
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