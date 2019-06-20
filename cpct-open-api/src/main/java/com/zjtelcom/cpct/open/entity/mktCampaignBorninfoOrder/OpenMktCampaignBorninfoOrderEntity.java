package com.zjtelcom.cpct.open.entity.mktCampaignBorninfoOrder;

import com.zjtelcom.cpct.open.entity.mktCampaignEntity.OpenMktCampaignRelEntity;

import java.util.Date;
import java.util.List;

public class OpenMktCampaignBorninfoOrderEntity {

    private Long offerSceneRelId;
    private Long mktCampaignId;
    private String obiType;
    private Long obiId;
    private String relType;
    private Date effDate;
    private Date expDate;
    private String statusCd;
    private Date statusDate;
    private Long createStaff;
    private Date createDate;
    private Long updateStaff;
    private Date updateDate;
    private String remark;
    private List<OpenMktCampaignRelEntity> mktCampaignRels;
    //private List<OpenMktAlgorithmsEntity> mktAlgorithms;

    public Long getOfferSceneRelId() {
        return offerSceneRelId;
    }

    public void setOfferSceneRelId(Long offerSceneRelId) {
        this.offerSceneRelId = offerSceneRelId;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public String getObiType() {
        return obiType;
    }

    public void setObiType(String obiType) {
        this.obiType = obiType;
    }

    public Long getObiId() {
        return obiId;
    }

    public void setObiId(Long obiId) {
        this.obiId = obiId;
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

    public Long getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(Long createStaff) {
        this.createStaff = createStaff;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getUpdateStaff() {
        return updateStaff;
    }

    public void setUpdateStaff(Long updateStaff) {
        this.updateStaff = updateStaff;
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

    public List<OpenMktCampaignRelEntity> getMktCampaignRels() {
        return mktCampaignRels;
    }

    public void setMktCampaignRels(List<OpenMktCampaignRelEntity> mktCampaignRels) {
        this.mktCampaignRels = mktCampaignRels;
    }
}
