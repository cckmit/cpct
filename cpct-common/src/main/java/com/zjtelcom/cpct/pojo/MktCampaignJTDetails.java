/*
 * 文件名：MktCampaignDetails.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年11月1日
 * 修改内容：
 */

package com.zjtelcom.cpct.pojo;


import com.zjtelcom.cpct.domain.campaign.MktCamGrpRul;
import com.zjtelcom.cpct.domain.campaign.MktCamItem;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;
import com.zjtelcom.cpct.dto.campaign.MktCamEvtRel;
import com.zjtelcom.cpct.dto.campaign.MktCpcAlgorithmsRul;
import com.zjtelcom.cpct.dto.event.EventScene;
import com.zjtelcom.cpct.dto.event.EvtSceneCamRel;
import com.zjtelcom.cpct.dto.strategy.MktStrategy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 活动详情包含的对象
 * @author taowenwu
 * @version 1.0
 * @see MktCampaignJTDetails
 * @since JDK1.7
 */

public class MktCampaignJTDetails implements Serializable {
    //客户分群规则
    private List<MktCamGrpRul> mktCamGrpRuls = new ArrayList<>();

    //推荐条目
    private List<MktCamItem> mktCamItems = new ArrayList<>();

    //接触渠道配置
    private List<MktCamChlConf> mktCamChlConfDetails = new ArrayList<>();

    //营销活动脚本
    private List<MktCamScript> mktCamScripts = new ArrayList<>();

    //营销活动调查问卷关系
    private List<MktCamQuest> mktCamQuests = new ArrayList<>();

    //营销活动执行渠道配置属性
    private List<MktCamChlConfAttr> mktCamChlConfAttrs = new ArrayList<>();

    //营销执行算法规则关联
    private List<MktCamRecomCalcRel> recomCalcRels = new ArrayList<>();

    //CPC算法
    private List<MktCpcAlgorithmsRul> mktCpcAlgorithmsRulDetails = new ArrayList<>();

    //事件活动关联
    private List<MktCamEvtRel> evtRels = new ArrayList<>();

    //事件
    private List<MktContactEvt> mktCampaignEvts = new ArrayList<>();

    //营销活动渠道执行策略
    private List<MktCamStrategyRel> strategyRels = new ArrayList<>();

    //营销维挽策略
    private List<MktStrategy> mktCampaignStrategyDetails = new ArrayList<>();

    //事件场景与营销活动关系
    private List<EvtSceneCamRel> sceneCamRels = new ArrayList<>();

    //事件场景
    private List<EventScene> eventScenes = new ArrayList<>();

    //返回结果
    private List<Map<String, Object>> mktCampaigns = new ArrayList<>();

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

    public List<MktCamScript> getMktCamScripts() {
        return mktCamScripts;
    }

    public void setMktCamScripts(List<MktCamScript> mktCamScripts) {
        this.mktCamScripts = mktCamScripts;
    }

    public List<MktCamQuest> getMktCamQuests() {
        return mktCamQuests;
    }

    public void setMktCamQuests(List<MktCamQuest> mktCamQuests) {
        this.mktCamQuests = mktCamQuests;
    }

    public List<MktCamChlConfAttr> getMktCamChlConfAttrs() {
        return mktCamChlConfAttrs;
    }

    public void setMktCamChlConfAttrs(List<MktCamChlConfAttr> mktCamChlConfAttrs) {
        this.mktCamChlConfAttrs = mktCamChlConfAttrs;
    }

    public List<MktCamRecomCalcRel> getRecomCalcRels() {
        return recomCalcRels;
    }

    public void setRecomCalcRels(List<MktCamRecomCalcRel> recomCalcRels) {
        this.recomCalcRels = recomCalcRels;
    }

    public List<MktCpcAlgorithmsRul> getMktCpcAlgorithmsRulDetails() {
        return mktCpcAlgorithmsRulDetails;
    }

    public void setMktCpcAlgorithmsRulDetails(List<MktCpcAlgorithmsRul> mktCpcAlgorithmsRulDetails) {
        this.mktCpcAlgorithmsRulDetails = mktCpcAlgorithmsRulDetails;
    }

    public List<MktCamEvtRel> getEvtRels() {
        return evtRels;
    }

    public void setEvtRels(List<MktCamEvtRel> evtRels) {
        this.evtRels = evtRels;
    }

    public List<MktContactEvt> getMktCampaignEvts() {
        return mktCampaignEvts;
    }

    public void setMktCampaignEvts(List<MktContactEvt> mktCampaignEvts) {
        this.mktCampaignEvts = mktCampaignEvts;
    }

    public List<MktCamStrategyRel> getStrategyRels() {
        return strategyRels;
    }

    public void setStrategyRels(List<MktCamStrategyRel> strategyRels) {
        this.strategyRels = strategyRels;
    }

    public List<MktStrategy> getMktCampaignStrategyDetails() {
        return mktCampaignStrategyDetails;
    }

    public void setMktCampaignStrategyDetails(List<MktStrategy> mktCampaignStrategyDetails) {
        this.mktCampaignStrategyDetails = mktCampaignStrategyDetails;
    }

    public List<EvtSceneCamRel> getSceneCamRels() {
        return sceneCamRels;
    }

    public void setSceneCamRels(List<EvtSceneCamRel> sceneCamRels) {
        this.sceneCamRels = sceneCamRels;
    }

    public List<EventScene> getEventScenes() {
        return eventScenes;
    }

    public void setEventScenes(List<EventScene> eventScenes) {
        this.eventScenes = eventScenes;
    }

    public List<Map<String, Object>> getMktCampaigns() {
        return mktCampaigns;
    }

    public void setMktCampaigns(List<Map<String, Object>> mktCampaigns) {
        this.mktCampaigns = mktCampaigns;
    }

}
