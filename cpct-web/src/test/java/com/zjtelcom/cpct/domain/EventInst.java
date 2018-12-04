package com.zjtelcom.cpct.domain;

import java.util.Date;

public class EventInst {
    private Long evtInstId;

    private String evtSerialNum;

    private Long eventId;

    private Date evtCollectTime;

    private String evtContent;

    private String statusCd;

    private Long createStaff;

    private Long updateStaff;

    private Date createDate;

    private Date statusDate;

    private Date updateDate;

    private String remark;

    public Long getEvtInstId() {
        return evtInstId;
    }

    public void setEvtInstId(Long evtInstId) {
        this.evtInstId = evtInstId;
    }

    public String getEvtSerialNum() {
        return evtSerialNum;
    }

    public void setEvtSerialNum(String evtSerialNum) {
        this.evtSerialNum = evtSerialNum;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Date getEvtCollectTime() {
        return evtCollectTime;
    }

    public void setEvtCollectTime(Date evtCollectTime) {
        this.evtCollectTime = evtCollectTime;
    }

    public String getEvtContent() {
        return evtContent;
    }

    public void setEvtContent(String evtContent) {
        this.evtContent = evtContent;
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