package com.zjtelcom.cpct.service.synchronize.campaign;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/19
 * @Description:
 */
public interface SynMktCampaignRelService {


    Map<String,Object> synchronizeSingleCampaignRel(Long campaignRelId, String roleName);

    Map<String,Object> synchronizeBatchCampaignRel(String roleName);
}
