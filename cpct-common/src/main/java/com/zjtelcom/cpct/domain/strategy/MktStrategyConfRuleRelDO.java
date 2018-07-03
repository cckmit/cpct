package com.zjtelcom.cpct.domain.strategy;

import java.util.Date;

public class MktStrategyConfRuleRelDO {
    private Long mktStrategyConfRuleRelId;

    private Long mktStrategyConfId;

    private Long mktStrategyConfRuleId;

    private String createStaff;

    private Date createDate;

    private String updateStaff;

    private Date updateDate;

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

    public String getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(String createStaff) {
        this.createStaff = createStaff;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdateStaff() {
        return updateStaff;
    }

    public void setUpdateStaff(String updateStaff) {
        this.updateStaff = updateStaff;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}