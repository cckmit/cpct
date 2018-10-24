package com.zjtelcom.cpct.dubbo.model;

import java.io.Serializable;

public class Ret<T> implements Serializable {
    private String resultCode;
    private String resultMsg;
    private T data;


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

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
