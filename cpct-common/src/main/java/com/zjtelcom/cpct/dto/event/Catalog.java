package com.zjtelcom.cpct.dto.event;

import java.io.Serializable;
import java.util.Date;

public class Catalog implements Serializable {
    private Long catalogId;

    private String catalogName;

    private String catalogType;

    private String catalogDesc;

    private String catalogNbr;

    private String catalogUsage;

    private Long applyRegionId;

    private String statusCd;

    private Long createStaff;

    private Long updateStaff;

    private Date createDate;

    private Date updateDate;

    private Date statusDate;

    private String remark;

    public Long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Long catalogId) {
        this.catalogId = catalogId;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName == null ? null : catalogName.trim();
    }

    public String getCatalogType() {
        return catalogType;
    }

    public void setCatalogType(String catalogType) {
        this.catalogType = catalogType == null ? null : catalogType.trim();
    }

    public String getCatalogDesc() {
        return catalogDesc;
    }

    public void setCatalogDesc(String catalogDesc) {
        this.catalogDesc = catalogDesc == null ? null : catalogDesc.trim();
    }

    public String getCatalogNbr() {
        return catalogNbr;
    }

    public void setCatalogNbr(String catalogNbr) {
        this.catalogNbr = catalogNbr == null ? null : catalogNbr.trim();
    }

    public String getCatalogUsage() {
        return catalogUsage;
    }

    public void setCatalogUsage(String catalogUsage) {
        this.catalogUsage = catalogUsage == null ? null : catalogUsage.trim();
    }

    public Long getApplyRegionId() {
        return applyRegionId;
    }

    public void setApplyRegionId(Long applyRegionId) {
        this.applyRegionId = applyRegionId;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd == null ? null : statusCd.trim();
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

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

	public Catalog() {
		super();
	}

	public Catalog(Long catalogId, String catalogName, String catalogType,
                   String catalogDesc, String catalogNbr, String catalogUsage,
                   Long applyRegionId, String statusCd, Long createStaff,
                   Long updateStaff, Date createDate, Date updateDate,
                   Date statusDate, String remark) {
		super();
		this.catalogId = catalogId;
		this.catalogName = catalogName;
		this.catalogType = catalogType;
		this.catalogDesc = catalogDesc;
		this.catalogNbr = catalogNbr;
		this.catalogUsage = catalogUsage;
		this.applyRegionId = applyRegionId;
		this.statusCd = statusCd;
		this.createStaff = createStaff;
		this.updateStaff = updateStaff;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.statusDate = statusDate;
		this.remark = remark;
	}
    
    
}