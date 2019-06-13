package com.zjtelcom.cpct.domain.strategy;

import java.io.Serializable;
import java.util.Date;

public class MktStrategyCloseRuleRelDO implements Serializable {
    private Long mktStrategyFilterRuleRelId;

    private Long strategyId;

    private Long ruleId;

    private Long createStaff;

    private Date createDate;

    private Long updateStaff;

    private Date updateDate;

    public Long getMktStrategyFilterRuleRelId() {
        return mktStrategyFilterRuleRelId;
    }

    public void setMktStrategyFilterRuleRelId(Long mktStrategyFilterRuleRelId) {
        this.mktStrategyFilterRuleRelId = mktStrategyFilterRuleRelId;
    }

    public Long getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Long strategyId) {
        this.strategyId = strategyId;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Long getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(Long createStaff) {
        this.createStaff = createStaff;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getUpdateStaff() {
        return updateStaff;
    }

    public void setUpdateStaff(Long updateStaff) {
        this.updateStaff = updateStaff;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}