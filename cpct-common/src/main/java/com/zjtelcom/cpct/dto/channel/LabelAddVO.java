package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;

public class LabelAddVO implements Serializable {
    private String injectionLabelCode;//
    private String injectionLabelName;
    private String injectionLabelDesc;
    private String labelType;//1000	客户注智标签;2000	产品注智标签;3000	销售品注智标签;4000	营销资源注智标签;5000	礼包注智标签
    private String labelValueType;//1000输入型;2000	枚举型
    private String labelDataType;

    private String fitDomain;//适用域
    private String rightOperand;//右操作符（标签值）
    private String conditionType;//单选多选框
    private String operator;//运算符
    private Integer scope;


    public Integer getScope() {
        return scope;
    }

    public void setScope(Integer scope) {
        this.scope = scope;
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

    public String getLabelType() {
        return labelType;
    }

    public void setLabelType(String labelType) {
        this.labelType = labelType;
    }

    public String getLabelValueType() {
        return labelValueType;
    }

    public void setLabelValueType(String labelValueType) {
        this.labelValueType = labelValueType;
    }

    public String getLabelDataType() {
        return labelDataType;
    }

    public void setLabelDataType(String labelDataType) {
        this.labelDataType = labelDataType;
    }
}
