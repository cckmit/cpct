/**
 * @(#)MktCampaignVO.java, 2018/7/7.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.dto.campaign;

/**
 * Description:
 * author: linchao
 * date: 2018/07/07 15:25
 * version: V1.0
 */
public class MktCampaignVO extends MktCampaign {

    /**
     * 活动关联的事件Id
     */
    private Long eventId;

    /**
     * 活动类别（关系：强制活动，框架活动，自主活动）
     */
    private String relType;

    /**
     * 下发地市Id
     */
    private String applyRegionId;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getRelType() {
        return relType;
    }

    public void setRelType(String relType) {
        this.relType = relType;
    }

    public String getApplyRegionId() {
        return applyRegionId;
    }

    public void setApplyRegionId(String applyRegionId) {
        this.applyRegionId = applyRegionId;
    }
}