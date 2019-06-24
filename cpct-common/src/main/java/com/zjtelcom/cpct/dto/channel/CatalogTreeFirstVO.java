package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.List;

public class CatalogTreeFirstVO implements Serializable {
    private Long injectionLabelId;
    private String injectionLabelName;
    private List<CatalogTreeTwoVO> children;
    private String conditionType;//单选多选框
    private List<OperatorDetail> operatorList;//运算符
    private List<LabelValueVO> valueList;
    private String scope;

    public Long getInjectionLabelId() {
        return injectionLabelId;
    }

    public void setInjectionLabelId(Long injectionLabelId) {
        this.injectionLabelId = injectionLabelId;
    }

    public String getInjectionLabelName() {
        return injectionLabelName;
    }

    public void setInjectionLabelName(String injectionLabelName) {
        this.injectionLabelName = injectionLabelName;
    }

    public List<CatalogTreeTwoVO> getChildren() {
        return children;
    }

    public void setChildren(List<CatalogTreeTwoVO> children) {
        this.children = children;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public List<OperatorDetail> getOperatorList() {
        return operatorList;
    }

    public void setOperatorList(List<OperatorDetail> operatorList) {
        this.operatorList = operatorList;
    }

    public List<LabelValueVO> getValueList() {
        return valueList;
    }

    public void setValueList(List<LabelValueVO> valueList) {
        this.valueList = valueList;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
