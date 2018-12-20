package com.zjtelcom.cpct.open.entity.label;

/**
 * @Auther: anson
 * @Date: 2018/12/18
 * @Description: 客户标签 openapi文档返回格式
 */
public class OpenCustInjectionLabel {


    private String InjectionLabelCode;
    private String InjectionLabelId;
    private String InjectionLabelName;
    private String custId;
    private String custLabelId;
    private String labelValue;
    private String remark;
    private String valueName;


    public String getInjectionLabelCode() {
        return InjectionLabelCode;
    }

    public void setInjectionLabelCode(String injectionLabelCode) {
        InjectionLabelCode = injectionLabelCode;
    }

    public String getInjectionLabelId() {
        return InjectionLabelId;
    }

    public void setInjectionLabelId(String injectionLabelId) {
        InjectionLabelId = injectionLabelId;
    }

    public String getInjectionLabelName() {
        return InjectionLabelName;
    }

    public void setInjectionLabelName(String injectionLabelName) {
        InjectionLabelName = injectionLabelName;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getCustLabelId() {
        return custLabelId;
    }

    public void setCustLabelId(String custLabelId) {
        this.custLabelId = custLabelId;
    }

    public String getLabelValue() {
        return labelValue;
    }

    public void setLabelValue(String labelValue) {
        this.labelValue = labelValue;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }
}
