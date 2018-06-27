package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.Date;

public class VerbalConditionVO implements Serializable {

    private Long conditionId;

    private Long verbalId;

    private String leftParam;

    private String leftParamType;

    private String operType;

    private String rightParam;

    private String rightParamType;

    private String statusCd;

    private Long createStaff;

    private Date createDate;

    private Date statusDate;

    private String remark;

    public Long getConditionId() {
        return conditionId;
    }

    public void setConditionId(Long conditionId) {
        this.conditionId = conditionId;
    }

    public Long getVerbalId() {
        return verbalId;
    }

    public void setVerbalId(Long verbalId) {
        this.verbalId = verbalId;
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

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
