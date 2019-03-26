package com.zjtelcom.cpct.dubbo.model;

import java.io.Serializable;

public class RetEvent implements Serializable {
    private String resultCode;
    private String resultMsg;
    private ContactEvtModel data;

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

    public ContactEvtModel getData() {
        return data;
    }

    public void setData(ContactEvtModel data) {
        this.data = data;
    }
}
