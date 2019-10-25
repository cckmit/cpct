package com.zjtelcom.cpct.open.entity.mktCampaignEntity;

import java.util.List;

public class CompleteMktCampaign {

    private String orderId;//申请单号
    private String orderName;//申请单名称
    private String tacheCd;//省环节CD
    private String tacheValueCd	;//省环节值编码
    private String beginTime;//省环节开始时间
    private String endTime;//省环节结束时间
    private String detaileTacheList	;//省内环节明细(省份在省环节结束时统一反馈省内环节明细，且格式符合以下规范：环节名称、环节开始时间、环节结束时间、换行符#)
    private String regionCode;//省编码
    private String statusCd	;//环节状态(1100：环节开始 1200：环节结束 1300：回退)
    private List<OpenMktCampaignEntity> mktCampaigns;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getTacheCd() {
        return tacheCd;
    }

    public void setTacheCd(String tacheCd) {
        this.tacheCd = tacheCd;
    }

    public String getTacheValueCd() {
        return tacheValueCd;
    }

    public void setTacheValueCd(String tacheValueCd) {
        this.tacheValueCd = tacheValueCd;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDetaileTacheList() {
        return detaileTacheList;
    }

    public void setDetaileTacheList(String detaileTacheList) {
        this.detaileTacheList = detaileTacheList;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public List<OpenMktCampaignEntity> getMktCampaigns() {
        return mktCampaigns;
    }

    public void setMktCampaigns(List<OpenMktCampaignEntity> mktCampaigns) {
        this.mktCampaigns = mktCampaigns;
    }
}
