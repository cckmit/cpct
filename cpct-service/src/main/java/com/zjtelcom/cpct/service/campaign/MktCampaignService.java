/**
 * @(#)MktCampaignService.java, 2018/6/22.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.campaign;


import com.zjtelcom.cpct.dto.campaign.MktCampaign;
import com.zjtelcom.cpct.dto.campaign.MktCampaignDetail;
import com.zjtelcom.cpct.dto.campaign.MktCampaignVO;
import com.zjtelcom.cpct.request.campaign.QryMktCampaignListReq;

import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/06/22 22:21
 * version: V1.0
 */
public interface MktCampaignService {

/*    int createMktCampaign(MktCampaignDetail mktCampaignDetail) throws Exception;

    int modMktCampaign(MktCampaignDetail mktCampaignDetail) throws Exception;*/

    Map<String,Object> qryMktCampaignListPage (String mktCampaignName, String statusCd, String tiggerType, String mktCampaignType, Integer page, Integer pageSize);

    Map<String,Object> createMktCampaign(MktCampaignVO mktCampaignVO) throws Exception;

    Map<String,Object> delMktCampaign(Long mktCampaignId) throws Exception;

    Map<String,Object> getMktCampaign(Long mktCampaignId) throws Exception;

    Map<String,Object> modMktCampaign(MktCampaignVO mktCampaignVO) throws Exception;

    Map<String,Object> changeMktCampaignStatus(Long mktCampaignId, String statusCd) throws Exception;

//    Map<String,Object> qryMktCampaignList (MktCampaignVO mktCampaignVO);

}