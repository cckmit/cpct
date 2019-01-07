package com.zjtelcom.cpct.enums;

import org.apache.zookeeper.Op;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/01/07 14:52
 * @version: V1.0
 */
public enum OperatorLogEnum {
    ADD("添加", "2008"),
    UPDATE("修改", "2009"),
    DELETE("删除", "2010");

    private String operator;
    private String operatorValue;

    OperatorLogEnum(String operator, String operatorValue) {
        this.operator = operator;
        this.operatorValue = operatorValue;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperatorValue() {
        return operatorValue;
    }

    public void setOperatorValue(String operatorValue) {
        this.operatorValue = operatorValue;
    }
}