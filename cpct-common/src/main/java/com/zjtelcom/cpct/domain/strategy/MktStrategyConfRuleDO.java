package com.zjtelcom.cpct.domain.strategy;

import com.zjtelcom.cpct.BaseEntity;

import java.util.Date;

/**
 * 策略配置规则DO
 */
public class MktStrategyConfRuleDO extends BaseEntity {
    /**
     * 策略配置规则Id
     */
    private Long mktStrategyConfRuleId;

    /**
     * 策略配置规则Name
     */
    private String mktStrategyConfRuleName;

    /**
     * 客户分群配置Id
     */
    private Long tarGrpId;

    /**
     * 销售品配置Id集
     */
    private String productId;

    /**
     * 协同渠道配置Id
     */
    private String evtContactConfId;

    /**
     * 过滤规则配置Id
     */
    private Long ruleConfId;

    public Long getMktStrategyConfRuleId() {
        return mktStrategyConfRuleId;
    }

    public void setMktStrategyConfRuleId(Long mktStrategyConfRuleId) {
        this.mktStrategyConfRuleId = mktStrategyConfRuleId;
    }

    public Long getTarGrpId() {
        return tarGrpId;
    }

    public void setTarGrpId(Long tarGrpId) {
        this.tarGrpId = tarGrpId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getEvtContactConfId() {
        return evtContactConfId;
    }

    public void setEvtContactConfId(String evtContactConfId) {
        this.evtContactConfId = evtContactConfId;
    }

    public Long getRuleConfId() {
        return ruleConfId;
    }

    public void setRuleConfId(Long ruleConfId) {
        this.ruleConfId = ruleConfId;
    }

    public String getMktStrategyConfRuleName() {
        return mktStrategyConfRuleName;
    }

    public void setMktStrategyConfRuleName(String mktStrategyConfRuleName) {
        this.mktStrategyConfRuleName = mktStrategyConfRuleName;
    }
}