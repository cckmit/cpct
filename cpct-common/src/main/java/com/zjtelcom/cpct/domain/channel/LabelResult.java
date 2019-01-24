/**
 * @(#)LabelResult.java, 2018/8/23.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.domain.channel;

import java.io.Serializable;

/**
 * Description:
 * author: linchao
 * date: 2018/08/23 18:20
 * version: V1.0
 */
public class LabelResult  implements Serializable {

    /**
     * 标签编码
     */
    private String labelCode;

    /**
     * 标签名称
     */
    private String labelName;


    private String operType;
    /**
     * 参考值
     */
    private String rightOperand;

    /**
     * 真实值
     */
    private String rightParam;

    /**
     * 是否命中
     */
    private Boolean result; // ture--命中， false--未命中

    /**
     * 是否是大数据标签
     */
    private String className;

    /**
     * 标签数据类型   1100 日期时间型;1200	字符型;1300	浮点型
     */
    private String labelDataType;


    public String getLabelDataType() {
        return labelDataType;
    }

    public void setLabelDataType(String labelDataType) {
        this.labelDataType = labelDataType;
    }

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

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

    public String getRightOperand() {
        return rightOperand;
    }

    public void setRightOperand(String rightOperand) {
        this.rightOperand = rightOperand;
    }

    public String getRightParam() {
        return rightParam;
    }

    public void setRightParam(String rightParam) {
        this.rightParam = rightParam;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }
}