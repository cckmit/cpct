package com.zjtelcom.cpct.dto.strategy;

import com.zjtelcom.cpct.dto.campaign.MktCamChlConf;
import com.zjtelcom.cpct.dto.campaign.MktCamChlResult;

import java.util.List;

/**
 * 策略配置规则DTO
 */
public class MktStrategyConfRule {
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
     * 销售品配置Id集合
     */
    private List<Long> productIdlist;

    /**
     * 协同渠道配置集合
     */
    private List<MktCamChlConf> mktCamChlConfList;

    /**
     * 二次协同渠道结果集合
     */
    private List<MktCamChlResult> mktCamChlResultList;


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

    public List<MktCamChlConf> getMktCamChlConfList() {
        return mktCamChlConfList;
    }

    public void setMktCamChlConfList(List<MktCamChlConf> mktCamChlConfList) {
        this.mktCamChlConfList = mktCamChlConfList;
    }

    public String getMktStrategyConfRuleName() {
        return mktStrategyConfRuleName;
    }

    public void setMktStrategyConfRuleName(String mktStrategyConfRuleName) {
        this.mktStrategyConfRuleName = mktStrategyConfRuleName;
    }

    public List<MktCamChlResult> getMktCamChlResultList() {
        return mktCamChlResultList;
    }

    public void setMktCamChlResultList(List<MktCamChlResult> mktCamChlResultList) {
        this.mktCamChlResultList = mktCamChlResultList;
    }
}