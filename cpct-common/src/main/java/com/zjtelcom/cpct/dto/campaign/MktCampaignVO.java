/**
 * @(#)MktCampaignVO.java, 2018/7/7.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.dto.campaign;

import com.zjtelcom.cpct.domain.campaign.CityProperty;
import com.zjtelcom.cpct.dto.event.EventDTO;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConf;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfDetail;

import java.util.List;

/**
 * Description:
 * author: linchao
 * date: 2018/07/07 15:25
 * version: V1.0
 */
public class MktCampaignVO extends MktCampaign {

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
     * 状态Id
     */
    private String statusCd;

    /**
     * 状态value
     */
    private String statusCdValue;

    /**
     * 活动关联策略集合
     */
    private List<MktStrategyConf> mktStrategyConfList;


    private List<MktStrategyConfDetail> mktStrategyConfDetailList;


    public List<EventDTO> getEventDTOS() {
        return eventDTOS;
    }

    public void setEventDTOS(List<EventDTO> eventDTOS) {
        this.eventDTOS = eventDTOS;
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

    public String getTiggerType() {
        return tiggerType;
    }

    public void setTiggerType(String tiggerType) {
        this.tiggerType = tiggerType;
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

    public String getMktCampaignTypeValue() {
        return mktCampaignTypeValue;
    }

    public void setMktCampaignTypeValue(String mktCampaignTypeValue) {
        this.mktCampaignTypeValue = mktCampaignTypeValue;
    }

    public String getTiggerTypeValue() {
        return tiggerTypeValue;
    }

    public void setTiggerTypeValue(String tiggerTypeValue) {
        this.tiggerTypeValue = tiggerTypeValue;
    }

    public String getExecTypeValue() {
        return execTypeValue;
    }

    public void setExecTypeValue(String execTypeValue) {
        this.execTypeValue = execTypeValue;
    }


    public String getStatusCdValue() {
        return statusCdValue;
    }

    public void setStatusCdValue(String statusCdValue) {
        this.statusCdValue = statusCdValue;
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
}