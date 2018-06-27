package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;

public class LabelVO implements Serializable {
    private Long injectionLabelId;
    private String injectionLabelCode;
    private String injectionLabelName;
    private String injectionLabelDesc;
    private String labelType;
    private String labelValueType;
    private String labelDataType;

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
