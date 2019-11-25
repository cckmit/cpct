package com.zjtelcom.cpct.domain.campaign;

import java.util.Date;

public class MktDttsLog {

    private Long dttsLogId;
    private String dttsType;
    private String dttsState;
    private Date beginTime;
    private Date endTime;
    private String dttsResult;
    private Date statusDate;
    private Long createStaff;
    private Date createDate;
    private Long updateStaff;
    private Date updateDate;
    private String remark;
    private String remarkOne;
    private String remarkTwo;

    public Long getDttsLogId() {
        return dttsLogId;
    }

    public void setDttsLogId(Long dttsLogId) {
        this.dttsLogId = dttsLogId;
    }

    public String getDttsType() {
        return dttsType;
    }

    public void setDttsType(String dttsType) {
        this.dttsType = dttsType;
    }

    public String getDttsState() {
        return dttsState;
    }

    public void setDttsState(String dttsState) {
        this.dttsState = dttsState;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getDttsResult() {
        return dttsResult;
    }

    public void setDttsResult(String dttsResult) {
        this.dttsResult = dttsResult;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemarkOne() {
        return remarkOne;
    }

    public void setRemarkOne(String remarkOne) {
        this.remarkOne = remarkOne;
    }

    public String getRemarkTwo() {
        return remarkTwo;
    }

    public void setRemarkTwo(String remarkTwo) {
        this.remarkTwo = remarkTwo;
    }
}
