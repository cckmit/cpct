package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.List;

public class LabelVO implements Serializable {
    private Long injectionLabelId;
    private String injectionLabelCode;
    private String injectionLabelName;
    private String injectionLabelDesc;
    private String labelType;//1000
    private String labelValueType;//1000输入型；2000枚举型
    private String labelDataType;//1000	日期型;1100	日期时间型;1200	字符型;1300	浮点型;1400	整数型;1500	布尔型;1600	计算型
    private String fitDomain;//适用域
    private String conditionType;//单选多选框
    private List<OperatorDetail> operatorList;//运算符
    private List<LabelValueVO> valueList;
    private String scope;
    private Integer isShared;


    public List<LabelValueVO> getValueList() {
        return valueList;
    }

    public void setValueList(List<LabelValueVO> valueList) {
        this.valueList = valueList;
    }

    public Integer getIsShared() {
        return isShared;
    }

    public void setIsShared(Integer isShared) {
        this.isShared = isShared;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<OperatorDetail> getOperatorList() {
        return operatorList;
    }

    public void setOperatorList(List<OperatorDetail> operatorList) {
        this.operatorList = operatorList;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public String getFitDomain() {
        return fitDomain;
    }

    public void setFitDomain(String fitDomain) {
        this.fitDomain = fitDomain;
    }

    public Long getInjectionLabelId() {
        return injectionLabelId;
    }

    public void setInjectionLabelId(Long injectionLabelId) {
        this.injectionLabelId = injectionLabelId;
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
