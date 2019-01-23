/**
 * @(#)SynchronizeCampaignService.java, 2018/9/17.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.synchronize.campaign;

import java.util.Map;

/**
 * @Description:
 * @author: linchao
 * @date: 2018/09/17 14:27
 * @version: V1.0
 */
public interface SynchronizeCampaignService {

    //Map<String, Object> synchronizeCampaign(Long mktCampaignId, String roleName) throws Exception;
    Map<String, Object> synchronizeCampaign(Long mktCampaignId, String roleName) throws Exception;

    Map<String, Object> updateCampaignRedis(Long mktCampaignId) throws Exception;

    Map<String, Object> deleteCampaignRedisProd(Long mktCampaignId);

    Map<String, Object> deleteCampaignRedisPre(Long mktCampaignId);

}