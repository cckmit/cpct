package com.zjtelcom.cpct.domain;

import java.util.Date;

public class EventInstItem {
    private Long evtInstItemId;

    private Long evtInstId;

    private Long evtItemId;

    private String evtItemCode;

    private String evtItemValue;

    private String statusCd;

    private Long createStaff;

    private Long updateStaff;

    private Date createDate;

    private Date statusDate;

    private Date updateDate;

    private String remark;

    public Long getEvtInstItemId() {
        return evtInstItemId;
    }

    public void setEvtInstItemId(Long evtInstItemId) {
        this.evtInstItemId = evtInstItemId;
    }

    public Long getEvtInstId() {
        return evtInstId;
    }

    public void setEvtInstId(Long evtInstId) {
        this.evtInstId = evtInstId;
    }

    public Long getEvtItemId() {
        return evtItemId;
    }

    public void setEvtItemId(Long evtItemId) {
        this.evtItemId = evtItemId;
    }

    public String getEvtItemCode() {
        return evtItemCode;
    }

    public void setEvtItemCode(String evtItemCode) {
        this.evtItemCode = evtItemCode;
    }

    public String getEvtItemValue() {
        return evtItemValue;
    }

    public void setEvtItemValue(String evtItemValue) {
        this.evtItemValue = evtItemValue;
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