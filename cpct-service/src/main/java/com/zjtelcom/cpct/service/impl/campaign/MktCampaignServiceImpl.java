/**
 * @(#)MktCampaignServiceImpl.java, 2018/6/22.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.campaign;

import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.domain.campaign.DTO.MktCampaign;
import com.zjtelcom.cpct.service.campaign.MktCampaignService;
import org.springframework.stereotype.Service;

/**
 * Description:
 * author: linchao
 * date: 2018/06/22 22:22
 * version: V1.0
 */
@Service
public class MktCampaignServiceImpl implements MktCampaignService {

    private MktCampaignMapper mktCampaignMapper;

    /**
     * 添加活动
     *
     * @param mktCampaign
     * @return
     */
    @Override
    public int saveMktCampaign(MktCampaign mktCampaign) {
        int  mktCampaignId = mktCampaignMapper.insert(mktCampaign);
        return mktCampaignId;
    }

}