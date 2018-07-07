package com.zjtelcom.cpct.dto.strategy;

import java.util.List;

public class MktStrategyConfRule {
    /**
     * 策略配置规则Id
     */
    private Long mktStrategyConfRuleId;

    /**
     * 客户分群配置Id
     */
    private Long tarGrpId;

    /**
     * 销售品配置Id集合
     */
    private List<Long> productIdlist;

    /**
     * 协同渠道配置Id集合
     */
    private List<Long> evtContactConfIdList;

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

    public List<Long> getProductIdlist() {
        return productIdlist;
    }

    public void setProductIdlist(List<Long> productIdlist) {
        this.productIdlist = productIdlist;
    }

    public List<Long> getEvtContactConfIdList() {
        return evtContactConfIdList;
    }

    public void setEvtContactConfIdList(List<Long> evtContactConfIdList) {
        this.evtContactConfIdList = evtContactConfIdList;
    }

    public Long getRuleConfId() {
        return ruleConfId;
    }

    public void setRuleConfId(Long ruleConfId) {
        this.ruleConfId = ruleConfId;
    }
    
}