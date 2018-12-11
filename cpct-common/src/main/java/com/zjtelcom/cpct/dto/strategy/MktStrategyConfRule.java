package com.zjtelcom.cpct.dto.strategy;

import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
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
     * 营销活动标识
     */
    private Long mktCampaignId;

    /**
     * 营销活动名称
     */
    private String mktCampaignName;

    /**
     * 营销活动分类
     */
    private String mktCampaignType;

    /**
     * 策略配置标识
     */
    private Long StrategyConfId;

    /**
     * 策略配置名称
     */
    private String StrategyConfName;

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
    private List<MktCamChlConfDetail> mktCamChlConfDetailList;

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

    public List<MktCamChlConfDetail> getMktCamChlConfDetailList() {
        return mktCamChlConfDetailList;
    }

    public void setMktCamChlConfDetailList(List<MktCamChlConfDetail> mktCamChlConfDetailList) {
        this.mktCamChlConfDetailList = mktCamChlConfDetailList;
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

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public Long getStrategyConfId() {
        return StrategyConfId;
    }

    public void setStrategyConfId(Long strategyConfId) {
        StrategyConfId = strategyConfId;
    }

    public String getStrategyConfName() {
        return StrategyConfName;
    }

    public void setStrategyConfName(String strategyConfName) {
        StrategyConfName = strategyConfName;
    }

    public String getMktCampaignName() {
        return mktCampaignName;
    }

    public void setMktCampaignName(String mktCampaignName) {
        this.mktCampaignName = mktCampaignName;
    }

    public String getMktCampaignType() {
        return mktCampaignType;
    }

    public void setMktCampaignType(String mktCampaignType) {
        this.mktCampaignType = mktCampaignType;
    }
}