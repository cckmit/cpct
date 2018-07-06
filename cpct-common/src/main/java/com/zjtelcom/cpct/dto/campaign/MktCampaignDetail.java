package com.zjtelcom.cpct.dto.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamGrpRul;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.EventScene;
import com.zjtelcom.cpct.dto.strategy.MktStrategyDetail;
import lombok.Data;

import java.util.List;

/**
 * @Author:sunpeng
 * @Descirption:营销活动服务对接dto
 * @Date: 2018/6/26.
 */
public class MktCampaignDetail extends MktCampaign{

    /**
     * 活动客户分群规则列表
     */
    private List<MktCamGrpRul> mktCamGrpRuls;

    /**
     * 营销活动推荐条目
     */
    private List<MktCamItem> mktCamItems;

    /**
     * 营销活动执行渠道配置详细信息
     */
    private List<MktCamChlConfDetail> mktCamChlConfDetails;

    /**
     * CPC 算法规则详细信息
     */
    private List<MktCpcAlgorithmsRulDetail> mktCpcAlgorithmsRulDetails;

    /**
     * 事件
     */
    private List<ContactEvt> mktCampaignEvts;

    /**
     * 营销维挽策略详细信息
     */
    private List<MktStrategyDetail> mktCampaignStrategyDetails;

    /**
     * 事件场景
     */
    private List<EventScene> eventScenes;


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

    public List<MktCamChlConfDetail> getMktCamChlConfDetails() {
        return mktCamChlConfDetails;
    }

    public void setMktCamChlConfDetails(List<MktCamChlConfDetail> mktCamChlConfDetails) {
        this.mktCamChlConfDetails = mktCamChlConfDetails;
    }

    public List<MktCpcAlgorithmsRulDetail> getMktCpcAlgorithmsRulDetails() {
        return mktCpcAlgorithmsRulDetails;
    }

    public void setMktCpcAlgorithmsRulDetails(List<MktCpcAlgorithmsRulDetail> mktCpcAlgorithmsRulDetails) {
        this.mktCpcAlgorithmsRulDetails = mktCpcAlgorithmsRulDetails;
    }

    public List<ContactEvt> getMktCampaignEvts() {
        return mktCampaignEvts;
    }

    public void setMktCampaignEvts(List<ContactEvt> mktCampaignEvts) {
        this.mktCampaignEvts = mktCampaignEvts;
    }

    public List<MktStrategyDetail> getMktCampaignStrategyDetails() {
        return mktCampaignStrategyDetails;
    }

    public void setMktCampaignStrategyDetails(List<MktStrategyDetail> mktCampaignStrategyDetails) {
        this.mktCampaignStrategyDetails = mktCampaignStrategyDetails;
    }

    public List<EventScene> getEventScenes() {
        return eventScenes;
    }

    public void setEventScenes(List<EventScene> eventScenes) {
        this.eventScenes = eventScenes;
    }
}