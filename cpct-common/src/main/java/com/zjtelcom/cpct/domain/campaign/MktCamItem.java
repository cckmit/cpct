package com.zjtelcom.cpct.domain.campaign;


/**
 * 定义营销活动的可推荐条目,支持通过推荐条目组定义不同的客户目标分群可推荐不同
 * 的条目.
 */

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

/**
 * @author:sunpeng
 * @descirption:定义营销活动的可推荐条目,支持通过推荐条目组定义不同的客户目标分群可推荐不同的条目.
 * @date: 2018/6/26.
 */
@Data
public class MktCamItem extends BaseEntity{


    /**
     * 通用据操作类型
     */
    private String actType;

    /**
     * 活动条目标识
     */
    private Long mktCamItemId;

    /**
     * 营销活动标识
     */
    private Long mktCampaignId;

    /**
     * 推荐条目类型
     */
    private String itemType;

    /**
     * 推荐条目标识
     */
    private Long itemId;

    /**
     * 优先级
     */
    private Long priority;

    /**
     * 推荐条目组
     */
    private Long itemGroup;


}
