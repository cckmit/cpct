package com.zjtelcom.cpct.open.service.dubbo;

import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;

public interface UCCPService {

    void sendShortMessage4GroupCampaignRecipient(MktCampaignDO mktCampaignDO);

    void sendShortMessage4CampaignStaff(MktCampaignDO mktCampaignDO, String sendContent);

    String sendShortMessage(String targPhone, String sendContent, String lanId) throws Exception;

}
