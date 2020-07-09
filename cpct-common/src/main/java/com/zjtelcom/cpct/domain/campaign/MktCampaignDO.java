package com.zjtelcom.cpct.domain.campaign;

import com.zjtelcom.cpct.BaseEntity;

import java.util.Date;

public class MktCampaignDO extends BaseEntity{

    private Long mktCampaignId;
    private Long initId;
    private String tiggerType;
    private String mktCampaignName;
    private Date planBeginTime;
    private Date planEndTime;
    private Date beginTime;
    private Date endTime;
    private String mktCampaignType;
    private String mktCampaignCategory;  // MANAGE_TYPE为集团后来新增加的字段，之前自定义为这个字段
    private String mktActivityNbr;
    private String mktActivityTarget;
    private String mktCampaignDesc;
    private Long calcDisplay;
    private Long isaleDisplay;
    private String execType;
    private String execInvl;
    private Integer execNum;
    private String createChannel;
    private Long directoryId;
    private Long camLevel;
    private String isCheckRule;
    private String srcId;
    private String srcType;
    private String serviceType;
    private Long serviceCancleFlag;
    private Long regionId;
    private String lifeStage;
    private String extMktCampaignId;
    private String execInitTime;
    private String strBeginTime;
    private String strEndTime;
    private Long lanIdFour;
    private Long lanIdFive;
    private String oneChannelFlg;//是否单渠道活动
    private String theMe;//活动主题
    private String regionFlg;//活动创建地市
    private String batchType;
    private String skipDisturbed;//是否屏蔽过扰
    private Integer autoTrial; // 是否自动派单（0-否，1-是）


    public String getSkipDisturbed() {
        return skipDisturbed;
    }

    public void setSkipDisturbed(String skipDisturbed) {
        this.skipDisturbed = skipDisturbed;
    }


    public String getOneChannelFlg() {
        return oneChannelFlg;
    }

    public void setOneChannelFlg(String oneChannelFlg) {
        this.oneChannelFlg = oneChannelFlg;
    }

    public String getTheMe() {
        return theMe;
    }

    public void setTheMe(String theMe) {
        this.theMe = theMe;
    }

    public String getRegionFlg() {
        return regionFlg;
    }

    public void setRegionFlg(String regionFlg) {
        this.regionFlg = regionFlg;
    }

    public String getStrBeginTime() {
        return strBeginTime;
    }

    public void setStrBeginTime(String strBeginTime) {
        this.strBeginTime = strBeginTime;
    }

    public String getStrEndTime() {
        return strEndTime;
    }

    public void setStrEndTime(String strEndTime) {
        this.strEndTime = strEndTime;
    }

    public Long getInitId() {
        return initId;
    }

    public void setInitId(Long initId) {
        this.initId = initId;
    }

    public Long getCalcDisplay() {
        return calcDisplay;
    }

    public void setCalcDisplay(Long calcDisplay) {
        this.calcDisplay = calcDisplay;
    }

    public Long getIsaleDisplay() {
        return isaleDisplay;
    }

    public void setIsaleDisplay(Long isaleDisplay) {
        this.isaleDisplay = isaleDisplay;
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

    public String getCreateChannel() {
        return createChannel;
    }

    public void setCreateChannel(String createChannel) {
        this.createChannel = createChannel;
    }


    public Long getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(Long directoryId) {
        this.directoryId = directoryId;
    }

    public Long getCamLevel() {
        return camLevel;
    }

    public void setCamLevel(Long camLevel) {
        this.camLevel = camLevel;
    }

    public String getIsCheckRule() {
        return isCheckRule;
    }

    public void setIsCheckRule(String isCheckRule) {
        this.isCheckRule = isCheckRule;
    }

    public String getSrcId() {
        return srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }

    public String getSrcType() {
        return srcType;
    }

    public void setSrcType(String srcType) {
        this.srcType = srcType;
    }

    public Long getServiceCancleFlag() {
        return serviceCancleFlag;
    }

    public void setServiceCancleFlag(Long serviceCancleFlag) {
        this.serviceCancleFlag = serviceCancleFlag;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public String getLifeStage() {
        return lifeStage;
    }

    public void setLifeStage(String lifeStage) {
        this.lifeStage = lifeStage;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getExtMktCampaignId() {
        return extMktCampaignId;
    }

    public void setExtMktCampaignId(String extMktCampaignId) {
        this.extMktCampaignId = extMktCampaignId;
    }

    public String getExecInitTime() {
        return execInitTime;
    }

    public void setExecInitTime(String execInitTime) {
        this.execInitTime = execInitTime;
    }

    public Long getLanIdFour() {
        return lanIdFour;
    }

    public void setLanIdFour(Long lanIdFour) {
        this.lanIdFour = lanIdFour;
    }

    public Long getLanIdFive() {
        return lanIdFive;
    }

    public void setLanIdFive(Long lanIdFive) {
        this.lanIdFive = lanIdFive;
    }

    public String getBatchType() {
        return batchType;
    }

    public void setBatchType(String batchType) {
        this.batchType = batchType;
    }

    public Integer getAutoTrial() {
        return autoTrial;
    }

    public void setAutoTrial(Integer autoTrial) {
        this.autoTrial = autoTrial;
    }
}