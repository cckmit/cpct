package com.zjtelcom.cpct.domain.campaign;

import java.util.Date;

public class MktStrategyConfRegionRelDO {
    private Long mktStrategyConfRegionRelId;

    private Long mktStrategyConfId;

    private Long applyCityId;

    private String applyCounty;

    private String applyBranch;

    private String applyGridding;

    private Long createStaff;

    private Date createDate;

    private Long updateStaff;

    private Date updateDate;

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