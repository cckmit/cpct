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
import org.springframework.web.bind.annotation.*;

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


    /**
     * 删除策略配置规则
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/deleteMktStrategyConfRule", method = RequestMethod.POST)
    @CrossOrigin
    public String deleteMktStrategyConfRule(@RequestBody  Map<String, String> param){
        Long mktStrategyConfRuleId =  Long.valueOf(param.get("mktStrategyConfRuleId"));
        Map<String, Object> map = mktStrategyConfRuleService.deleteMktStrategyConfRule(mktStrategyConfRuleId);
        return JSON.toJSONString(map);
    }

    /**
     * 获取策略规则信息
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/getMktStrategyConfRule", method = RequestMethod.POST)
    @CrossOrigin
    public String getMktStrategyConfRule(@RequestBody  Map<String, String> param){
        Long mktStrategyConfRuleId =  Long.valueOf(param.get("mktStrategyConfRuleId"));
        Map<String, Object> map = mktStrategyConfRuleService.getMktStrategyConfRule(mktStrategyConfRuleId);
        return JSON.toJSONString(map);
    }

    /**
     * 通过策略Id 查询对应的规则列表（id+名称）
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/listAllMktStrategyConfRuleForName", method = RequestMethod.POST)
    @CrossOrigin
    public String listAllMktStrategyConfRuleForName(@RequestBody  Map<String, String> param){
        Long mktStrategyConfId = Long.valueOf(param.get("mktStrategyConfId"));
        Map<String, Object> map = mktStrategyConfRuleService.listAllMktStrategyConfRuleForName(mktStrategyConfId);
        return JSON.toJSONString(map);
    }


}