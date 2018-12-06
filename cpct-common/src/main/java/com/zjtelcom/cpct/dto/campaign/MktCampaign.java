package com.zjtelcom.cpct.dto.campaign;

import com.zjtelcom.cpct.BaseEntity;

import java.util.Date;

/**
 * @Author:sunpeng
 * @Descirption:营销活动服务对接基本dto父对象
 * @Date: 2018/6/26.
 */
public class MktCampaign extends BaseEntity {

    /**
     * 营销活动标识
     */
    private Long mktCampaignId;

    /**
     * 营销策略标识
     */
    private Long strategyId;

    /**
     * 营销活动名称
     */
    private String mktCampaignName;

    /**
     * 计划开始时间
     */
    private Date planBeginTime;

    /**
     * 计划结束时间
     */
    private Date planEndTime;

    /**
     * 实际开始时间
     */
    private Date beginTime;

    /**
     * 实际结束时间
     */
    private Date endTime;

    /**
     * 营销活动分类
     */
    private String mktCampaignType;

    /**
     * 营销活动编号
     */
    private String mktActivityNbr;

    /**
     * 营销活动目标
     */
    private String mktActivityTarget;

    /**
     * 营销活动描述
     */
    private String mktCampaignDesc;

    /**
     * 试运算展示列
     */
    private Long calcDisplay;

    /**
     * isale展示列
     */
    private Long isaleDisplay;

    /**
     * 创建渠道
     */
    private String createChannel;

    /**
     * 活动目录标识
     */
    private Long directoryId;

    /**
     * 二次营销中的上次活动的Id
     */
    private Long preMktCampaignId;

    /**
     * 所属地市名称
     */
    private String landName;

    /**
     * 活动优先级 0-10
     */
    private Long camLevel;

    /**
     * 是否进行规制校验
     */
    private String isCheckRule;

    private String srcId;

    private String srcType;

    private Long serviceCancleFlag;

    private Long regionId;

    private String lifeStage;

    private String serviceType;

    private String extMktCampaignId;


    public Long getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(Long directoryId) {
        this.directoryId = directoryId;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public Long getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Long strategyId) {
        this.strategyId = strategyId;
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

    public String getCreateChannel() {
        return createChannel;
    }

    public void setCreateChannel(String createChannel) {
        this.createChannel = createChannel;
    }

    public Long getPreMktCampaignId() {
        return preMktCampaignId;
    }

    public void setPreMktCampaignId(Long preMktCampaignId) {
        this.preMktCampaignId = preMktCampaignId;
    }

    public String getLandName() {
        return landName;
    }

    public void setLandName(String landName) {
        this.landName = landName;
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
}