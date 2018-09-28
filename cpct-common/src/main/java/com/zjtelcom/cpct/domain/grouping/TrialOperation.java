package com.zjtelcom.cpct.domain.grouping;

import com.zjtelcom.cpct.BaseEntity;

public class TrialOperation extends BaseEntity {
    /**
     * 试运算标识
     */
    private Long id;
    /**
     * 活动标识
     */
    private Long campaignId;
    /**
     * 活动名称
     */
    private String campaignName;
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
     * 状态  1000 试算中；2000 试算异常；3000 试算成功
     */


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public Long getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Long strategyId) {
        this.strategyId = strategyId;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    public Long getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(Long batchNum) {
        this.batchNum = batchNum;
    }

}