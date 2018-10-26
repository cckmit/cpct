package com.zjtelcom.cpct.dubbo.model;

import com.zjtelcom.cpct.dto.campaign.MktCamResultRelDeatil;

import java.io.Serializable;
import java.util.List;

public class RetCamResult implements Serializable {
    private String resultCode;
    private String resultMsg;
    private List<MktCamResultRelDeatil> data;

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

    public List<MktCamResultRelDeatil> getData() {
        return data;
    }

    public void setData(List<MktCamResultRelDeatil> data) {
        this.data = data;
    }
}
