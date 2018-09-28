package com.zjtelcom.cpct.dto.grouping;

import java.io.Serializable;
import java.util.List;

public class TrialOperationVO implements Serializable {
    /**
     * 试运算标识
     */
    private Long trialId;
    /**
     * 活动标识
     */
    private Long campaignId;

    /**
     * 活动名称
     */
    private String campaignName;

    /**
     * 活动类型
     */
    private String campaignType;
    /**
     * 策略标识
     */
    private Long strategyId;

    /**
     * 策略名称
     */
    private String strategyName;
    /**
     * 批次号
     */
    private Long batchNum;

    /**
     * 本地网
     */
    private Long lanId;


    private List<TrialOperationParam> paramList;

    private String[] fieldList;


    public String[] getFieldList() {
        return fieldList;
    }

    public void setFieldList(String[] fieldList) {
        this.fieldList = fieldList;
    }

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

    public Long getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(Long batchNum) {
        this.batchNum = batchNum;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getCampaignType() {
        return campaignType;
    }

    public void setCampaignType(String campaignType) {
        this.campaignType = campaignType;
    }

    public Long getLanId() {
        return lanId;
    }

    public void setLanId(Long lanId) {
        this.lanId = lanId;
    }
}

