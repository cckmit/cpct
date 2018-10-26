package com.zjtelcom.cpct.dubbo.model;

import java.io.Serializable;
import java.util.ArrayList;

public class RetCamResult implements Serializable {
    private String resultCode;
    private String resultMsg;
    private ArrayList<MktCamResultRelDeatil> data;

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

    public ArrayList<MktCamResultRelDeatil> getData() {
        return data;
    }

    public void setData(ArrayList<MktCamResultRelDeatil> data) {
        this.data = data;
    }
}
