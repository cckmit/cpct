package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.List;

public class ProductParam implements Serializable {
    private Long campaignId;
    private List<Long> idList;
    private Long strategyRuleId;
    private String itemType;
    private String statusCd;
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Long getStrategyRuleId() {
        return strategyRuleId;
    }

    public void setStrategyRuleId(Long strategyRuleId) {
        this.strategyRuleId = strategyRuleId;
    }

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }
}
