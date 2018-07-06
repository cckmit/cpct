package com.zjtelcom.cpct.domain.strategy;

import com.zjtelcom.cpct.BaseEntity;

import java.util.Date;

public class MktStrategyConfRuleRelDO extends BaseEntity {
    /**
     * 策略配置规则关联Id
     */
    private Long mktStrategyConfRuleRelId;

    /**
     * 策略配置Id
     */
    private Long mktStrategyConfId;

    /**
     * 策略配置规则Id
     */
    private Long mktStrategyConfRuleId;

    public Long getMktStrategyConfRuleRelId() {
        return mktStrategyConfRuleRelId;
    }

    public void setMktStrategyConfRuleRelId(Long mktStrategyConfRuleRelId) {
        this.mktStrategyConfRuleRelId = mktStrategyConfRuleRelId;
    }

    public Long getMktStrategyConfId() {
        return mktStrategyConfId;
    }

    public void setMktStrategyConfId(Long mktStrategyConfId) {
        this.mktStrategyConfId = mktStrategyConfId;
    }

    public Long getMktStrategyConfRuleId() {
        return mktStrategyConfRuleId;
    }

    public void setMktStrategyConfRuleId(Long mktStrategyConfRuleId) {
        this.mktStrategyConfRuleId = mktStrategyConfRuleId;
    }
}