/**
 * @(#)MktCamDirectoryService.java, 2018/9/12.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.campaign;

import java.util.Map;

/**
 * @Description:
 * @author: linchao
 * @date: 2018/09/12 10:16
 * @version: V1.0
 */
public interface MktCamDirectoryService {
    Map<String, Object> listAllDirectoryTree();
}