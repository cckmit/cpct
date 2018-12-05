/**
 * @(#)MktCampaignService.java, 2018/6/22.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.campaign;


import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/06/22 22:21
 * version: V1.0
 */
public interface MktCampaignRelService {

    Map<String, Object> getMktCampaignRel(Long mktCampaignId);

}