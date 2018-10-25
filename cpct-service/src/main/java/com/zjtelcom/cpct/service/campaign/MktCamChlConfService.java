/**
 * @(#)MktCamChlConfAttrService.java, 2018/7/2.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.campaign;

import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;

import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/07/02 14:07
 * version: V1.0
 */
public interface MktCamChlConfService {

    Map<String,Object> saveMktCamChlConf(MktCamChlConfDetail mktCamChlConfDetail);

    Map<String,Object> updateMktCamChlConf(MktCamChlConfDetail mktCamChlConfDetail);

    Map<String,Object> getMktCamChlConf(Long evtContactConfId);

    Map<String,Object> listMktCamChlConf();

    Map<String,Object> deleteMktCamChlConf(Long evtContactConfId);

    Map<String, Object> copyMktCamChlConf(Long parentEvtContactConfId) throws Exception;

    Map<String, Object> copyMktCamChlConfFormRedis(Long parentEvtContactConfId, String scriptDesc) throws Exception;
}