package com.zjtelcom.cpct.domain.event;

import java.util.Date;

public class CommonRegion {
    private Long commonRegionId;

    private Long parRegionId;

    private String regionName;

    private String regionPyName;

    private String regionNbr;

    private String regionType;

    private String regionDesc;

    private Long regionLevel;

    private Long regionSort;

    private String provinceNbr;

    private String cityFlag;

    private String statusCd;

    private Long createStaff;

    private Long updateStaff;

    private Date createDate;

    private Date statusDate;

    private Date updateDate;

    private String remark;

    private Long c4RegionId;

    public Long getCommonRegionId() {
        return commonRegionId;
    }

    public void setCommonRegionId(Long commonRegionId) {
        this.commonRegionId = commonRegionId;
    }

    public Long getParRegionId() {
        return parRegionId;
    }

    public void setParRegionId(Long parRegionId) {
        this.parRegionId = parRegionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getRegionPyName() {
        return regionPyName;
    }

    public void setRegionPyName(String regionPyName) {
        this.regionPyName = regionPyName;
    }

    public String getRegionNbr() {
        return regionNbr;
    }

    public void setRegionNbr(String regionNbr) {
        this.regionNbr = regionNbr;
    }

    public String getRegionType() {
        return regionType;
    }

    public void setRegionType(String regionType) {
        this.regionType = regionType;
    }

    public String getRegionDesc() {
        return regionDesc;
    }

    public void setRegionDesc(String regionDesc) {
        this.regionDesc = regionDesc;
    }

    public Long getRegionLevel() {
        return regionLevel;
    }

    public void setRegionLevel(Long regionLevel) {
        this.regionLevel = regionLevel;
    }

    public Long getRegionSort() {
        return regionSort;
    }

    public void setRegionSort(Long regionSort) {
        this.regionSort = regionSort;
    }

    public String getProvinceNbr() {
        return provinceNbr;
    }

    public void setProvinceNbr(String provinceNbr) {
        this.provinceNbr = provinceNbr;
    }

    public String getCityFlag() {
        return cityFlag;
    }

    public void setCityFlag(String cityFlag) {
        this.cityFlag = cityFlag;
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

    public Long getC4RegionId() {
        return c4RegionId;
    }

    public void setC4RegionId(Long c4RegionId) {
        this.c4RegionId = c4RegionId;
    }
}