package com.zjtelcom.cpct.domain.grouping;

import com.zjtelcom.cpct.BaseEntity;

import java.util.Date;

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

    private Date startTime;
    private Date endTime;
    private String beforeNum;
    private String endNum;
    private String subNum;
    private String specifiedNum;
    private String specifiedFileNum;

    public String getSpecifiedFileNum() {
        return specifiedFileNum;
    }

    public void setSpecifiedFileNum(String specifiedFileNum) {
        this.specifiedFileNum = specifiedFileNum;
    }

    public String getSpecifiedNum() {
        return specifiedNum;
    }

    public void setSpecifiedNum(String specifiedNum) {
        this.specifiedNum = specifiedNum;
    }


    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getBeforeNum() {
        return beforeNum;
    }

    public void setBeforeNum(String beforeNum) {
        this.beforeNum = beforeNum;
    }

    public String getEndNum() {
        return endNum;
    }

    public void setEndNum(String endNum) {
        this.endNum = endNum;
    }

    public String getSubNum() {
        return subNum;
    }

    public void setSubNum(String subNum) {
        this.subNum = subNum;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

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