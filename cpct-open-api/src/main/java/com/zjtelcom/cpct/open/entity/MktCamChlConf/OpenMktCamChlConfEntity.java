package com.zjtelcom.cpct.open.entity.mktCamChlConf;

import com.zjtelcom.cpct.open.entity.mktCampaignEntity.OpenMktCamQuestEntity;
import com.zjtelcom.cpct.open.entity.mktCampaignEntity.OpenMktCamScriptEntity;

import java.util.Date;
import java.util.List;

public class OpenMktCamChlConfEntity {

    private String actType;
    private Long evtContactConfId;
    private String evtContactConfName;
    private Long mktCampaignId;
    private String mktActivityNbr;
    private Long contactChlId;
    private String pushType;
    private Long policyId;
    private String statusCd;
    private Date statusDate;
    private Long createStaff;
    private Date createDate;
    private Long updateStaff;
    private Date updateDate;
    //营服活动脚本
    private List<OpenMktCamScriptEntity> mktCamScripts;
    //调查问卷
    private List<OpenMktCamQuestEntity> mktCamQuests;
    //营服活动执行渠道配置属性
    private List<OpenMktCamChlConfAttrEntity> mktCamChlConfAttrs;
    private String remark;

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

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

    public String getMktActivityNbr() {
        return mktActivityNbr;
    }

    public void setMktActivityNbr(String mktActivityNbr) {
        this.mktActivityNbr = mktActivityNbr;
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

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public Long getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(Long createStaff) {
        this.createStaff = createStaff;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getUpdateStaff() {
        return updateStaff;
    }

    public void setUpdateStaff(Long updateStaff) {
        this.updateStaff = updateStaff;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public List<OpenMktCamScriptEntity> getMktCamScripts() {
        return mktCamScripts;
    }

    public void setMktCamScripts(List<OpenMktCamScriptEntity> mktCamScripts) {
        this.mktCamScripts = mktCamScripts;
    }

    public List<OpenMktCamQuestEntity> getMktCamQuests() {
        return mktCamQuests;
    }

    public void setMktCamQuests(List<OpenMktCamQuestEntity> mktCamQuests) {
        this.mktCamQuests = mktCamQuests;
    }

    public List<OpenMktCamChlConfAttrEntity> getMktCamChlConfAttrs() {
        return mktCamChlConfAttrs;
    }

    public void setMktCamChlConfAttrs(List<OpenMktCamChlConfAttrEntity> mktCamChlConfAttrs) {
        this.mktCamChlConfAttrs = mktCamChlConfAttrs;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
