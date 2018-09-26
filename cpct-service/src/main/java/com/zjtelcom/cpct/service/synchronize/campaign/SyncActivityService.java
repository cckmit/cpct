/**
 * @(#)SyncActivityService.java, 2018/9/25.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.synchronize.campaign;

import com.zjhcsoft.eagle.main.dubbo.model.policy.ResponseHeaderModel;

import java.util.Map;

/**
 * @Description:
 * @author: linchao
 * @date: 2018/09/25 15:50
 * @version: V1.0
 */
public interface SyncActivityService {

    ResponseHeaderModel SyncActivity(Long mktCampaignId, String roleName);
}