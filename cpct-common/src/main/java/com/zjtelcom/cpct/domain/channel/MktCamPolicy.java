package com.zjtelcom.cpct.domain.channel;

import java.util.Date;

public class MktCamPolicy {
    private Long mktCamPolicyId;

    private Long mktCampaignId;

    private Long policyId;

    private String policyName;

    private String policyDesc;

    private String policyTypeName;

    private Date createDate;

    private Date updateDate;

    private String statusCd;

    private Long createStaff;

    private Long updateStaff;

    private String remark;

    public Long getMktCamPolicyId() {
        return mktCamPolicyId;
    }

    public void setMktCamPolicyId(Long mktCamPolicyId) {
        this.mktCamPolicyId = mktCamPolicyId;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyDesc() {
        return policyDesc;
    }

    public void setPolicyDesc(String policyDesc) {
        this.policyDesc = policyDesc;
    }

    public String getPolicyTypeName() {
        return policyTypeName;
    }

    public void setPolicyTypeName(String policyTypeName) {
        this.policyTypeName = policyTypeName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public Long getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(Long createStaff) {
        this.createStaff = createStaff;
    }

    public Long getUpdateStaff() {
        return updateStaff;
    }

    public void setUpdateStaff(Long updateStaff) {
        this.updateStaff = updateStaff;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}