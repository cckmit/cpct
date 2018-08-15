package com.zjtelcom.cpct.dto.grouping;

import java.io.Serializable;

public class TrialOperationParam implements Serializable {
    private Long ruleId;
    private String rule;
    private Long batchNum;


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
}

