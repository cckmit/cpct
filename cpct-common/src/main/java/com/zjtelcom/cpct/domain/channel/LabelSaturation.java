package com.zjtelcom.cpct.domain.channel;

import java.util.Date;

public class LabelSaturation {
    private Long labelSaturationId;

    private String labelCode;

    private String saturationBatchNumber;

    private Long bigdataSaturation;

    private Long esSaturation;

    private String comparingResults;

    private String statusCd;

    private Long createStaff;

    private Long updateStaff;

    private Date createDate;

    private Date statusDate;

    private Date updateDate;

    private String remake;

    public Long getLabelSaturationId() {
        return labelSaturationId;
    }

    public void setLabelSaturationId(Long labelSaturationId) {
        this.labelSaturationId = labelSaturationId;
    }

    public String getLabelCode() {
        return labelCode;
    }

    public void setLabelCode(String labelCode) {
        this.labelCode = labelCode;
    }

    public String getSaturationBatchNumber() {
        return saturationBatchNumber;
    }

    public void setSaturationBatchNumber(String saturationBatchNumber) {
        this.saturationBatchNumber = saturationBatchNumber;
    }

    public Long getBigdataSaturation() {
        return bigdataSaturation;
    }

    public void setBigdataSaturation(Long bigdataSaturation) {
        this.bigdataSaturation = bigdataSaturation;
    }

    public Long getEsSaturation() {
        return esSaturation;
    }

    public void setEsSaturation(Long esSaturation) {
        this.esSaturation = esSaturation;
    }

    public String getComparingResults() {
        return comparingResults;
    }

    public void setComparingResults(String comparingResults) {
        this.comparingResults = comparingResults;
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

    public String getRemake() {
        return remake;
    }

    public void setRemake(String remake) {
        this.remake = remake;
    }
}