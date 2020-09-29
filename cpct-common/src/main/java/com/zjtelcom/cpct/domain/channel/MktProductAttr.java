package com.zjtelcom.cpct.domain.channel;

import java.util.Date;

public class MktProductAttr {
    private Long mktProductAttrId;

    private Long productId;

    private Long ruleId;

    private Long attrId;

    private String attrName;

    private String attrValue;

    private String editFlg;

    private String frameFlg;

    private String statusCd;

    private Long createStaff;

    private Date createDate;

    private Long updateStaff;

    private Date updateDate;

    private String remark;

    public Long getMktProductAttrId() {
        return mktProductAttrId;
    }

    public void setMktProductAttrId(Long mktProductAttrId) {
        this.mktProductAttrId = mktProductAttrId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Long getAttrId() {
        return attrId;
    }

    public void setAttrId(Long attrId) {
        this.attrId = attrId;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

    public String getEditFlg() {
        return editFlg;
    }

    public void setEditFlg(String editFlg) {
        this.editFlg = editFlg;
    }

    public String getFrameFlg() {
        return frameFlg;
    }

    public void setFrameFlg(String frameFlg) {
        this.frameFlg = frameFlg;
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