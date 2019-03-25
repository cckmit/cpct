package com.zjtelcom.cpct.dubbo.model;

import java.io.Serializable;

public class RetQuestion implements Serializable {
    private String resultCode;
    private String resultMsg;
    private QuestionRep data;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public QuestionRep getData() {
        return data;
    }

    public void setData(QuestionRep data) {
        this.data = data;
    }
}
