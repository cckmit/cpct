package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.Date;

public class ChannelEditVO implements Serializable {
    private Long channelId;
    private String contactChlCode;
    private String contactChlName;
    private String contactChlType;
    private String contactChlDesc;
    private Long regionId;
    private String channelType;
    private Date startTime;
    private Date endTime;


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

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
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
}
