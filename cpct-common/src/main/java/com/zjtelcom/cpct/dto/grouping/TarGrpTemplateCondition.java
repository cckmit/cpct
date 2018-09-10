package com.zjtelcom.cpct.dto.grouping;

import java.util.Date;
/**
 * @Description:
 * @author: linchao
 * @date: 2018/09/06 16:14
 * @version: V1.0
 */
public class TarGrpTemplateCondition {
    private Long conditionId;

    private Long tarGrpTemplateId;

    private String leftParam;

    private String leftParamType;

    private String operType;

    private String rightParam;

    private String rightParamType;

    private String conditionText;

    private String statusCd;

    public Long getConditionId() {
        return conditionId;
    }

    public void setConditionId(Long conditionId) {
        this.conditionId = conditionId;
    }

    public Long getTarGrpTemplateId() {
        return tarGrpTemplateId;
    }

    public void setTarGrpTemplateId(Long tarGrpTemplateId) {
        this.tarGrpTemplateId = tarGrpTemplateId;
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

}