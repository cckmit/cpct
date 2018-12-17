package com.zjtelcom.cpct.domain.campaign;

import com.zjtelcom.cpct.BaseEntity;

public class MktAlgorithms extends BaseEntity{

    private Long algoId;
    private String algoCode;
    private String algoName;
    private String handleClass;
    private String algoDesc;

    public Long getAlgoId() {
        return algoId;
    }

    public void setAlgoId(Long algoId) {
        this.algoId = algoId;
    }

    public String getAlgoCode() {
        return algoCode;
    }

    public void setAlgoCode(String algoCode) {
        this.algoCode = algoCode;
    }

    public String getAlgoName() {
        return algoName;
    }

    public void setAlgoName(String algoName) {
        this.algoName = algoName;
    }

    public String getHandleClass() {
        return handleClass;
    }

    public void setHandleClass(String handleClass) {
        this.handleClass = handleClass;
    }

    public String getAlgoDesc() {
        return algoDesc;
    }

    public void setAlgoDesc(String algoDesc) {
        this.algoDesc = algoDesc;
    }
}
