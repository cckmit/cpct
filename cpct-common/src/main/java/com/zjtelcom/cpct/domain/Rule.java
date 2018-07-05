package com.zjtelcom.cpct.domain;

import java.util.List;

public class Rule {


    private String type;

    private List<RuleDetail> listData;

    private Rule ruleChildren;


    public Rule getRuleChildren() {
        return ruleChildren;
    }

    public void setRuleChildren(Rule ruleChildren) {
        this.ruleChildren = ruleChildren;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<RuleDetail> getListData() {
        return listData;
    }

    public void setListData(List<RuleDetail> listData) {
        this.listData = listData;
    }
}
