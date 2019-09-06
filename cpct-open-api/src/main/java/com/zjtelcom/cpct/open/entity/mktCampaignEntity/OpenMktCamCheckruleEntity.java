package com.zjtelcom.cpct.open.entity.mktCampaignEntity;

import java.util.Date;

public class OpenMktCamCheckruleEntity {

    private String actType;
    private Long checkruleId;
    private Long mktCampaignId;
    private String checkruleType;
    private Long busiRuleId;
    private String checkruleResult;
    private String statusCd	;
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

    public Long getCheckruleId() {
        return checkruleId;
    }

    public void setCheckruleId(Long checkruleId) {
        this.checkruleId = checkruleId;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public String getCheckruleType() {
        return checkruleType;
    }

    public void setCheckruleType(String checkruleType) {
        this.checkruleType = checkruleType;
    }

    public Long getBusiRuleId() {
        return busiRuleId;
    }

    public void setBusiRuleId(Long busiRuleId) {
        this.busiRuleId = busiRuleId;
    }

    public String getCheckruleResult() {
        return checkruleResult;
    }

    public void setCheckruleResult(String checkruleResult) {
        this.checkruleResult = checkruleResult;
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
