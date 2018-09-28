package com.zjtelcom.cpct.dto.grouping;

import java.io.Serializable;
import java.util.List;

public class TrialRequest implements Serializable {

    private List<TrialOperationParam> operationVOList;

    private String[] fieldList;

    public List<TrialOperationParam> getOperationVOList() {
        return operationVOList;
    }

    public void setOperationVOList(List<TrialOperationParam> operationVOList) {
        this.operationVOList = operationVOList;
    }

    public String[] getFieldList() {
        return fieldList;
    }

    public void setFieldList(String[] fieldList) {
        this.fieldList = fieldList;
    }
}
