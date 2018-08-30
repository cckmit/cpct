/**
 * @(#)FilterRuleConfController.java, 2018/7/4.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *//*

package com.zjtelcom.cpct.controller.campaign;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.campaign.FilterRuleConf;
import com.zjtelcom.cpct.service.campaign.FilterRuleConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

*/
/**
 * Description: 过滤规则配置
 * author: linchao
 * date: 2018/07/04 20:34
 * version: V1.0
 *//*

@RequestMapping("${adminPath}/filterRuleConf")
@RestController
public class FilterRuleConfController extends BaseController {

    @Autowired
    private FilterRuleConfService filterRuleConfService;

    */
/**
     * 新增过滤规则配置
     *
     * @param filterRuleConf
     * @return
     *//*

    @RequestMapping(value = "/saveFilterRuleConf", method = RequestMethod.POST)
    @CrossOrigin
    public String saveFilterRuleConf(@RequestBody FilterRuleConf filterRuleConf) {
        Map<String, Object> map = new HashMap<>();
        try {
            map = filterRuleConfService.saveFilterRuleConf(filterRuleConf);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfController] failed to save filterRuleConf = {} Exception: ", JSON.toJSON(filterRuleConf), e);
        }
        return JSON.toJSONString(map);
    }

    */
/**
     * 修改过滤规则配置
     *
     * @param filterRuleConf
     * @return
     *//*

    @RequestMapping(value = "/updateFilterRuleConf", method = RequestMethod.POST)
    @CrossOrigin
    public String updateFilterRuleConf(@RequestBody FilterRuleConf filterRuleConf) {
        Map<String, Object> map = new HashMap<>();
        try {
            map = filterRuleConfService.updateFilterRuleConf(filterRuleConf);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfController] failed to update filterRuleConf = {} Exception: ", JSON.toJSON(filterRuleConf), e);
        }
        return JSON.toJSONString(map);
    }

    */
/**
     * 获取过滤规则配置
     *
     * @param params
     * @return
     *//*

    @RequestMapping(value = "/getFilterRuleConf", method = RequestMethod.POST)
    @CrossOrigin
    public String getFilterRuleConf(@RequestBody Map<String, String> params) {
        Map<String, Object> map = new HashMap<>();
        Long filterRuleConfId = 0L;
        try {
            if (params.get("filterRuleConfId") != null) {
                filterRuleConfId = Long.valueOf(params.get("filterRuleConfId"));
                map = filterRuleConfService.getFilterRuleConf(filterRuleConfId);
            }
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfController] failed to get filterRuleConf by filterRuleConfId = {} Exception: ", filterRuleConfId, e);
        }
        return JSON.toJSONString(map);
    }


    */
/**
     * 删除过滤规则配置
     *
     * @param params
     * @return
     *//*

    @RequestMapping(value = "/deleteFilterRuleConf", method = RequestMethod.POST)
    @CrossOrigin
    public String deleteFilterRuleConf(@RequestBody Map<String, String> params) {
        Map<String, Object> map = new HashMap<>();
        Long filterRuleConfId = Long.valueOf(params.get("filterRuleConfId"));
        try {
            map = filterRuleConfService.deleteFilterRuleConf(filterRuleConfId);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfController] failed to delete filterRuleConf by filterRuleConfId = {} Exception: ", filterRuleConfId, e);
        }
        return JSON.toJSONString(map);
    }

}*/
