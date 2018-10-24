package com.zjtelcom.cpct.dubbo.model;

import java.io.Serializable;

public class RetChannel implements Serializable {
    private String resultCode;
    private String resultMsg;
    private ChannelModel data;

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

    public ChannelModel getData() {
        return data;
    }

    public void setData(ChannelModel data) {
        this.data = data;
    }
}
