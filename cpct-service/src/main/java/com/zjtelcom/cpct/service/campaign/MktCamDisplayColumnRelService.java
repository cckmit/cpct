package com.zjtelcom.cpct.service.campaign;

import java.util.Map;

public interface MktCamDisplayColumnRelService {

    Map<String,Object> findLabelListByDisplayId(Long mktCampaignId, Long displayId);

    // 复制活动标签关系
    Map<String,Object> copyDisplayLabelByCamId(Long oldCampaignId, Long newCampaignId);


    Map<String, Object> importOldCamDisplay();

    Map<String, Object> updateOldCamDisplay();

}
