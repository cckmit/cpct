package com.zjtelcom.cpct.dto.grouping;

import java.io.Serializable;
import java.util.List;

public class TrialOperationVO implements Serializable {
    private Long trialId;
    private Long campaignId;
    private Long strategyId;
    private List<TrialOperationParam> paramList;


    public Long getTrialId() {
        return trialId;
    }

    public void setTrialId(Long trialId) {
        this.trialId = trialId;
    }

    public List<TrialOperationParam> getParamList() {
        return paramList;
    }

    public void setParamList(List<TrialOperationParam> paramList) {
        this.paramList = paramList;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Long getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Long strategyId) {
        this.strategyId = strategyId;
    }


}
