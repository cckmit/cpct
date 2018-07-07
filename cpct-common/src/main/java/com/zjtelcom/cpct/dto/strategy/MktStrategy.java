package com.zjtelcom.cpct.dto.strategy;

import com.zjtelcom.cpct.BaseEntity;

/**
 * 营销维挽策略
 */
public class MktStrategy{

    /**
     * 通用数据操作类型
     */
    private String actType;

    /**
     * 营销策略标识
     */
    private Long strategyId;

    /**
     * 策略名称
     */
    private String strategyName;

    /**
     * 策略类型
     */
    private String strategyType;

    /**
     * 策略内容
     */
    private String strategyDesc;

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

    public String getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(String strategyType) {
        this.strategyType = strategyType;
    }

    public String getStrategyDesc() {
        return strategyDesc;
    }

    public void setStrategyDesc(String strategyDesc) {
        this.strategyDesc = strategyDesc;
    }

    public String getRuleExpression() {
        return ruleExpression;
    }

    public void setRuleExpression(String ruleExpression) {
        this.ruleExpression = ruleExpression;
    }
}
