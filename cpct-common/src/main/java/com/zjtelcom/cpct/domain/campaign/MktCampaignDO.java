package com.zjtelcom.cpct.domain.campaign;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;
import java.util.Date;

public class MktCampaignDO extends BaseEntity{

    private Long mktCampaignId;
    private String tiggerType;
    private String mktCampaignName;
    private Date planBeginTime;
    private Date planEndTime;
    private Date beginTime;
    private Date endTime;
    private String mktCampaignType;
    private String mktCampaignCategory;
    private String mktActivityNbr;
    private String mktActivityTarget;
    private String mktCampaignDesc;
    private String calcDisplay;
    private String isaleDisplay;
    private String execType;
    private String execInvl;
    private Integer execNum;


    public String getIsaleDisplay() {
        return isaleDisplay;
    }

    public void setIsaleDisplay(String isaleDisplay) {
        this.isaleDisplay = isaleDisplay;
    }

    public String getCalcDisplay() {
        return calcDisplay;
    }

    public void setCalcDisplay(String calcDisplay) {
        this.calcDisplay = calcDisplay;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public String getTiggerType() {
        return tiggerType;
    }

    public void setTiggerType(String tiggerType) {
        this.tiggerType = tiggerType;
    }

    public String getMktCampaignName() {
        return mktCampaignName;
    }

    public void setMktCampaignName(String mktCampaignName) {
        this.mktCampaignName = mktCampaignName;
    }

    public Date getPlanBeginTime() {
        return planBeginTime;
    }

    public void setPlanBeginTime(Date planBeginTime) {
        this.planBeginTime = planBeginTime;
    }

    public Date getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
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

    public String getMktCampaignType() {
        return mktCampaignType;
    }

    public void setMktCampaignType(String mktCampaignType) {
        this.mktCampaignType = mktCampaignType;
    }

    public String getMktActivityNbr() {
        return mktActivityNbr;
    }

    public void setMktActivityNbr(String mktActivityNbr) {
        this.mktActivityNbr = mktActivityNbr;
    }

    public String getMktActivityTarget() {
        return mktActivityTarget;
    }

    public void setMktActivityTarget(String mktActivityTarget) {
        this.mktActivityTarget = mktActivityTarget;
    }

    public String getMktCampaignDesc() {
        return mktCampaignDesc;
    }

    public void setMktCampaignDesc(String mktCampaignDesc) {
        this.mktCampaignDesc = mktCampaignDesc;
    }

    public String getExecType() {
        return execType;
    }

    public void setExecType(String execType) {
        this.execType = execType;
    }

    public String getExecInvl() {
        return execInvl;
    }

    public void setExecInvl(String execInvl) {
        this.execInvl = execInvl;
    }

    public Integer getExecNum() {
        return execNum;
    }

    public void setExecNum(Integer execNum) {
        this.execNum = execNum;
    }

    public String getMktCampaignCategory() {
        return mktCampaignCategory;
    }

    public void setMktCampaignCategory(String mktCampaignCategory) {
        this.mktCampaignCategory = mktCampaignCategory;
    }

}