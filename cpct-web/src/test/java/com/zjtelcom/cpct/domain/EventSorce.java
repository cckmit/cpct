package com.zjtelcom.cpct.domain;

import java.util.Date;

public class EventSorce {
    private Long evtSrcId;

    private String evtSrcCode;

    private String evtSrcName;

    private String evtSrcDesc;

    private String statusCd;

    private Date statusDate;

    private Long createStaff;

    private Date createDate;

    private Long updateStaff;

    private Date updateDate;

    private Long regionId;

    private String remark;

    public Long getEvtSrcId() {
        return evtSrcId;
    }

    public void setEvtSrcId(Long evtSrcId) {
        this.evtSrcId = evtSrcId;
    }

    public String getEvtSrcCode() {
        return evtSrcCode;
    }

    public void setEvtSrcCode(String evtSrcCode) {
        this.evtSrcCode = evtSrcCode;
    }

    public String getEvtSrcName() {
        return evtSrcName;
    }

    public void setEvtSrcName(String evtSrcName) {
        this.evtSrcName = evtSrcName;
    }

    public String getEvtSrcDesc() {
        return evtSrcDesc;
    }

    public void setEvtSrcDesc(String evtSrcDesc) {
        this.evtSrcDesc = evtSrcDesc;
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

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}