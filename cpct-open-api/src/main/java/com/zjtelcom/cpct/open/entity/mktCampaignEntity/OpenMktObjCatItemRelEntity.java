package com.zjtelcom.cpct.open.entity.mktCampaignEntity;

import java.util.Date;

public class OpenMktObjCatItemRelEntity {

    private String catalogItemId;//目录节点标识

    private String objType;//对象类型

    private Long relId;//对象目录节点关系标识

    private String statusCd;//状态

    private Long objId;

    private Long createStaff;

    private Long updateStaff;

    private Date createDate;

    private Date statusDate;

    private Date updateDate;

    private String remark;

    private String objNbr;

    private String catalogItemNbr;

    private String catalogItemName;


    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
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

    public String getObjNbr() {
        return objNbr;
    }

    public void setObjNbr(String objNbr) {
        this.objNbr = objNbr;
    }

    public String getCatalogItemNbr() {
        return catalogItemNbr;
    }

    public void setCatalogItemNbr(String catalogItemNbr) {
        this.catalogItemNbr = catalogItemNbr;
    }

    public String getCatalogItemName() {
        return catalogItemName;
    }

    public void setCatalogItemName(String catalogItemName) {
        this.catalogItemName = catalogItemName;
    }

    public String getCatalogItemId() {
        return catalogItemId;
    }

    public void setCatalogItemId(String catalogItemId) {
        this.catalogItemId = catalogItemId;
    }

    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        this.objType = objType;
    }

    public Long getRelId() {
        return relId;
    }

    public void setRelId(Long relId) {
        this.relId = relId;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
}
