package com.zjtelcom.cpct.open.service.completeMktCampaign;

import java.util.Map;

public interface OpenCompleteMktCampaignService {

    Map<String, Object> completeMktCampaign(Long mktCampaignId, String tacheCd);
}
