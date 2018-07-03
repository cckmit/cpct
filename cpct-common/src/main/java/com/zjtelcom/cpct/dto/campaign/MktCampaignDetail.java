package com.zjtelcom.cpct.dto.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamGrpRul;
import com.zjtelcom.cpct.dto.strategy.MktStrategyDetail;
import lombok.Data;

import java.util.List;

/**
 * @Author:sunpeng
 * @Descirption:营销活动服务对接dto
 * @Date: 2018/6/26.
 */
@Data
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
//    private List<ContactEvt> mktCampaignEvts;

    /**
     * 营销维挽策略详细信息
     */
    private List<MktStrategyDetail> mktCampaignStrategyDetails;

    /**
     * 事件场景
     */
//    private List<EventScene> eventScenes;





}