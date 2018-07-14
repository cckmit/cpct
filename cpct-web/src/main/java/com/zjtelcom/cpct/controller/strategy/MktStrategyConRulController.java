/**
 * @(#)MktStrategyConRulController.java, 2018/7/13.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.controller.strategy;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.*;
import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/07/13 16:06
 * version: V1.0
 */
@RestController
@RequestMapping("${adminPath}/mktStrategyConfRule")
public class MktStrategyConRulController {

    @Autowired
    private MktStrategyConfRuleService mktStrategyConfRuleService;


    @RequestMapping(value = "/deleteMktStrategyConfRule", method = RequestMethod.POST)
    public String deleteMktStrategyConfRule(Long mktStrategyConfRuleId){
        Map<String, Object> map = mktStrategyConfRuleService.deleteMktStrategyConfRule(mktStrategyConfRuleId);
        return JSON.toJSONString(map);
    }
}