package com.zjtelcom.cpct.dubbo.model;

import java.io.Serializable;

public class LabValueModel implements Serializable {
    private Long labValueRowId;//标签值id
    private String ValueName;//标签值名称
    private String labValue;//标签值

    public Long getLabValueRowId() {
        return labValueRowId;
    }

    public void setLabValueRowId(Long labValueRowId) {
        this.labValueRowId = labValueRowId;
    }

    public String getValueName() {
        return ValueName;
    }

    public void setValueName(String valueName) {
        ValueName = valueName;
    }

    public String getLabValue() {
        return labValue;
    }

    public void setLabValue(String labValue) {
        this.labValue = labValue;
    }
}
