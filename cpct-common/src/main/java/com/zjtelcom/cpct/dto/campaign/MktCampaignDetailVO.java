package com.zjtelcom.cpct.dto.campaign;

import com.zjtelcom.cpct.domain.campaign.CityProperty;
import com.zjtelcom.cpct.dto.event.EventDTO;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConf;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfDetail;

import java.util.List;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/01/03 18:49
 * @version: V1.0
 */
public class MktCampaignDetailVO extends MktCampaign  {

    /**
     * 活动关联的事件
     */
    private List<EventDTO> eventDTOS;

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
     * 下发地市Id
     */
    private List<CityProperty> applyRegionIdList;

    /**
     * 触发类型Value
     */
    private String tiggerTypeValue;

    /**
     * 触发类型
     */
    private String tiggerType;

    /**
     * 活动周期类型value
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


    private String statusExamine;

    /**
     * 活动目录名称
     */
    private String directoryName;

    /**
     * 该活动是否有有效的父/子活动
     */
    private Boolean isRelation;

    /**
     * 活动关联策略集合
     */
    private List<MktStrategyConf> mktStrategyConfList;

    /**
     * 活动关联策略详情集合
     */
    private List<MktStrategyConfDetail> mktStrategyConfDetailList;

    /**
     * 推荐条目id集合
     */
    private List<Long> mktCamItemIdList;


    //需求涵id
    private Long requestId;

    /**
     * 过滤规则id集合
     */
    private List<Long>  filterRuleIdList;


    public List<EventDTO> getEventDTOS() {
        return eventDTOS;
    }

    public void setEventDTOS(List<EventDTO> eventDTOS) {
        this.eventDTOS = eventDTOS;
    }

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

    public List<CityProperty> getApplyRegionIdList() {
        return applyRegionIdList;
    }

    public void setApplyRegionIdList(List<CityProperty> applyRegionIdList) {
        this.applyRegionIdList = applyRegionIdList;
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

    public String getStatusExamine() {
        return statusExamine;
    }

    public void setStatusExamine(String statusExamine) {
        this.statusExamine = statusExamine;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public Boolean getRelation() {
        return isRelation;
    }

    public void setRelation(Boolean relation) {
        isRelation = relation;
    }

    public List<MktStrategyConf> getMktStrategyConfList() {
        return mktStrategyConfList;
    }

    public void setMktStrategyConfList(List<MktStrategyConf> mktStrategyConfList) {
        this.mktStrategyConfList = mktStrategyConfList;
    }

    public List<MktStrategyConfDetail> getMktStrategyConfDetailList() {
        return mktStrategyConfDetailList;
    }

    public void setMktStrategyConfDetailList(List<MktStrategyConfDetail> mktStrategyConfDetailList) {
        this.mktStrategyConfDetailList = mktStrategyConfDetailList;
    }

    public List<Long> getMktCamItemIdList() {
        return mktCamItemIdList;
    }

    public void setMktCamItemIdList(List<Long> mktCamItemIdList) {
        this.mktCamItemIdList = mktCamItemIdList;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public List<Long> getFilterRuleIdList() {
        return filterRuleIdList;
    }

    public void setFilterRuleIdList(List<Long> filterRuleIdList) {
        this.filterRuleIdList = filterRuleIdList;
    }
}