package com.zjtelcom.cpct.dubbo.model;

import java.io.Serializable;

public class Ret implements Serializable {
    private String resultCode;
    private String resultMsg;



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


}
