package com.zjtelcom.cpct.dubbo.model;

import com.zjtelcom.cpct.dto.campaign.MktCampaign;
import com.zjtelcom.cpct.dto.filter.FilterRuleModel;

import java.util.ArrayList;


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
     * 触发类型Value
     */
    private String tiggerTypeValue;

    /**
     * 触发类型
     */
    private String tiggerType;

    /**
     * 活动周期类型Id
     */
    private String execTypeValue;

    /**
     * 活动周期类型
     */
    private String execType;

    /**
     * 执行间隔
     */
    private String execInvl;

    /**
     * 执行次数
     */
    private Integer execNum;

    /**
     * 状态value
     */
    private String statusCdValue;

    /**
     * 该活动是否有有效的父/子活动
     */
    private Boolean isRelation;

    /**
     * 策略配置信息
     */
    private ArrayList<MktStrategyConfResp> mktStrategyConfRespList;

    /**
     * 过滤规则
     */
    private ArrayList<FilterRuleModel> filterRuleModelList;


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

    public String getExecTypeValue() {
        return execTypeValue;
    }

    public void setExecTypeValue(String execTypeValue) {
        this.execTypeValue = execTypeValue;
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

    public String getStatusCdValue() {
        return statusCdValue;
    }

    public void setStatusCdValue(String statusCdValue) {
        this.statusCdValue = statusCdValue;
    }

    public Boolean getRelation() {
        return isRelation;
    }

    public void setRelation(Boolean relation) {
        isRelation = relation;
    }

    public ArrayList<MktStrategyConfResp> getMktStrategyConfRespList() {
        return mktStrategyConfRespList;
    }

    public void setMktStrategyConfRespList(ArrayList<MktStrategyConfResp> mktStrategyConfRespList) {
        this.mktStrategyConfRespList = mktStrategyConfRespList;
    }

    public ArrayList<FilterRuleModel> getFilterRuleModelList() {
        return filterRuleModelList;
    }

    public void setFilterRuleModelList(ArrayList<FilterRuleModel> filterRuleModelList) {
        this.filterRuleModelList = filterRuleModelList;
    }
}