/**
 * @(#)MktCamDirectoryController.java, 2018/9/12.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.controller.campaign;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.service.campaign.MktCamDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Description:
 * @author: linchao
 * @date: 2018/09/12 11:30
 * @version: V1.0
 */
@RestController
@RequestMapping("${adminPath}/mktCamDirectory")
public class MktCamDirectoryController {

    @Autowired
    private MktCamDirectoryService mktCamDirectoryService;

    @RequestMapping(value = "/listAllDirectoryTree", method = RequestMethod.POST)
    @CrossOrigin
    public String listAllDirectoryTree() throws Exception {
        Map<String, Object> directoryMap = mktCamDirectoryService.listAllDirectoryTree();
        return JSON.toJSONString(directoryMap);
    }
}