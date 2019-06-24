package com.zjtelcom.cpct.open.entity.mktCampaignEntity;

import com.zjtelcom.cpct.open.entity.mktCpcAlgorithmsRule.OpenMktCpcAlgorithmsRulEntity;

import java.util.Date;

public class OpenMktCamRecomCalcRelEntity {

    private String actType;
    private Long evtRecomCalcRelId;
    private Long mktCampaignId;
    private Long algoId;
    private Long algorithmsRulId;
    private Long priority;
    //private List<OpenMktAlgorithmsEntity> mktAlgorithms
    private OpenMktCpcAlgorithmsRulEntity mktCpcAlgorithmsRul;
    private String statusCd;
    private Date statusDate;
    private Long createStaff;
    private Date createDate;
    private Long updateStaff;
    private Date updateDate;
    private String remark;

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public Long getEvtRecomCalcRelId() {
        return evtRecomCalcRelId;
    }

    public void setEvtRecomCalcRelId(Long evtRecomCalcRelId) {
        this.evtRecomCalcRelId = evtRecomCalcRelId;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public Long getAlgoId() {
        return algoId;
    }

    public void setAlgoId(Long algoId) {
        this.algoId = algoId;
    }

    public Long getAlgorithmsRulId() {
        return algorithmsRulId;
    }

    public void setAlgorithmsRulId(Long algorithmsRulId) {
        this.algorithmsRulId = algorithmsRulId;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public OpenMktCpcAlgorithmsRulEntity getMktCpcAlgorithmsRul() {
        return mktCpcAlgorithmsRul;
    }

    public void setMktCpcAlgorithmsRul(OpenMktCpcAlgorithmsRulEntity mktCpcAlgorithmsRul) {
        this.mktCpcAlgorithmsRul = mktCpcAlgorithmsRul;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
