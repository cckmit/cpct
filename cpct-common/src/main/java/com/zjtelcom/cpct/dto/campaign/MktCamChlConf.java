package com.zjtelcom.cpct.dto.campaign;

import com.zjtelcom.cpct.BaseEntity;

/**
 * @author:sunpeng
 * @descirption:营销活动渠道推送配置
 * @date: 2018/6/26.
 */
public class MktCamChlConf extends BaseEntity {

    /**
     * 通用数据操作类型
     */
    private String actType;

    /**
     * 执行渠道推送配置标识
     */
    private Long evtContactConfId;

    /**
     * 执行渠道推送配置名称
     */
    private String evtContactConfName;

    /**
     * 营销活动标识
     */
    private Long mktCampaignId;

    /**
     * 推送渠道标识
     */
    private Long contactChlId;

    /**
     * 推送方式
     */
    private String pushType;

    /**
     * 计算表达式
     */
    private String ruleExpression;



}
