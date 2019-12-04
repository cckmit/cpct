package com.zjtelcom.cpct.service.dubbo;

import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;

import java.util.Map;

public interface UCCPService {

    void sendShortMessage4CampaignStaff(MktCampaignDO mktCampaignDO, String sendContent);

    String sendShortMessage(String targPhone, String sendContent, String lanId) throws Exception;

}
