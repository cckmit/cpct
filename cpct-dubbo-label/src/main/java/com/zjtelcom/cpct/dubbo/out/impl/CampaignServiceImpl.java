package com.zjtelcom.cpct.dubbo.out.impl;

import com.zjtelcom.cpct.dubbo.out.CampaignService;
import com.zjtelcom.cpct.service.campaign.MktCampaignService;
import com.zjtelcom.cpct.service.scheduled.ScheduledTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private MktCampaignService campaignService;
    @Autowired
    private ScheduledTaskService scheduledTaskService;

    /**
     * 活动延期短信通知
     * @return
     */
    @Override
    public void campaignDelayNotice() {
        campaignService.campaignDelayNotice();
    }

    /**
     * 定时任务：每个派单成功活动取最后一个批次若该批次下发时间超过20天（可变），查询处理率若低于1%（可变)，则自动失效，并短信通知
     */
    @Override
    public void issuedSuccessMktCheck() {
        scheduledTaskService.issuedSuccessMktCheck();
    }
}
