package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;

import java.util.Date;

public class Channel extends BaseEntity {
    private Long orgId;

    private String channelNbr;

    private String channelName;

    private String channelClass;

    private String salesFirstType;

    private String salesSecondType;

    private String salesThirdType;

    private String chnTypeCd;

    private Long partyId;

    private String channelDesc;

    private String channelLevel;

    private String applyCode;

    private Long regionId;

    private Date createDate;

    private Long createStaff;

    private String statusCd;

    private Date statusDate;

    private Date updateDate;

    private Long updateStaff;

    private Long contactChlId;//'触点渠道标识'
    private String contactChlCode;//'触点渠道编码',
    private String contactChlName;//'触点渠道名称',
    private String contactChlType;//'记录渠道类型，LOVB=CHN-0017',//100000-直销渠道/110000-实体渠道/120000-电子渠道/130000-转售
    private String contactChlDesc;//'触点渠道描述',
//    private Long regionId;//'记录适用区域标识，指定公共管理区域',
    private String channelType;//主动被动
    private Long parentId;
    private Date startTime;//可接触时间段
    private Date endTime;

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getChannelNbr() {
        return channelNbr;
    }

    public void setChannelNbr(String channelNbr) {
        this.channelNbr = channelNbr;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelClass() {
        return channelClass;
    }

    public void setChannelClass(String channelClass) {
        this.channelClass = channelClass;
    }

    public String getSalesFirstType() {
        return salesFirstType;
    }

    public void setSalesFirstType(String salesFirstType) {
        this.salesFirstType = salesFirstType;
    }

    public String getSalesSecondType() {
        return salesSecondType;
    }

    public void setSalesSecondType(String salesSecondType) {
        this.salesSecondType = salesSecondType;
    }

    public String getSalesThirdType() {
        return salesThirdType;
    }

    public void setSalesThirdType(String salesThirdType) {
        this.salesThirdType = salesThirdType;
    }

    public String getChnTypeCd() {
        return chnTypeCd;
    }

    public void setChnTypeCd(String chnTypeCd) {
        this.chnTypeCd = chnTypeCd;
    }

    public Long getPartyId() {
        return partyId;
    }

    public void setPartyId(Long partyId) {
        this.partyId = partyId;
    }

    public String getChannelDesc() {
        return channelDesc;
    }

    public void setChannelDesc(String channelDesc) {
        this.channelDesc = channelDesc;
    }

    public String getChannelLevel() {
        return channelLevel;
    }

    public void setChannelLevel(String channelLevel) {
        this.channelLevel = channelLevel;
    }

    public String getApplyCode() {
        return applyCode;
    }

    public void setApplyCode(String applyCode) {
        this.applyCode = applyCode;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(Long createStaff) {
        this.createStaff = createStaff;
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

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Long getUpdateStaff() {
        return updateStaff;
    }

    public void setUpdateStaff(Long updateStaff) {
        this.updateStaff = updateStaff;
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

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
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

