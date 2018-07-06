package com.zjtelcom.cpct.domain.strategy;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

import java.util.Date;

public class MktStrategyConfRegionRelDO extends BaseEntity {
    /**
     * 策略配置下发区域关联Id
     */
    private Long mktStrategyConfRegionRelId;

    private Long mktStrategyConfId;

    private Long applyCityId;

    private String applyCounty;

    private String applyBranch;

    private String applyGridding;


    public Long getMktStrategyConfRegionRelId() {
        return mktStrategyConfRegionRelId;
    }

    public void setMktStrategyConfRegionRelId(Long mktStrategyConfRegionRelId) {
        this.mktStrategyConfRegionRelId = mktStrategyConfRegionRelId;
    }

    public Long getMktStrategyConfId() {
        return mktStrategyConfId;
    }

    public void setMktStrategyConfId(Long mktStrategyConfId) {
        this.mktStrategyConfId = mktStrategyConfId;
    }

    public Long getApplyCityId() {
        return applyCityId;
    }

    public void setApplyCityId(Long applyCityId) {
        this.applyCityId = applyCityId;
    }

    public String getApplyCounty() {
        return applyCounty;
    }

    public void setApplyCounty(String applyCounty) {
        this.applyCounty = applyCounty;
    }

    public String getApplyBranch() {
        return applyBranch;
    }

    public void setApplyBranch(String applyBranch) {
        this.applyBranch = applyBranch;
    }

    public String getApplyGridding() {
        return applyGridding;
    }

    public void setApplyGridding(String applyGridding) {
        this.applyGridding = applyGridding;
    }
}