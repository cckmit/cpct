package com.zjtelcom.cpct.open.entity.mktCampaign;

import com.zjtelcom.cpct.open.entity.mktCamItem.OpenMktCamItem;
import com.zjtelcom.cpct.open.entity.mktStrategy.OpenMktStrategy;
import com.zjtelcom.cpct.open.entity.script.OpenScript;

import java.util.List;

/**
 * @Auther: anson
 * @Date: 2018-11-05 17:32:51
 * @Description:营销活动 实体类 匹配集团openapi规范返回
 */
public class OpenMktCampaign {

    private Integer mktCampaignId;
    private String mktCampaignName;
    private String planBeginTime;
    private String planEndTime;
    private String beginTime;
    private String endTime;
    private String mktCampaignType;
    private String mktActivityNbr;
    private String mktActivityTarget;
    private String mktCampaignDesc;
    private String execType;
    private Integer execNum;
    private String execInvl;
    private String manageType;
    private String extMktCampaignId;
    private Integer lanId;//本地网标识
    //营销活动推荐条目列表
    private List<OpenMktCamItem> mktCamItems;
    //营销活动脚本
    private List<OpenScript> mktScripts;
    //营销维挽策略列表
    private List<OpenMktStrategy> mktStrategys;

    public Integer getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Integer mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public String getMktCampaignName() {
        return mktCampaignName;
    }

    public void setMktCampaignName(String mktCampaignName) {
        this.mktCampaignName = mktCampaignName;
    }

    public String getPlanBeginTime() {
        return planBeginTime;
    }

    public void setPlanBeginTime(String planBeginTime) {
        this.planBeginTime = planBeginTime;
    }

    public String getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(String planEndTime) {
        this.planEndTime = planEndTime;
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

    public Integer getExecNum() {
        return execNum;
    }

    public void setExecNum(Integer execNum) {
        this.execNum = execNum;
    }

    public String getExecInvl() {
        return execInvl;
    }

    public void setExecInvl(String execInvl) {
        this.execInvl = execInvl;
    }

    public String getManageType() {
        return manageType;
    }

    public void setManageType(String manageType) {
        this.manageType = manageType;
    }

    public String getExtMktCampaignId() {
        return extMktCampaignId;
    }

    public void setExtMktCampaignId(String extMktCampaignId) {
        this.extMktCampaignId = extMktCampaignId;
    }

    public Integer getLanId() {
        return lanId;
    }

    public void setLanId(Integer lanId) {
        this.lanId = lanId;
    }

    public List<OpenMktCamItem> getMktCamItems() {
        return mktCamItems;
    }

    public void setMktCamItems(List<OpenMktCamItem> mktCamItems) {
        this.mktCamItems = mktCamItems;
    }

    public List<OpenScript> getMktScripts() {
        return mktScripts;
    }

    public void setMktScripts(List<OpenScript> mktScripts) {
        this.mktScripts = mktScripts;
    }

    public List<OpenMktStrategy> getMktStrategys() {
        return mktStrategys;
    }

    public void setMktStrategys(List<OpenMktStrategy> mktStrategys) {
        this.mktStrategys = mktStrategys;
    }
}
