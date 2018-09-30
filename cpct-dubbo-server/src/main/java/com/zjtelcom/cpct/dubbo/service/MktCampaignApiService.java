
package com.zjtelcom.cpct.dubbo.service;

import com.zjtelcom.cpct.dubbo.model.QryMktCampaignListReq;
import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/07/17 11:11
 * version: V1.0
 */

public interface MktCampaignApiService {

  //  Map<String, Object> qryMktCampaignList(QryMktCampaignListReq qryMktCampaignListReq) throws Exception;

    Map<String, Object> qryMktCampaignDetail(Long mktCampaignId) throws Exception;


}
