package com.zjtelcom.cpct.dto.pojo;


import com.zjtelcom.cpct.domain.campaign.MktCamGrpRul;
import com.zjtelcom.cpct.domain.campaign.MktCamItem;
import com.zjtelcom.cpct.dto.campaign.MktCpcAlgorithmsRul;
import com.zjtelcom.cpct.dto.event.EventScene;
import com.zjtelcom.cpct.dto.strategy.MktStrategy;
import com.zjtelcom.cpct.pojo.MktCamChlConf;
import com.zjtelcom.cpct.pojo.MktContactEvt;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class MktCampaignPO implements Serializable {
    private Long mktCampaignId;

    private String tiggerType;

    private String mktCampaignName;

    private Date planBeginTime;

    private Date planEndTime;

    private Date beginTime;

    private Date endTime;

    private String mktCampaignType;

    private String mktActivityNbr;

    private String mktActivityTarget;

    private String mktCampaignDesc;

    private Long calcDisplay;

    private Long isaleDisplay;

    private String execType;

    private String execInvl;

    private Integer execNum;

    private String statusCd;

    private Date statusDate;

    private Long createStaff;

    private Date createDate;

    private Long updateStaff;

    private Date updateDate;

    private String remark;

    private Long lanId;

    private Long strategyId;

    private List<MktCamGrpRul> mktCamGrpRuls;

    private List<MktCamItem> mktCamItems;

    private List<MktCamChlConf> mktCamChlConfDetails;

    private List<MktCpcAlgorithmsRul> mktCpcAlgorithmsRulDetails;

    private List<MktContactEvt> mktCampaignEvts;

    private List<MktStrategy> mktCampaignStrategyDetails;

    private List<EventScene> eventScenes;

    private String actType;

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getLanId() {
        return lanId;
    }

    public void setLanId(Long lanId) {
        this.lanId = lanId;
    }

    public Long getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Long strategyId) {
        this.strategyId = strategyId;
    }

    public List<MktCamGrpRul> getMktCamGrpRuls() {
        return mktCamGrpRuls;
    }

    public void setMktCamGrpRuls(List<MktCamGrpRul> mktCamGrpRuls) {
        this.mktCamGrpRuls = mktCamGrpRuls;
    }

    public List<MktCamItem> getMktCamItems() {
        return mktCamItems;
    }

    public void setMktCamItems(List<MktCamItem> mktCamItems) {
        this.mktCamItems = mktCamItems;
    }

    public List<MktCamChlConf> getMktCamChlConfDetails() {
        return mktCamChlConfDetails;
    }

    public void setMktCamChlConfDetails(List<MktCamChlConf> mktCamChlConfDetails) {
        this.mktCamChlConfDetails = mktCamChlConfDetails;
    }

    public List<MktCpcAlgorithmsRul> getMktCpcAlgorithmsRulDetails() {
        return mktCpcAlgorithmsRulDetails;
    }

    public void setMktCpcAlgorithmsRulDetails(List<MktCpcAlgorithmsRul> mktCpcAlgorithmsRulDetails) {
        this.mktCpcAlgorithmsRulDetails = mktCpcAlgorithmsRulDetails;
    }

    public List<MktContactEvt> getMktCampaignEvts() {
        return mktCampaignEvts;
    }

    public void setMktCampaignEvts(List<MktContactEvt> mktCampaignEvts) {
        this.mktCampaignEvts = mktCampaignEvts;
    }

    public List<MktStrategy> getMktCampaignStrategyDetails() {
        return mktCampaignStrategyDetails;
    }

    public void setMktCampaignStrategyDetails(List<MktStrategy> mktCampaignStrategyDetails) {
        this.mktCampaignStrategyDetails = mktCampaignStrategyDetails;
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

    public List<EventScene> getEventScenes() {
        return eventScenes;
    }

    public void setEventScenes(List<EventScene> eventScenes) {
        this.eventScenes = eventScenes;
    }

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

	public MktCampaignPO() {
		super();
	}

	public MktCampaignPO(Long mktCampaignId, String tiggerType,
                         String mktCampaignName, Date planBeginTime, Date planEndTime,
                         Date beginTime, Date endTime, String mktCampaignType,
                         String mktActivityNbr, String mktActivityTarget,
                         String mktCampaignDesc, Long calcDisplay, Long isaleDisplay, String execType, String execInvl,
                         Integer execNum, String statusCd, Date statusDate,
                         Long createStaff, Date createDate, Long updateStaff,
                         Date updateDate, String remark, Long lanId, Long strategyId,
                         List<MktCamGrpRul> mktCamGrpRuls, List<MktCamItem> mktCamItems,
                         List<MktCamChlConf> mktCamChlConfDetails,
                         List<MktCpcAlgorithmsRul> mktCpcAlgorithmsRulDetails,
                         List<MktContactEvt> mktCampaignEvts,
                         List<MktStrategy> mktCampaignStrategyDetails,
                         List<EventScene> eventScenes, String actType) {
		super();
		this.mktCampaignId = mktCampaignId;
		this.tiggerType = tiggerType;
		this.mktCampaignName = mktCampaignName;
		this.planBeginTime = planBeginTime;
		this.planEndTime = planEndTime;
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.mktCampaignType = mktCampaignType;
		this.mktActivityNbr = mktActivityNbr;
		this.mktActivityTarget = mktActivityTarget;
		this.mktCampaignDesc = mktCampaignDesc;
		this.calcDisplay = calcDisplay;
		this.isaleDisplay = isaleDisplay;
		this.execType = execType;
		this.execInvl = execInvl;
		this.execNum = execNum;
		this.statusCd = statusCd;
		this.statusDate = statusDate;
		this.createStaff = createStaff;
		this.createDate = createDate;
		this.updateStaff = updateStaff;
		this.updateDate = updateDate;
		this.remark = remark;
		this.lanId = lanId;
		this.strategyId = strategyId;
		this.mktCamGrpRuls = mktCamGrpRuls;
		this.mktCamItems = mktCamItems;
		this.mktCamChlConfDetails = mktCamChlConfDetails;
		this.mktCpcAlgorithmsRulDetails = mktCpcAlgorithmsRulDetails;
		this.mktCampaignEvts = mktCampaignEvts;
		this.mktCampaignStrategyDetails = mktCampaignStrategyDetails;
		this.eventScenes = eventScenes;
		this.actType = actType;
	}

    
    
}