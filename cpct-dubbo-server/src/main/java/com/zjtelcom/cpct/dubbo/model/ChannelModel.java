package com.zjtelcom.cpct.dubbo.model;

import java.io.Serializable;
import java.util.Date;

public class ChannelModel implements Serializable {
    private Long contactChlId;//'触点渠道标识'
    private String contactChlCode;//'触点渠道编码',
    private String contactChlName;//'触点渠道名称',
    private String contactChlType;//'记录渠道类型，LOVB=CHN-0017',//100000-直销渠道/110000-实体渠道/120000-电子渠道/130000-转售
    private String contactChlDesc;//'触点渠道描述',
    private Long regionId;//'记录适用区域标识，指定公共管理区域',
    private Long parentId;
    private Date startTime;//可接触时间段
    private Date endTime;
    private String channelType;//人工/被动

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public Long getContactChlId() {
        return contactChlId;
    }

    public void setContactChlId(Long contactChlId) {
        this.contactChlId = contactChlId;
    }

    public String getContactChlCode() {
        return contactChlCode;
    }

    public void setContactChlCode(String contactChlCode) {
        this.contactChlCode = contactChlCode;
    }

    public String getContactChlName() {
        return contactChlName;
    }

    public void setContactChlName(String contactChlName) {
        this.contactChlName = contactChlName;
    }

    public String getContactChlType() {
        return contactChlType;
    }

    public void setContactChlType(String contactChlType) {
        this.contactChlType = contactChlType;
    }

    public String getContactChlDesc() {
        return contactChlDesc;
    }

    public void setContactChlDesc(String contactChlDesc) {
        this.contactChlDesc = contactChlDesc;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
