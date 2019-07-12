/**
 * @(#)MktCamChlResultService.java, 2018/7/27.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.campaign;

import com.zjtelcom.cpct.dto.campaign.MktCamChlResult;

import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/07/27 14:22
 * version: V1.0
 */
public interface MktCamChlResultService {

    Map<String,Object> saveMktCamChlResult(MktCamChlResult mktCamChlResult);

    Map<String,Object> getMktCamChlResult(Long mktCamChlResultId);

    Map<String,Object> updateMktCamChlResult(MktCamChlResult mktCamChlResult);

    Map<String,Object> deleteMktCamChlResult(Long mktCamChlResultId);

    Map<String,Object> selectAllMktCamChlResult();

    Map<String, Object> copyMktCamChlResult(Long parentMktCamChlResultId);

    Map<String, Object> copyMktCamChlResultForAdjust(Long parentMktCamChlResultId, Long childMktCampaignId);

    Map<String, Object> copyMktCamChlResultFromRedis(MktCamChlResult mktCamChlResult);

    Map<String, Object> selectResultList();

}