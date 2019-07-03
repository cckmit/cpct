package com.zjtelcom.cpct.open.entity.tarGrp;

import java.util.Date;

public class OpenTarGrpConditionEntity {

    private Long conditionId;
    private Long tarGrpId;
    private Long rootFlag;
    private String leftParam;
    private String leftParamType;
    private String operType;
    private String rightParam;
    private String rightParamType;
    private String conditionText;
    private String statusCd;
    private Date statusDate;
    private Long createStaff;
    private Date createDate;
    private Long updateStaff;
    private Date updateDate;
    private String remark;

    public Long getConditionId() {
        return conditionId;
    }

    public void setConditionId(Long conditionId) {
        this.conditionId = conditionId;
    }

    public Long getTarGrpId() {
        return tarGrpId;
    }

    public void setTarGrpId(Long tarGrpId) {
        this.tarGrpId = tarGrpId;
    }

    public Long getRootFlag() {
        return rootFlag;
    }

    public void setRootFlag(Long rootFlag) {
        this.rootFlag = rootFlag;
    }

    public String getLeftParam() {
        return leftParam;
    }

    public void setLeftParam(String leftParam) {
        this.leftParam = leftParam;
    }

    public String getLeftParamType() {
        return leftParamType;
    }

    public void setLeftParamType(String leftParamType) {
        this.leftParamType = leftParamType;
    }

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public String getRightParam() {
        return rightParam;
    }

    public void setRightParam(String rightParam) {
        this.rightParam = rightParam;
    }

    public String getRightParamType() {
        return rightParamType;
    }

    public void setRightParamType(String rightParamType) {
        this.rightParamType = rightParamType;
    }

    public String getConditionText() {
        return conditionText;
    }

    public void setConditionText(String conditionText) {
        this.conditionText = conditionText;
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
