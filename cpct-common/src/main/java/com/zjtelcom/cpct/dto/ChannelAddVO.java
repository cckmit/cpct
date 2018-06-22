package com.zjtelcom.cpct.dto;

import java.io.Serializable;

public class ChannelAddVO implements Serializable {

    private String contactChlCode;
    private String contactChlName;
    private String contactChlType;
    private String contactChlDesc;
    private Long regionId;

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
