package com.zjtelcom.cpct.dto.campaign;

import com.zjtelcom.cpct.BaseEntity;

public class MktCpcAlgorithmsRul extends BaseEntity {

    /**
     * 通用数据操作类型,
     * KIP=保持/ADD=新增
     * /MOD=修改/DEL=删除 1
     */
    private String actType;

    /**
     * 算法规则标识
     */
    private Long algorithmsRulId;

    /**
     * 算法规则名称
     */
    private String algorithmsRulName;

    /**
     * 规则描述
     */
    private String ruleDesc;

    /**
     * 计算表达式
     */
    private String ruleExpression;

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public Long getAlgorithmsRulId() {
        return algorithmsRulId;
    }

    public void setAlgorithmsRulId(Long algorithmsRulId) {
        this.algorithmsRulId = algorithmsRulId;
    }

    public String getAlgorithmsRulName() {
        return algorithmsRulName;
    }

    public void setAlgorithmsRulName(String algorithmsRulName) {
        this.algorithmsRulName = algorithmsRulName;
    }

    public String getRuleDesc() {
        return ruleDesc;
    }

    public void setRuleDesc(String ruleDesc) {
        this.ruleDesc = ruleDesc;
    }

    public String getRuleExpression() {
        return ruleExpression;
    }

    public void setRuleExpression(String ruleExpression) {
        this.ruleExpression = ruleExpression;
    }
}
