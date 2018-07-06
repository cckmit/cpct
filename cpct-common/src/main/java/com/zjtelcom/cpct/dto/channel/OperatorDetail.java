package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;

public class OperatorDetail implements Serializable {

    private String operName;
    private Integer operValue;

    public String getOperName() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName = operName;
    }

    public Integer getOperValue() {
        return operValue;
    }

    public void setOperValue(Integer operValue) {
        this.operValue = operValue;
    }
}
