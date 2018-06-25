package com.zjtelcom.cpct.domain.event;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;


@Data
public class EvtSceneCamRelDO extends BaseEntity{

    private Long sceneCamRelId;//事件场景营销活动关系标识
    private Long eventSceneId;//事件场景标识
    private Integer campaignSeq;//活动顺序
    private Long mktCampaignId;//营销活动标识

}