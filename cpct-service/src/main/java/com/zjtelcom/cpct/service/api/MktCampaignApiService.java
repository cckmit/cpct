/**
 * @(#)MktCampaignService.java, 2018/7/17.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.api;

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