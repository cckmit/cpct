package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;

import java.io.Serializable;
import java.util.Date;

public class Product implements Serializable {
    private Long prodId;

    private String prodNbr;

    private String prodSysNbr;

    private String prodName;

    private String prodDesc;

    private String manageGrade;

    private Date effDate;

    private Date expDate;

    private String prodCompType;

    private String prodFuncType;

    private String billProdType;

    private String prodUseType; //1000	主 //2000 附

    private Long baseOfferId;

    private Long manageRegionId;

    private String statusCd;

    private Long createStaff;

    private Long updateStaff;

    private Date createDate;

    private Date statusDate;

    private Date updateDate;

    private String aliasName;

    private String remark;

    private String grpProdNbr;

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    public String getProdNbr() {
        return prodNbr;
    }

    public void setProdNbr(String prodNbr) {
        this.prodNbr = prodNbr;
    }

    public String getProdSysNbr() {
        return prodSysNbr;
    }

    public void setProdSysNbr(String prodSysNbr) {
        this.prodSysNbr = prodSysNbr;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getProdDesc() {
        return prodDesc;
    }

    public void setProdDesc(String prodDesc) {
        this.prodDesc = prodDesc;
    }

    public String getManageGrade() {
        return manageGrade;
    }

    public void setManageGrade(String manageGrade) {
        this.manageGrade = manageGrade;
    }

    public Date getEffDate() {
        return effDate;
    }

    public void setEffDate(Date effDate) {
        this.effDate = effDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public String getProdCompType() {
        return prodCompType;
    }

    public void setProdCompType(String prodCompType) {
        this.prodCompType = prodCompType;
    }

    public String getProdFuncType() {
        return prodFuncType;
    }

    public void setProdFuncType(String prodFuncType) {
        this.prodFuncType = prodFuncType;
    }

    public String getBillProdType() {
        return billProdType;
    }

    public void setBillProdType(String billProdType) {
        this.billProdType = billProdType;
    }

    public String getProdUseType() {
        return prodUseType;
    }

    public void setProdUseType(String prodUseType) {
        this.prodUseType = prodUseType;
    }

    public Long getBaseOfferId() {
        return baseOfferId;
    }

    public void setBaseOfferId(Long baseOfferId) {
        this.baseOfferId = baseOfferId;
    }

    public Long getManageRegionId() {
        return manageRegionId;
    }

    public void setManageRegionId(Long manageRegionId) {
        this.manageRegionId = manageRegionId;
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

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getGrpProdNbr() {
        return grpProdNbr;
    }

    public void setGrpProdNbr(String grpProdNbr) {
        this.grpProdNbr = grpProdNbr;
    }
}