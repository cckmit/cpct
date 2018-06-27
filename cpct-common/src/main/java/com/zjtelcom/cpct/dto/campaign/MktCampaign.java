package com.zjtelcom.cpct.dto.campaign;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @Author:sunpeng
 * @Descirption:营销活动服务对接基本dto父对象
 * @Date: 2018/6/26.
 */
@Data
public class MktCampaign extends BaseEntity {

    /**
     * 营销活动标识
     */
    private Long mktCampaignId;

    /**
     * 营销策略标识
     */
    private Long strategyId;

    /**
     * 营销活动名称
     */
    private String mktCampaignName;

    /**
     * 计划开始时间
     */
    private Date planBeginTime;

    /**
     * 计划结束时间
     */
    private Date planEndTime;

    /**
     * 实际开始时间
     */
    private Date beginTime;

    /**
     * 实际结束时间
     */
    private Date endTime;

    /**
     * 营销活动分类
     */
    private String mktCampaignType;

    /**
     * 营销活动编号
     */
    private String mktActivityNbr;

    /**
     * 营销活动目标
     */
    private String mktActivityTarget;

    /**
     * 营销活动描述
     */
    private String mktCampaignDesc;

}