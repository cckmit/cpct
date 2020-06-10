package com.zjtelcom.cpct.domain.openApi.mktCamItem;

import java.util.Date;

public class OpenObjectLabelRelEntity {
    private Long objectLabelRelId;

    private Long labelId;

    private Long labelValueId;

    private String labelValue;

    private String objType;

    private Long objId;

    private String statusCd;

    private Long createStaff;

    private Long updateStaff;

    private String createDate;

    private String statusDate;

    private String updateDate;

    private String remark;

    private String labelName;

    private String objNbr;

    private String labelCode;


    public String getLabelCode() {
        return labelCode;
    }

    public void setLabelCode(String labelCode) {
        this.labelCode = labelCode;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getObjNbr() {
        return objNbr;
    }

    public void setObjNbr(String objNbr) {
        this.objNbr = objNbr;
    }

    public Long getObjectLabelRelId() {
        return objectLabelRelId;
    }

    public void setObjectLabelRelId(Long objectLabelRelId) {
        this.objectLabelRelId = objectLabelRelId;
    }

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public Long getLabelValueId() {
        return labelValueId;
    }

    public void setLabelValueId(Long labelValueId) {
        this.labelValueId = labelValueId;
    }

    public String getLabelValue() {
        return labelValue;
    }

    public void setLabelValue(String labelValue) {
        this.labelValue = labelValue;
    }

    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        this.objType = objType;
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
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

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}