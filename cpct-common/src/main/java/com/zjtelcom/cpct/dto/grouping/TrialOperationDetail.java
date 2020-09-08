package com.zjtelcom.cpct.dto.grouping;

import com.zjtelcom.cpct.domain.grouping.TrialOperation;

import java.io.Serializable;

public class TrialOperationDetail extends TrialOperation implements Serializable {
    private String cost;
    private Long ruleId;
    private String ruleName;
    private String flg;


    public String getFlg() {
        return flg;
    }

    public void setFlg(String flg) {
        this.flg = flg;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
