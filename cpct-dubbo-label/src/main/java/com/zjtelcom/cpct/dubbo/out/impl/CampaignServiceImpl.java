package com.zjtelcom.cpct.dubbo.out.impl;

import com.zjtelcom.cpct.dubbo.out.CampaignService;
import com.zjtelcom.cpct.service.campaign.MktCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private MktCampaignService campaignService;

    /**
     * 活动延期短信通知
     * @return
     */
    @Override
    public void campaignDelayNotice() {
        campaignService.campaignDelayNotice();
    }
}
