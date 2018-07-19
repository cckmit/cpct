/**
 * @(#)MktCampaignService.java, 2018/7/17.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.dubbo.service;

import com.zjtelcom.cpct.dubbo.model.QryMktCampaignListReq;

import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/07/17 11:11
 * version: V1.0
 */
public interface MktCampaignService {

    Map<String, Object> qryMktCampaignList(QryMktCampaignListReq qryMktCampaignListReq);

//    Map<String, Object> qryMktCampaignDetail(QryMktCampaignListReq qryMktCampaignListReq);

}