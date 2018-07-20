package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;

public class LabelEditVO implements Serializable {
    private Long labelId;
    private String injectionLabelCode;//
    private String injectionLabelName;
    private String injectionLabelDesc;
    private String fitDomain;//适用域
    private String rightOperand;//右操作符（标签值）
    private String conditionType;//单选多选框
    private String operator;//运算符
    private Integer isShared;


    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public String getInjectionLabelCode() {
        return injectionLabelCode;
    }

    public void setInjectionLabelCode(String injectionLabelCode) {
        this.injectionLabelCode = injectionLabelCode;
    }

    public String getInjectionLabelName() {
        return injectionLabelName;
    }

    public void setInjectionLabelName(String injectionLabelName) {
        this.injectionLabelName = injectionLabelName;
    }

    public String getInjectionLabelDesc() {
        return injectionLabelDesc;
    }

    public void setInjectionLabelDesc(String injectionLabelDesc) {
        this.injectionLabelDesc = injectionLabelDesc;
    }

    public String getFitDomain() {
        return fitDomain;
    }

    public void setFitDomain(String fitDomain) {
        this.fitDomain = fitDomain;
    }

    public String getRightOperand() {
        return rightOperand;
    }

    public void setRightOperand(String rightOperand) {
        this.rightOperand = rightOperand;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }



    public Integer getIsShared() {
        return isShared;
    }

    public void setIsShared(Integer isShared) {
        this.isShared = isShared;
    }
}
