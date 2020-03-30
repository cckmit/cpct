package com.zjtelcom.cpct.open.service.dubbo;

import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.open.entity.event.OpenEvent;

public interface UCCPService {

    void sendShortMessage4GroupEventRecipient(OpenEvent openEvent);

    void sendShortMessage4GroupCampaignRecipient(MktCampaignDO mktCampaignDO);

    void sendShortMessage4CampaignStaff(MktCampaignDO mktCampaignDO, String sendContent);

    String sendShortMessage(String targPhone, String sendContent, String lanId) throws Exception;

}
