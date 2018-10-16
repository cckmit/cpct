package com.zjtelcom.cpct.pojo;


import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class MktCamChlConf implements Serializable {
    // 推送渠道标识
    private Long evtContactConfId;

    // 推送渠道名称
    private String evtContactConfName;

    // 活动标识
    private Long mktCampaignId;

    // 活动编号
    private String mktActivityNbr;

    // 推送渠道标识
    private Long contactChlId;

    // 推送方式
    private String pushType;

    // 计算表达式
    private String ruleExpression;

    // 状态
    private String statusCd;

    private Long createStaff;

    private Long updateStaff;

    private Date createDate;

    private Date updateDate;

    private Date statusDate;

    private String remark;

    private String actType;

    private List<MktCamScript> mktCamScripts;

    private List<MktCamQuest> mktCamQuests;

    private List<MktCamChlConfAttr> mktCamChlConfAttrs;

    public Long getEvtContactConfId() {
        return evtContactConfId;
    }

    public void setEvtContactConfId(Long evtContactConfId) {
        this.evtContactConfId = evtContactConfId;
    }

    public String getEvtContactConfName() {
        return evtContactConfName;
    }

    public void setEvtContactConfName(String evtContactConfName) {
        this.evtContactConfName = evtContactConfName;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public Long getContactChlId() {
        return contactChlId;
    }

    public void setContactChlId(Long contactChlId) {
        this.contactChlId = contactChlId;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public Long getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(Long createStaff) {
        this.createStaff = createStaff;
    }

    public Long getUpdateStaff() {
        return updateStaff;
    }

    public void setUpdateStaff(Long updateStaff) {
        this.updateStaff = updateStaff;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public List<MktCamScript> getMktCamScripts() {
        return mktCamScripts;
    }

    public void setMktCamScripts(List<MktCamScript> mktCamScripts) {
        this.mktCamScripts = mktCamScripts;
    }

    public List<MktCamQuest> getMktCamQuests() {
        return mktCamQuests;
    }

    public void setMktCamQuests(List<MktCamQuest> mktCamQuests) {
        this.mktCamQuests = mktCamQuests;
    }

    public List<MktCamChlConfAttr> getMktCamChlConfAttrs() {
        return mktCamChlConfAttrs;
    }

    public void setMktCamChlConfAttrs(List<MktCamChlConfAttr> mktCamChlConfAttrs) {
        this.mktCamChlConfAttrs = mktCamChlConfAttrs;
    }

    public String getRuleExpression() {
        return ruleExpression;
    }

    public void setRuleExpression(String ruleExpression) {
        this.ruleExpression = ruleExpression;
    }

    public String getMktActivityNbr() {
        return mktActivityNbr;
    }

    public void setMktActivityNbr(String mktActivityNbr) {
        this.mktActivityNbr = mktActivityNbr;
    }

}