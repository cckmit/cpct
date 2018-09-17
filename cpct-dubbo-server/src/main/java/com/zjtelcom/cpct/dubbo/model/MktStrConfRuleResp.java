package com.zjtelcom.cpct.dubbo.model;

import com.zjtelcom.cpct.domain.channel.Offer;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.campaign.MktCamChlResult;
import com.zjtelcom.cpct.vo.grouping.TarGrpConditionVO;

import java.util.List;

/**
 * 策略配置规则DTO
 */
public class MktStrConfRuleResp {
    /**
     * 策略配置规则Id
     */
    private Long mktStrategyConfRuleId;

    /**
     * 策略配置规则Name
     */
    private String mktStrategyConfRuleName;

    /**
     * 目标分群条件
     */
    private List<TarGrpConditionVO> tarGrpConditionList;

    /**
     * 销售品
     */
    private List<Offer> offerList;

    /**
     * 协同渠道配置集合
     */
    List<MktCamChlConfDetail> mktCamChlConfDetailList;

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

    public List<TarGrpConditionVO> getTarGrpConditionList() {
        return tarGrpConditionList;
    }

    public void setTarGrpConditionList(List<TarGrpConditionVO> tarGrpConditionList) {
        this.tarGrpConditionList = tarGrpConditionList;
    }

    public List<Offer> getOfferList() {
        return offerList;
    }

    public void setOfferList(List<Offer> offerList) {
        this.offerList = offerList;
    }
}