package com.zjtelcom.cpct.domain.openApi.mktCamChlConf;

import com.zjtelcom.cpct.domain.openApi.mktCampaignEntity.OpenMktCamScriptEntity;

import java.util.Date;
import java.util.List;

public class OpenMktCamChlConfEntity {

    private String actType;
    private Long evtContactConfId;
    private String evtContactConfName;
    private Long mktCampaignId;
    private String mktActivityNbr;
    private Long contactChlId;
    private String contactChlCode;
    private String contactChlName;
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
    private String remark;

    public String getContactChlName() {
        return contactChlName;
    }

    public void setContactChlName(String contactChlName) {
        this.contactChlName = contactChlName;
    }

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

    public String getContactChlCode() {
        return contactChlCode;
    }

    public void setContactChlCode(String contactChlCode) {
        this.contactChlCode = contactChlCode;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
