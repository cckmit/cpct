package com.zjtelcom.cpct.service.channel;

import java.util.Map;

public interface MktCampaignPolicyService {

    Map<String,Object> getPolicyListByOfferList(Map<String,Object> param );

    Map<String,Object> getPolicyList(Map<String,Object> param);

    Map<String,Object> addCampaignPolicyRel(Map<String,Object> param);

    Map<String,Object> getPolicyListByCampaign(Map<String,Object> param);
}
