package com.zjtelcom.cpct.service.campaign;

import java.util.Map;

public interface MktCamDisplayColumnRelService {

    Map<String,Object> findLabelListByDisplayId(Long mktCampaignId, Long displayId);
}
