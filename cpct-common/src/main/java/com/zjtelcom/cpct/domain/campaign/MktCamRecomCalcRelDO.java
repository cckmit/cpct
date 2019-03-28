package com.zjtelcom.cpct.domain.campaign;

import java.io.Serializable;
import java.util.Date;

public class MktCamRecomCalcRelDO  implements Serializable {
    private Long evtRecomCalcRelId;

    private Long mktCampaignId;

    private Long algoId;

    private Long mktStrategyConfRuleId;

    private Long algorithmsRulId;

    private Long mktStrategyConfId;

    private Integer priority;

    private String statusCd;

    private Long createStaff;

    private Long updateStaff;

    private Date createDate;

    private Date updateDate;

    private Date statusDate;

    private String remark;

    public Long getEvtRecomCalcRelId() {
        return evtRecomCalcRelId;
    }

    public void setEvtRecomCalcRelId(Long evtRecomCalcRelId) {
        this.evtRecomCalcRelId = evtRecomCalcRelId;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public Long getAlgoId() {
        return algoId;
    }

    public void setAlgoId(Long algoId) {
        this.algoId = algoId;
    }

    public Long getAlgorithmsRulId() {
        return algorithmsRulId;
    }

    public void setAlgorithmsRulId(Long algorithmsRulId) {
        this.algorithmsRulId = algorithmsRulId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public Long getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(Long createStaff) {
        this.createStaff = createStaff;
    }

    public Long getUpdateStaff() {
        return updateStaff;
    }

    public void setUpdateStaff(Long updateStaff) {
        this.updateStaff = updateStaff;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getMktStrategyConfRuleId() {
        return mktStrategyConfRuleId;
    }

    public void setMktStrategyConfRuleId(Long mktStrategyConfRuleId) {
        this.mktStrategyConfRuleId = mktStrategyConfRuleId;
    }

    public Long getMktStrategyConfId() {
        return mktStrategyConfId;
    }

    public void setMktStrategyConfId(Long mktStrategyConfId) {
        this.mktStrategyConfId = mktStrategyConfId;
    }
}