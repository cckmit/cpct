package com.zjtelcom.cpct.dto.campaign;

import java.util.Date;
import java.util.List;

public class MktCamChlResult {

    /**
     * 二次协同渠道结果标识
     */
    private Long mktCamChlResultId;
    /**
     * 二次协同渠道结果名称
     */
    private String mktCamChlResultName;
    /**
     * 营销结果Id
     */
    private Long result;
    /**
     * 原因Id
     */
    private Long reason;

    /**
     * 推送渠道集合
     */
    List<MktCamChlConfDetail> mktCamChlConfDetailList;


    public Long getMktCamChlResultId() {
        return mktCamChlResultId;
    }

    public void setMktCamChlResultId(Long mktCamChlResultId) {
        this.mktCamChlResultId = mktCamChlResultId;
    }

    public String getMktCamChlResultName() {
        return mktCamChlResultName;
    }

    public void setMktCamChlResultName(String mktCamChlResultName) {
        this.mktCamChlResultName = mktCamChlResultName;
    }

    public Long getResult() {
        return result;
    }

    public void setResult(Long result) {
        this.result = result;
    }

    public Long getReason() {
        return reason;
    }

    public void setReason(Long reason) {
        this.reason = reason;
    }

    public List<MktCamChlConfDetail> getMktCamChlConfDetailList() {
        return mktCamChlConfDetailList;
    }

    public void setMktCamChlConfDetailList(List<MktCamChlConfDetail> mktCamChlConfDetailList) {
        this.mktCamChlConfDetailList = mktCamChlConfDetailList;
    }
}