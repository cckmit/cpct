/**
 * @(#)MktCamResultRelDeatil.java, 2018/9/20.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.dto.campaign;

import java.util.List;

/**
 * @Description:
 * @author: linchao
 * @date: 2018/09/20 15:53
 * @version: V1.0
 */
public class MktCamResultRelDeatil {

    private Long mktCampaignId;

    private List<MktCamChlResult> mktCamChlResultList;

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public List<MktCamChlResult> getMktCamChlResultList() {
        return mktCamChlResultList;
    }

    public void setMktCamChlResultList(List<MktCamChlResult> mktCamChlResultList) {
        this.mktCamChlResultList = mktCamChlResultList;
    }
}