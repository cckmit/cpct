package com.zjtelcom.cpct.open.entity.mktCampaignEntity;

import com.zjtelcom.cpct.open.entity.event.OpenEvtTrigCamRulEntity;
import com.zjtelcom.cpct.open.entity.mktCamChlConf.OpenMktCamChlConfEntity;
import com.zjtelcom.cpct.open.entity.mktCamItem.OpenMktCamItemEntity;

import java.util.Date;
import java.util.List;

public class OpenMktCampaignEntity {

    private String actType;
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
    private String execType;
    private String execInvl;
    private Long execNum;
    private String manageType;
    private String extMktCampaignId;
    private String statusCd;
    private Date statusDate;
    private Long createStaff;
    private Date createDate;
    private Long updateStaff;
    private Date updateDate;
    private String remark;
    private Long regionId;
    private String regionNbr;
    private String srcType;
    private String srcId;
    private String srcNbr;
    private String serviceType;
    private String lifeStage;
    private Long serviceCancleFlag;
    private Long lanId;
    //营服活动分群规则
    private List<OpenMktCamGrpRulEntity> mktCamGrpRuls;
    //营服活动推荐条目
    private List<OpenMktCamItemEntity> mktCamItems;
    //营服活动渠道推送配置
    private List<OpenMktCamChlConfEntity> mktCamChlConfs;
    //营服活动执行算法规则关联
    private List<OpenMktCamRecomCalcRelEntity> mktCamRecomCalcRels;
    //营服活动关联事件
    private List<OpenMktCamEvtRelEntity> mktCamEvtRels;
    //营服活动渠道执行策略
    private List<OpenMktCamStrategyRelEntity> mktCamStrategyRels;
    //营服活动关系
    private List<OpenMktCampaignRelEntity> mktCampaignRels;
    //事件触发活动规则
    private List<OpenEvtTrigCamRulEntity> evtTrigCamRuls;
    //营服活动校验规则
    private List<OpenMktCamCheckruleEntity> mktCamCheckrules;
    //对象区域关系
    private List<ObjRegionRel> objRegionRels;

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
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

    public Long getExecNum() {
        return execNum;
    }

    public void setExecNum(Long execNum) {
        this.execNum = execNum;
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

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public String getRegionNbr() {
        return regionNbr;
    }

    public void setRegionNbr(String regionNbr) {
        this.regionNbr = regionNbr;
    }

    public String getSrcType() {
        return srcType;
    }

    public void setSrcType(String srcType) {
        this.srcType = srcType;
    }

    public String getSrcId() {
        return srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }

    public String getSrcNbr() {
        return srcNbr;
    }

    public void setSrcNbr(String srcNbr) {
        this.srcNbr = srcNbr;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getLifeStage() {
        return lifeStage;
    }

    public void setLifeStage(String lifeStage) {
        this.lifeStage = lifeStage;
    }

    public Long getServiceCancleFlag() {
        return serviceCancleFlag;
    }

    public void setServiceCancleFlag(Long serviceCancleFlag) {
        this.serviceCancleFlag = serviceCancleFlag;
    }

    public List<OpenMktCamGrpRulEntity> getMktCamGrpRuls() {
        return mktCamGrpRuls;
    }

    public void setMktCamGrpRuls(List<OpenMktCamGrpRulEntity> mktCamGrpRuls) {
        this.mktCamGrpRuls = mktCamGrpRuls;
    }

    public List<OpenMktCamItemEntity> getMktCamItems() {
        return mktCamItems;
    }

    public void setMktCamItems(List<OpenMktCamItemEntity> mktCamItems) {
        this.mktCamItems = mktCamItems;
    }

    public List<OpenMktCamChlConfEntity> getMktCamChlConfs() {
        return mktCamChlConfs;
    }

    public void setMktCamChlConfs(List<OpenMktCamChlConfEntity> mktCamChlConfs) {
        this.mktCamChlConfs = mktCamChlConfs;
    }

    public List<OpenMktCamRecomCalcRelEntity> getMktCamRecomCalcRels() {
        return mktCamRecomCalcRels;
    }

    public void setMktCamRecomCalcRels(List<OpenMktCamRecomCalcRelEntity> mktCamRecomCalcRels) {
        this.mktCamRecomCalcRels = mktCamRecomCalcRels;
    }

    public List<OpenMktCamEvtRelEntity> getMktCamEvtRels() {
        return mktCamEvtRels;
    }

    public void setMktCamEvtRels(List<OpenMktCamEvtRelEntity> mktCamEvtRels) {
        this.mktCamEvtRels = mktCamEvtRels;
    }

    public List<OpenMktCamStrategyRelEntity> getMktCamStrategyRels() {
        return mktCamStrategyRels;
    }

    public void setMktCamStrategyRels(List<OpenMktCamStrategyRelEntity> mktCamStrategyRels) {
        this.mktCamStrategyRels = mktCamStrategyRels;
    }

    public List<OpenMktCampaignRelEntity> getMktCampaignRels() {
        return mktCampaignRels;
    }

    public void setMktCampaignRels(List<OpenMktCampaignRelEntity> mktCampaignRels) {
        this.mktCampaignRels = mktCampaignRels;
    }

    public List<OpenEvtTrigCamRulEntity> getEvtTrigCamRuls() {
        return evtTrigCamRuls;
    }

    public void setEvtTrigCamRuls(List<OpenEvtTrigCamRulEntity> evtTrigCamRuls) {
        this.evtTrigCamRuls = evtTrigCamRuls;
    }

    public List<OpenMktCamCheckruleEntity> getMktCamCheckrules() {
        return mktCamCheckrules;
    }

    public void setMktCamCheckrules(List<OpenMktCamCheckruleEntity> mktCamCheckrules) {
        this.mktCamCheckrules = mktCamCheckrules;
    }

    public List<ObjRegionRel> getObjRegionRels() {
        return objRegionRels;
    }

    public void setObjRegionRels(List<ObjRegionRel> objRegionRels) {
        this.objRegionRels = objRegionRels;
    }

    public Long getLanId() {
        return lanId;
    }

    public void setLanId(Long lanId) {
        this.lanId = lanId;
    }
}
