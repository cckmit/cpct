package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;

import java.io.Serializable;
import java.util.Date;

public class OfferProdRel extends BaseEntity {
    private Long offerProdRelId;

    private Long parOfferProdRelId;

    private Long offerId;

    private Long prodId;

    private String relType;

    private Date effDate;

    private Date expDate;

    private String prodExistType;

    private Long roleId;

    private Long applyRegionId;


    public Long getOfferProdRelId() {
        return offerProdRelId;
    }

    public void setOfferProdRelId(Long offerProdRelId) {
        this.offerProdRelId = offerProdRelId;
    }

    public Long getParOfferProdRelId() {
        return parOfferProdRelId;
    }

    public void setParOfferProdRelId(Long parOfferProdRelId) {
        this.parOfferProdRelId = parOfferProdRelId;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    public String getRelType() {
        return relType;
    }

    public void setRelType(String relType) {
        this.relType = relType;
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

    public String getProdExistType() {
        return prodExistType;
    }

    public void setProdExistType(String prodExistType) {
        this.prodExistType = prodExistType;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getApplyRegionId() {
        return applyRegionId;
    }

    public void setApplyRegionId(Long applyRegionId) {
        this.applyRegionId = applyRegionId;
    }

}