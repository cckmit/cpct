package com.zjtelcom.cpct.domain.channel;

import java.util.Date;

public class OfferRestrict {
    private Long offerRestrictId;

    private Long offerId;

    private String rstrObjType;//1000	客户;2000	渠道;2100	渠道类型;3000	区域;4000	客户群;5000	销售系统;6000	客户标签

    private Long rstrObjId;

    private Long policyId;

    private Long applyRegionId;

    private String statusCd;

    private Long createStaff;

    private Long updateStaff;

    private Date statusDate;

    private Date createDate;

    private Date updateDate;

    private String remark;

    public Long getOfferRestrictId() {
        return offerRestrictId;
    }

    public void setOfferRestrictId(Long offerRestrictId) {
        this.offerRestrictId = offerRestrictId;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public String getRstrObjType() {
        return rstrObjType;
    }

    public void setRstrObjType(String rstrObjType) {
        this.rstrObjType = rstrObjType;
    }

    public Long getRstrObjId() {
        return rstrObjId;
    }

    public void setRstrObjId(Long rstrObjId) {
        this.rstrObjId = rstrObjId;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public Long getApplyRegionId() {
        return applyRegionId;
    }

    public void setApplyRegionId(Long applyRegionId) {
        this.applyRegionId = applyRegionId;
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

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}