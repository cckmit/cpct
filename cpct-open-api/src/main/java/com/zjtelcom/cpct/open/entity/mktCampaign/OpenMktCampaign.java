package com.zjtelcom.cpct.open.entity.mktCampaign;

import com.zjtelcom.cpct.domain.campaign.MktCamItem;
import com.zjtelcom.cpct.domain.channel.CamScript;
import com.zjtelcom.cpct.dto.strategy.MktStrategy;
import com.zjtelcom.cpct.open.base.entity.BaseEntity;
import com.zjtelcom.cpct.open.entity.mktCamItem.OpenMktCamItem;
import com.zjtelcom.cpct.open.entity.mktStrategy.OpenMktStrategy;
import com.zjtelcom.cpct.open.entity.script.OpenScript;

import java.util.List;


/**
 * @Auther: anson
 * @Date: 2018-11-05 17:32:51
 * @Description:营销活动 实体类 匹配集团openapi规范返回
 */
public class OpenMktCampaign{


    private Long id;
    private String href;
    private String beginTime;
    private String endTime;

    private String execInvl;
    private Integer execNum;
    private String execType;
    private String extMktCampaignId;

    private Long lanId;//本地网标识

    private String manageType;
    private String mktActivityNbr;
    private String mktActivityTarget;

    //营销活动推荐条目列表
    private List<OpenMktCamItem> mktCamItem;
    //营销活动脚本
    private List<OpenScript> mktCamScript;

    private String mktCampaignDesc;
    private String mktCampaignName;
    private String mktCampaignType;

    //营销维挽策略列表
    private List<OpenMktStrategy> mktStrategy;

    private String planBeginTime;
    private String planEndTime;
    private String remark;//备注
    private String statusCd;//记录状态。1000有效 1100无效  1200	未生效 1300已归档  1001将生效  1002待恢复  1101将失效  1102待失效 1301	待撤消
    private String tiggerType;
    private Long updateStaff;//更新人


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
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

    public String getExecType() {
        return execType;
    }

    public void setExecType(String execType) {
        this.execType = execType;
    }

    public Long getLanId() {
        return lanId;
    }

    public void setLanId(Long lanId) {
        this.lanId = lanId;
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

    public List<OpenMktCamItem> getMktCamItem() {
        return mktCamItem;
    }

    public void setMktCamItem(List<OpenMktCamItem> mktCamItem) {
        this.mktCamItem = mktCamItem;
    }

    public List<OpenScript> getMktCamScript() {
        return mktCamScript;
    }

    public void setMktCamScript(List<OpenScript> mktCamScript) {
        this.mktCamScript = mktCamScript;
    }

    public String getMktCampaignDesc() {
        return mktCampaignDesc;
    }

    public void setMktCampaignDesc(String mktCampaignDesc) {
        this.mktCampaignDesc = mktCampaignDesc;
    }

    public String getMktCampaignName() {
        return mktCampaignName;
    }

    public void setMktCampaignName(String mktCampaignName) {
        this.mktCampaignName = mktCampaignName;
    }

    public String getMktCampaignType() {
        return mktCampaignType;
    }

    public void setMktCampaignType(String mktCampaignType) {
        this.mktCampaignType = mktCampaignType;
    }

    public List<OpenMktStrategy> getMktStrategy() {
        return mktStrategy;
    }

    public void setMktStrategy(List<OpenMktStrategy> mktStrategy) {
        this.mktStrategy = mktStrategy;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String getTiggerType() {
        return tiggerType;
    }

    public void setTiggerType(String tiggerType) {
        this.tiggerType = tiggerType;
    }

    public Long getUpdateStaff() {
        return updateStaff;
    }

    public void setUpdateStaff(Long updateStaff) {
        this.updateStaff = updateStaff;
    }

    public String getExtMktCampaignId() {
        return extMktCampaignId;
    }

    public void setExtMktCampaignId(String extMktCampaignId) {
        this.extMktCampaignId = extMktCampaignId;
    }

    public String getManageType() {
        return manageType;
    }

    public void setManageType(String manageType) {
        this.manageType = manageType;
    }
}