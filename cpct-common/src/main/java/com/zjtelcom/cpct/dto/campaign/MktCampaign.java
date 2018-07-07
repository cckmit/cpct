package com.zjtelcom.cpct.dto.campaign;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @Author:sunpeng
 * @Descirption:营销活动服务对接基本dto父对象
 * @Date: 2018/6/26.
 */
public class MktCampaign {

    /**
     * 营销活动标识
     */
    private Long mktCampaignId;

    /**
     * 营销策略标识
     */
    private Long strategyId;

    /**
     * 营销活动名称
     */
    private String mktCampaignName;

    /**
     * 计划开始时间
     */
    private Date planBeginTime;

    /**
     * 计划结束时间
     */
    private Date planEndTime;

    /**
     * 实际开始时间
     */
    private Date beginTime;

    /**
     * 实际结束时间
     */
    private Date endTime;

    /**
     * 营销活动分类
     */
    private String mktCampaignType;

    /**
     * 营销活动编号
     */
    private String mktActivityNbr;

    /**
     * 营销活动目标
     */
    private String mktActivityTarget;

    /**
     * 营销活动描述
     */
    private String mktCampaignDesc;

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public Long getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Long strategyId) {
        this.strategyId = strategyId;
    }

    public String getMktCampaignName() {
        return mktCampaignName;
    }

    public void setMktCampaignName(String mktCampaignName) {
        this.mktCampaignName = mktCampaignName;
    }

    public Date getPlanBeginTime() {
        return planBeginTime;
    }

    public void setPlanBeginTime(Date planBeginTime) {
        this.planBeginTime = planBeginTime;
    }

    public Date getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getMktCampaignType() {
        return mktCampaignType;
    }

    public void setMktCampaignType(String mktCampaignType) {
        this.mktCampaignType = mktCampaignType;
    }

    public String getMktActivityNbr() {
        return mktActivityNbr;
    }

    public void setMktActivityNbr(String mktActivityNbr) {
        this.mktActivityNbr = mktActivityNbr;
    }

    public String getMktActivityTarget() {
        return mktActivityTarget;
    }

    public void setMktActivityTarget(String mktActivityTarget) {
        this.mktActivityTarget = mktActivityTarget;
    }

    public String getMktCampaignDesc() {
        return mktCampaignDesc;
    }

    public void setMktCampaignDesc(String mktCampaignDesc) {
        this.mktCampaignDesc = mktCampaignDesc;
    }
}