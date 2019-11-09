package com.zjtelcom.cpct.open.entity.mktCamItem;

import com.zjtelcom.cpct.open.entity.tarGrp.OpenTarGrp;

import java.util.List;

public class OpenMktCamItem{

    private Integer mktCamItemId;
    private Integer mktCampaignId;
    private String mktActivityNbr;
    private String itemType;
    private Integer itemId;
    private String itemNbr;
    private Integer priority;
    private Integer itemGroup;
    private String statusCd;
    private String statusDate;
    private String remark;
    private Integer lanId;
    private List<OpenTarGrp> tarGrps;

    public Integer getMktCamItemId() {
        return mktCamItemId;
    }

    public void setMktCamItemId(Integer mktCamItemId) {
        this.mktCamItemId = mktCamItemId;
    }

    public Integer getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Integer mktCampaignId) {
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

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemNbr() {
        return itemNbr;
    }

    public void setItemNbr(String itemNbr) {
        this.itemNbr = itemNbr;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(Integer itemGroup) {
        this.itemGroup = itemGroup;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getLanId() {
        return lanId;
    }

    public void setLanId(Integer lanId) {
        this.lanId = lanId;
    }

    public List<OpenTarGrp> getTarGrps() {
        return tarGrps;
    }

    public void setTarGrps(List<OpenTarGrp> tarGrps) {
        this.tarGrps = tarGrps;
    }
}
