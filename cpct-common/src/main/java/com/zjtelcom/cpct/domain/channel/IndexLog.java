package com.zjtelcom.cpct.domain.channel;

import java.util.Date;

public class IndexLog {
    private Long indexId;

    private String indexSuffix;

    private String statusCd;

    private Long createStaff;

    private Long updateStaff;

    private Date createDate;

    private Date statusDate;

    private Date updateDate;

    private Date startImportDate;

    private Date endImportDate;

    private String statusImport;

    private String remake;

    public Long getIndexId() {
        return indexId;
    }

    public void setIndexId(Long indexId) {
        this.indexId = indexId;
    }

    public String getIndexSuffix() {
        return indexSuffix;
    }

    public void setIndexSuffix(String indexSuffix) {
        this.indexSuffix = indexSuffix;
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

    public Date getStartImportDate() {
        return startImportDate;
    }

    public void setStartImportDate(Date startImportDate) {
        this.startImportDate = startImportDate;
    }

    public Date getEndImportDate() {
        return endImportDate;
    }

    public void setEndImportDate(Date endImportDate) {
        this.endImportDate = endImportDate;
    }

    public String getStatusImport() {
        return statusImport;
    }

    public void setStatusImport(String statusImport) {
        this.statusImport = statusImport;
    }

    public String getRemake() {
        return remake;
    }

    public void setRemake(String remake) {
        this.remake = remake;
    }
}