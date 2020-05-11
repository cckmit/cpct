package com.zjtelcom.cpct.dubbo.model;

import java.io.Serializable;

public class RetCamResp implements Serializable {
    private String resultCode;
    private String resultMsg;
    private MktCampaignResp data;

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

    public MktCampaignResp getData() {
        return data;
    }

    public void setData(MktCampaignResp data) {
        this.data = data;
    }
}
