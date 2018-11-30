/**
 * @(#)MktStrategyConRulController.java, 2018/7/13.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.controller.strategy;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRule;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public String deleteMktStrategyConfRule(@RequestBody Map<String, String> param) {
        Long mktStrategyConfRuleId = Long.valueOf(param.get("mktStrategyConfRuleId"));
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
    public String getMktStrategyConfRule(@RequestBody Map<String, String> param) {
        Map<String, Object> map = new HashMap<>();
        if(param.get("mktStrategyConfRuleId")!=null && !"null".equals(param.get("mktStrategyConfRuleId"))){
            Long mktStrategyConfRuleId = Long.valueOf(param.get("mktStrategyConfRuleId"));
            map = mktStrategyConfRuleService.getMktStrategyConfRule(mktStrategyConfRuleId);
        }
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
    public String listAllMktStrategyConfRuleForName(@RequestBody Map<String, String> param) {
        Map<String, Object> map = new HashMap<>();
        if (param.get("mktStrategyConfId") != null && !"null".equals(param.get("mktStrategyConfId"))) {
            Long mktStrategyConfId = Long.valueOf(param.get("mktStrategyConfId"));
            map = mktStrategyConfRuleService.listAllMktStrategyConfRuleForName(mktStrategyConfId);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 通过规则内容复制策略规则
     *
     * @param mktStrategyConfRule
     * @return
     */
    @RequestMapping(value = "/copyMktStrategyConfRule", method = RequestMethod.POST)
    @CrossOrigin
    public String copyMktStrategyConfRule(@RequestBody MktStrategyConfRule mktStrategyConfRule) throws Exception {
        List<MktStrategyConfRule> mktStrategyConfRuleList = new ArrayList<>();
        mktStrategyConfRuleList.add(mktStrategyConfRule);
        Map<String, Object> ruleListMap = mktStrategyConfRuleService.copyMktStrategyConfRule(mktStrategyConfRuleList);
        List<MktStrategyConfRule> ruleList = (List<MktStrategyConfRule>) ruleListMap.get("mktStrategyConfRuleList");
        Map<String, Object> ruleMap = new HashMap<>();
        ruleMap.put("mktStrategyConfRule", ruleList.get(0));
        return JSON.toJSONString(ruleMap);
    }

    /**
     * 批量插入客户分群
     * @param params
     * @return
     */
    @RequestMapping(value = "/insertTarGrpBatch", method = RequestMethod.POST)
    @CrossOrigin
    public String insertTarGrpBatch(@RequestBody Map<String, Object> params){
        List<Integer> ruleIdList = (List<Integer>) params.get("ruleIdList");
        Integer tarGrpNewId = (Integer) params.get("tarGrpId");
        // 查询新的客户分群
        Map<String, Object> map = mktStrategyConfRuleService.insertTarGrpBatch(ruleIdList, tarGrpNewId.longValue());
        return JSON.toJSONString(map);
    }


    /**
     * 批量修改客户分群
     * @param params
     * @return
     */
    @RequestMapping(value = "/updateTarGrpBatch", method = RequestMethod.POST)
    @CrossOrigin
    public String updateTarGrpBatch(@RequestBody Map<String, Object> params){
        List<Integer> ruleIdList = (List<Integer>) params.get("ruleIdList");
        Integer tarGrpNewId = (Integer) params.get("tarGrpId");
        // 查询新的客户分群
        Map<String, Object> map = mktStrategyConfRuleService.updateTarGrpBatch(ruleIdList, tarGrpNewId.longValue());
        return JSON.toJSONString(map);
    }

    /**
     * 批量删除客户分群
     * @param params
     * @return
     */
    @RequestMapping(value = "/deleteTarGrpBatch", method = RequestMethod.POST)
    @CrossOrigin
    public String deleteTarGrpBatch(@RequestBody Map<String, Object> params){
        List<Integer> ruleIdList = (List<Integer>) params.get("ruleIdList");
        Integer tarGrpNewId = (Integer) params.get("tarGrpId");
        // 查询新的客户分群
        Map<String, Object> map = mktStrategyConfRuleService.deleteTarGrpBatch(ruleIdList, tarGrpNewId.longValue());
        return JSON.toJSONString(map);
    }




    /**
     * 批量插入客户分群
     * @param params
     * @return
     */
    @RequestMapping(value = "/insertCamItemBatch", method = RequestMethod.POST)
    @CrossOrigin
    public String insertCamItemBatch(@RequestBody Map<String, Object> params){
        List<Integer> ruleIdList = (List<Integer>) params.get("ruleIdList");
        List<Integer> camitemIdList = (List<Integer>) params.get("camitemIdList");
        // 查询新的客户分群
        Map<String, Object> map = mktStrategyConfRuleService.insertCamItemBatch(ruleIdList, camitemIdList);
        return JSON.toJSONString(map);
    }


    /**
     * 批量修改客户分群
     * @param params
     * @return
     */
    @RequestMapping(value = "/updateCamItemBatch", method = RequestMethod.POST)
    @CrossOrigin
    public String updateCamItemBatch(@RequestBody Map<String, Object> params){
        List<Integer> ruleIdList = (List<Integer>) params.get("ruleIdList");
        List<Integer> camitemIdList = (List<Integer>) params.get("camitemIdList");
        // 查询新的客户分群
        Map<String, Object> map = mktStrategyConfRuleService.updateCamItemBatch(ruleIdList, camitemIdList);
        return JSON.toJSONString(map);
    }

    /**
     * 批量删除客户分群
     * @param params
     * @return
     */
    @RequestMapping(value = "/deleteCamItemBatch", method = RequestMethod.POST)
    @CrossOrigin
    public String deleteCamItemBatch(@RequestBody Map<String, Object> params){
        List<Integer> ruleIdList = (List<Integer>) params.get("ruleIdList");
        List<Integer> camitemIdList = (List<Integer>) params.get("camitemIdList");
        // 查询新的客户分群
        Map<String, Object> map = mktStrategyConfRuleService.deleteCamItemBatch(ruleIdList, camitemIdList);
        return JSON.toJSONString(map);
    }
}
