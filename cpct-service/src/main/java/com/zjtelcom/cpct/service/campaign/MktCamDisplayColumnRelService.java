package com.zjtelcom.cpct.service.campaign;

import java.util.Map;

public interface MktCamDisplayColumnRelService {

    Map<String,Object> findLabelListByDisplayId(Long mktCampaignId, Long displayId);

    // 复制活动标签关系
    Map<String,Object> copyDisplayLabelByCamId(Long oldCampaignId, Long newCampaignId);

    // 同步生产活动标签关系
    Map<String, Object> syncMktCamDisplayColumnRel(Long mktCampaignId);

    Map<String, Object> importOldCamDisplay();

}
