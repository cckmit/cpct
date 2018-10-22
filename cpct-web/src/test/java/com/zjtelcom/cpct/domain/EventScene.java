package com.zjtelcom.cpct.domain;

import java.util.Date;

public class EventScene {
    private Long eventSceneId;

    private String eventSceneNbr;

    private String eventSceneName;

    private String eventSceneDesc;

    private Long eventId;

    private Long extEventSceneId;

    private String statusCd;

    private Date statusDate;

    private Long createStaff;

    private Long updateStaff;

    private Date updateDate;

    private Date createDate;

    private String remark;

    private String contactEvtCode;

    public Long getEventSceneId() {
        return eventSceneId;
    }

    public void setEventSceneId(Long eventSceneId) {
        this.eventSceneId = eventSceneId;
    }

    public String getEventSceneNbr() {
        return eventSceneNbr;
    }

    public void setEventSceneNbr(String eventSceneNbr) {
        this.eventSceneNbr = eventSceneNbr;
    }

    public String getEventSceneName() {
        return eventSceneName;
    }

    public void setEventSceneName(String eventSceneName) {
        this.eventSceneName = eventSceneName;
    }

    public String getEventSceneDesc() {
        return eventSceneDesc;
    }

    public void setEventSceneDesc(String eventSceneDesc) {
        this.eventSceneDesc = eventSceneDesc;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getExtEventSceneId() {
        return extEventSceneId;
    }

    public void setExtEventSceneId(Long extEventSceneId) {
        this.extEventSceneId = extEventSceneId;
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

    public String getContactEvtCode() {
        return contactEvtCode;
    }

    public void setContactEvtCode(String contactEvtCode) {
        this.contactEvtCode = contactEvtCode;
    }
}