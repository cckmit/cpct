package com.zjtelcom.cpct.domain.channel;

import java.util.Date;

public class CatalogItem {
    private Long catalogItemId;

    private Long catalogId;

    private Long parCatalogItemId;

    private String catalogItemName;

    private String catalogItemDesc;

    private String catalogItemType;

    private String catalogItemNbr;

    private Integer catalogItemSort;

    private String statusCd;

    private Long createStaff;

    private Long updateStaff;

    private Date createDate;

    private Date statusDate;

    private Date updateDate;

    private String remark;

    public Long getCatalogItemId() {
        return catalogItemId;
    }

    public void setCatalogItemId(Long catalogItemId) {
        this.catalogItemId = catalogItemId;
    }

    public Long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Long catalogId) {
        this.catalogId = catalogId;
    }

    public Long getParCatalogItemId() {
        return parCatalogItemId;
    }

    public void setParCatalogItemId(Long parCatalogItemId) {
        this.parCatalogItemId = parCatalogItemId;
    }

    public String getCatalogItemName() {
        return catalogItemName;
    }

    public void setCatalogItemName(String catalogItemName) {
        this.catalogItemName = catalogItemName;
    }

    public String getCatalogItemDesc() {
        return catalogItemDesc;
    }

    public void setCatalogItemDesc(String catalogItemDesc) {
        this.catalogItemDesc = catalogItemDesc;
    }

    public String getCatalogItemType() {
        return catalogItemType;
    }

    public void setCatalogItemType(String catalogItemType) {
        this.catalogItemType = catalogItemType;
    }

    public String getCatalogItemNbr() {
        return catalogItemNbr;
    }

    public void setCatalogItemNbr(String catalogItemNbr) {
        this.catalogItemNbr = catalogItemNbr;
    }

    public Integer getCatalogItemSort() {
        return catalogItemSort;
    }

    public void setCatalogItemSort(Integer catalogItemSort) {
        this.catalogItemSort = catalogItemSort;
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
}