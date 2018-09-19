package com.zjtelcom.cpct.service.campaign;

import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/07/17 11:11
 * version: V1.0
 */

public interface MktCampaignApiService {

    Map<String, Object> qryMktCampaignDetail(Long mktCampaignId) throws Exception;

}
