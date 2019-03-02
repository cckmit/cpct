package com.zjtelcom.cpct.domain.channel;

import java.io.Serializable;
import java.util.Date;

public class Organization implements Serializable {
    private Long orgId;

    private Long partyId;

    private String orgCode;

    private String orgName;

    private Long regionId;

    private String orgType;

    private String orgSubtype;

    private String villageFlag;

    private Long parentOrgId;

    private Integer orgLevel;

    private Integer orgIndex;

    private String salesorgCode;

    private Short divorgFlag;

    private Date createDate;

    private Long createStaff;

    private String statusCd;

    private Date statusDate;

    private Date updateDate;

    private Long updateStaff;

    private String orgDivision;

    private Long orgId4a;

    private String orgContractType;

    private String orgAreaLevel;

    private String remark;

    private String orgDesc;

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getPartyId() {
        return partyId;
    }

    public void setPartyId(Long partyId) {
        this.partyId = partyId;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getOrgSubtype() {
        return orgSubtype;
    }

    public void setOrgSubtype(String orgSubtype) {
        this.orgSubtype = orgSubtype;
    }

    public String getVillageFlag() {
        return villageFlag;
    }

    public void setVillageFlag(String villageFlag) {
        this.villageFlag = villageFlag;
    }

    public Long getParentOrgId() {
        return parentOrgId;
    }

    public void setParentOrgId(Long parentOrgId) {
        this.parentOrgId = parentOrgId;
    }

    public Integer getOrgLevel() {
        return orgLevel;
    }

    public void setOrgLevel(Integer orgLevel) {
        this.orgLevel = orgLevel;
    }

    public Integer getOrgIndex() {
        return orgIndex;
    }

    public void setOrgIndex(Integer orgIndex) {
        this.orgIndex = orgIndex;
    }

    public String getSalesorgCode() {
        return salesorgCode;
    }

    public void setSalesorgCode(String salesorgCode) {
        this.salesorgCode = salesorgCode;
    }

    public Short getDivorgFlag() {
        return divorgFlag;
    }

    public void setDivorgFlag(Short divorgFlag) {
        this.divorgFlag = divorgFlag;
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

    public String getOrgDivision() {
        return orgDivision;
    }

    public void setOrgDivision(String orgDivision) {
        this.orgDivision = orgDivision;
    }

    public Long getOrgId4a() {
        return orgId4a;
    }

    public void setOrgId4a(Long orgId4a) {
        this.orgId4a = orgId4a;
    }

    public String getOrgContractType() {
        return orgContractType;
    }

    public void setOrgContractType(String orgContractType) {
        this.orgContractType = orgContractType;
    }

    public String getOrgAreaLevel() {
        return orgAreaLevel;
    }

    public void setOrgAreaLevel(String orgAreaLevel) {
        this.orgAreaLevel = orgAreaLevel;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOrgDesc() {
        return orgDesc;
    }

    public void setOrgDesc(String orgDesc) {
        this.orgDesc = orgDesc;
    }
}