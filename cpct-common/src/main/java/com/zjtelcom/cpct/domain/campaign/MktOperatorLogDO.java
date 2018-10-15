package com.zjtelcom.cpct.domain.campaign;

import java.io.Serializable;
import java.util.Date;

public class MktOperatorLogDO implements Serializable {

    private Long operationId;

    private Long mktCampaignId;

    private String mktActivityNbr;

    private String mktCampaignName;

    private String mktCampaignStateBefore;

    private String mktCampaignStateAfter;

    private String operatorAccount;

    private String operatorType;

    private Date operatorDate;

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
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

    public String getMktCampaignName() {
        return mktCampaignName;
    }

    public void setMktCampaignName(String mktCampaignName) {
        this.mktCampaignName = mktCampaignName;
    }

    public String getMktCampaignStateBefore() {
        return mktCampaignStateBefore;
    }

    public void setMktCampaignStateBefore(String mktCampaignStateBefore) {
        this.mktCampaignStateBefore = mktCampaignStateBefore;
    }

    public String getMktCampaignStateAfter() {
        return mktCampaignStateAfter;
    }

    public void setMktCampaignStateAfter(String mktCampaignStateAfter) {
        this.mktCampaignStateAfter = mktCampaignStateAfter;
    }

    public String getOperatorAccount() {
        return operatorAccount;
    }

    public void setOperatorAccount(String operatorAccount) {
        this.operatorAccount = operatorAccount;
    }

    public String getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType;
    }

    public Date getOperatorDate() {
        return operatorDate;
    }

    public void setOperatorDate(Date operatorDate) {
        this.operatorDate = operatorDate;
    }
}
