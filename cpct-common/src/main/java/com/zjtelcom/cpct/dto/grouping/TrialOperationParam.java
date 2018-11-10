package com.zjtelcom.cpct.dto.grouping;

import com.zjtelcom.cpct.domain.channel.LabelResult;
import com.zjtelcom.cpct.domain.channel.MktProductRule;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;

import java.io.Serializable;
import java.util.List;

public class TrialOperationParam implements Serializable {
    /**
     * 规则标识
     */
    private Long ruleId;

    /**
     * 规则名字
     */
    private String ruleName;
    /**
     * 规则表达式
     */
    private String rule;
    /**
     * 批次号
     */
    private Long batchNum;

    /**
     * 目标分群id
     */
    private Long tarGrpId;

    private List<LabelResult> labelResultList;

    /**
     * 销售品集合
     */
    private List<MktProductRule> mktProductRuleList;

    /**
     * 推送渠道集合
     */
    private List<MktCamChlConfDetail> mktCamChlConfDetailList;


    public List<LabelResult> getLabelResultList() {
        return labelResultList;
    }

    public void setLabelResultList(List<LabelResult> labelResultList) {
        this.labelResultList = labelResultList;
    }

    public Long getTarGrpId() {
        return tarGrpId;
    }

    public void setTarGrpId(Long tarGrpId) {
        this.tarGrpId = tarGrpId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public Long getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(Long batchNum) {
        this.batchNum = batchNum;
    }

    public List<MktProductRule> getMktProductRuleList() {
        return mktProductRuleList;
    }

    public void setMktProductRuleList(List<MktProductRule> mktProductRuleList) {
        this.mktProductRuleList = mktProductRuleList;
    }

    public List<MktCamChlConfDetail> getMktCamChlConfDetailList() {
        return mktCamChlConfDetailList;
    }

    public void setMktCamChlConfDetailList(List<MktCamChlConfDetail> mktCamChlConfDetailList) {
        this.mktCamChlConfDetailList = mktCamChlConfDetailList;
    }
}

