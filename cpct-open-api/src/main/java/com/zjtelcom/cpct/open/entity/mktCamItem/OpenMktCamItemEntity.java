package com.zjtelcom.cpct.open.entity.mktCamItem;

import java.util.Date;

public class OpenMktCamItemEntity {

    private String actType;
    private Long mktCamItemId;
    private Long mktCampaignId;
    private String mktActivityNbr;
    private String itemType;
    private String itemNbr;
    private Long itemId;
    private Long priority;
    private Long itemGroup;
    private String statusCd;
    private Date statusDate;
    private Long createStaff;
    private Date createDate;
    private Long updateStaff;
    private Date updateDate;
    private String remark;
    private Long lanId;

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public Long getMktCamItemId() {
        return mktCamItemId;
    }

    public void setMktCamItemId(Long mktCamItemId) {
        this.mktCamItemId = mktCamItemId;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public String getMktActivityNbr() {
        return mktActivityNbr;
    }

    public void setMktActivityNbr(String mktActivityNbr) {
        this.mktActivityNbr = mktActivityNbr;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemNbr() {
        return itemNbr;
    }

    public void setItemNbr(String itemNbr) {
        this.itemNbr = itemNbr;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public Long getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(Long itemGroup) {
        this.itemGroup = itemGroup;
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

    public Long getLanId() {
        return lanId;
    }

    public void setLanId(Long lanId) {
        this.lanId = lanId;
    }
}
