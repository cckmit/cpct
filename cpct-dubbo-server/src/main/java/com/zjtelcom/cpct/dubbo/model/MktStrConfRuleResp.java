package com.zjtelcom.cpct.dubbo.model;

import com.zjtelcom.cpct.domain.channel.Offer;
import com.zjtelcom.cpct.vo.grouping.TarGrpConditionVO;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * 策略配置规则DTO
 */
public class MktStrConfRuleResp implements Serializable {
    /**
     * 策略配置规则Id
     */
    private Long mktStrategyConfRuleId;

    /**
     * 策略配置规则Name
     */
    private String mktStrategyConfRuleName;

    public String getMktStrategyConfRuleName() {
        return mktStrategyConfRuleName;
    }

    public void setMktStrategyConfRuleName(String mktStrategyConfRuleName) {
        this.mktStrategyConfRuleName = mktStrategyConfRuleName;
    }

    public ArrayList<MktCamChlConfDetail> getMktCamChlConfDetailList() {
        return mktCamChlConfDetailList;
    }

    public void setMktCamChlConfDetailList(ArrayList<MktCamChlConfDetail> mktCamChlConfDetailList) {
        this.mktCamChlConfDetailList = mktCamChlConfDetailList;
    }

    public ArrayList<MktCamChlResult> getMktCamChlResultList() {
        return mktCamChlResultList;
    }

    public void setMktCamChlResultList(ArrayList<MktCamChlResult> mktCamChlResultList) {
        this.mktCamChlResultList = mktCamChlResultList;
    }

    /**
     * 协同渠道配置集合
     */
    private ArrayList<MktCamChlConfDetail> mktCamChlConfDetailList;

    /**
     * 二次协同渠道结果集合
     */
    private ArrayList<MktCamChlResult> mktCamChlResultList;


    public Long getMktStrategyConfRuleId() {
        return mktStrategyConfRuleId;
    }

    public void setMktStrategyConfRuleId(Long mktStrategyConfRuleId) {
        this.mktStrategyConfRuleId = mktStrategyConfRuleId;
    }


}