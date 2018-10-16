/**
 * @(#)MktCamChlResultApiService.java, 2018/10/12.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.campaign;

import java.util.Map;

/**
 * @Description:
 * @author: linchao
 * @date: 2018/10/12 16:56
 * @version: V1.0
 */
public interface MktCamChlResultApiService {

    Map<String,Object> secondChannelSynergy(Map<String,Object> params);

}