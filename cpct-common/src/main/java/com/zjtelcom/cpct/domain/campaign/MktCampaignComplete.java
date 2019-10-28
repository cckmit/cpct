package com.zjtelcom.cpct.domain.campaign;

import com.zjtelcom.cpct.BaseEntity;

import java.util.Date;

public class MktCampaignComplete extends BaseEntity {

    private Long completeId;
    private Long mktCampaignId;//活动标识
    private String mktActivityNbr;//活动编码
    private String orderId;//申请单号
    private String orderName;//申请单名称
    private String tacheCd;//省环节CD
    private String tacheValueCd	;//省环节值编码
    private Date beginTime;//省环节开始时间
    private Date endTime;//省环节结束时间
    private Long sort;//排序

    public Long getCompleteId() {
        return completeId;
    }

    public void setCompleteId(Long completeId) {
        this.completeId = completeId;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public String getMktActivityNbr() {
        return mktActivityNbr;
    }

    public void setMktActivityNbr(String mktActivityNbr) {
        this.mktActivityNbr = mktActivityNbr;
    }

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

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
        this.sort = sort;
    }
}
