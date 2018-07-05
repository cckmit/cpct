package com.zjtelcom.cpct.dto.strategy;

import java.util.Date;

public class MktStrategyConf {
    private Long mktStrategyConfId;

    private String mktStrategyConfName;

    private Date beginTime;

    private Date endTime;

    private Long channelsId;

    private Long createStaff;

    private Date createDate;

    private Long updateStaff;

    private Date updateDate;

    public Long getMktStrategyConfId() {
        return mktStrategyConfId;
    }

    public void setMktStrategyConfId(Long mktStrategyConfId) {
        this.mktStrategyConfId = mktStrategyConfId;
    }

    public String getMktStrategyConfName() {
        return mktStrategyConfName;
    }

    public void setMktStrategyConfName(String mktStrategyConfName) {
        this.mktStrategyConfName = mktStrategyConfName;
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

    public Long getChannelsId() {
        return channelsId;
    }

    public void setChannelsId(Long channelsId) {
        this.channelsId = channelsId;
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
}