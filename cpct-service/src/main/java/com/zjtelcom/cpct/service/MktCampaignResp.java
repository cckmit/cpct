package com.zjtelcom.cpct.service;

import com.zjtelcom.cpct.domain.SysArea;
import com.zjtelcom.cpct.dto.campaign.MktCampaign;

import java.util.List;


public class MktCampaignResp extends MktCampaign{
    /**
     * 营销活动分类value
     */
    private String mktCampaignTypeValue;

    /**
     * 活动类别value（关系：强制活动，框架活动，自主活动）
     */
    private String mktCampaignCategoryValue;

    /**
     * 活动类别（关系：强制活动，框架活动，自主活动）
     */
    private String mktCampaignCategory;

    /**
     * 下发地市
     */
    private List<SysArea> sysAreaList;

    /**
     * 触发类型Value
     */
    private String tiggerTypeValue;

    /**
     * 触发类型
     */
    private String tiggerType;

    /**
     * 状态value
     */
    private String statusCdValue;

    /**
     * 策略配置信息
     */
    List<MktStrategyConfResp> mktStrategyConfRespList;

    public String getMktCampaignTypeValue() {
        return mktCampaignTypeValue;
    }

    public void setMktCampaignTypeValue(String mktCampaignTypeValue) {
        this.mktCampaignTypeValue = mktCampaignTypeValue;
    }

    public String getMktCampaignCategoryValue() {
        return mktCampaignCategoryValue;
    }

    public void setMktCampaignCategoryValue(String mktCampaignCategoryValue) {
        this.mktCampaignCategoryValue = mktCampaignCategoryValue;
    }

    public String getMktCampaignCategory() {
        return mktCampaignCategory;
    }

    public void setMktCampaignCategory(String mktCampaignCategory) {
        this.mktCampaignCategory = mktCampaignCategory;
    }

    public List<SysArea> getSysAreaList() {
        return sysAreaList;
    }

    public void setSysAreaList(List<SysArea> sysAreaList) {
        this.sysAreaList = sysAreaList;
    }

    public String getTiggerTypeValue() {
        return tiggerTypeValue;
    }

    public void setTiggerTypeValue(String tiggerTypeValue) {
        this.tiggerTypeValue = tiggerTypeValue;
    }

    public String getTiggerType() {
        return tiggerType;
    }

    public void setTiggerType(String tiggerType) {
        this.tiggerType = tiggerType;
    }

    public String getStatusCdValue() {
        return statusCdValue;
    }

    public void setStatusCdValue(String statusCdValue) {
        this.statusCdValue = statusCdValue;
    }


    public List<MktStrategyConfResp> getMktStrategyConfRespList() {
        return mktStrategyConfRespList;
    }

    public void setMktStrategyConfRespList(List<MktStrategyConfResp> mktStrategyConfRespList) {
        this.mktStrategyConfRespList = mktStrategyConfRespList;
    }

}