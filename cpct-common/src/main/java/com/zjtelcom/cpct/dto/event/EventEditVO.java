package com.zjtelcom.cpct.dto.event;

import java.io.Serializable;

public class EventEditVO implements Serializable {

    private String contactEvtName;//记录事件的名称
    private String contactEvtDesc;//记录事件的描述说明
    private String mktCampaignType;//事件分类
    private String recCampaignAmount;//推荐活动数量
    private String evtTrigType;//记录事件的触发类型,1000实时触发事件 2000定期触发事件 3000人工触发事件
    private Long contactEvtTypeId;//记录事件的所属事件类型标识


    public String getContactEvtName() {
        return contactEvtName;
    }

    public void setContactEvtName(String contactEvtName) {
        this.contactEvtName = contactEvtName;
    }

    public String getContactEvtDesc() {
        return contactEvtDesc;
    }

    public void setContactEvtDesc(String contactEvtDesc) {
        this.contactEvtDesc = contactEvtDesc;
    }

    public String getMktCampaignType() {
        return mktCampaignType;
    }

    public void setMktCampaignType(String mktCampaignType) {
        this.mktCampaignType = mktCampaignType;
    }

    public String getRecCampaignAmount() {
        return recCampaignAmount;
    }

    public void setRecCampaignAmount(String recCampaignAmount) {
        this.recCampaignAmount = recCampaignAmount;
    }

    public String getEvtTrigType() {
        return evtTrigType;
    }

    public void setEvtTrigType(String evtTrigType) {
        this.evtTrigType = evtTrigType;
    }

    public Long getContactEvtTypeId() {
        return contactEvtTypeId;
    }

    public void setContactEvtTypeId(Long contactEvtTypeId) {
        this.contactEvtTypeId = contactEvtTypeId;
    }
}
