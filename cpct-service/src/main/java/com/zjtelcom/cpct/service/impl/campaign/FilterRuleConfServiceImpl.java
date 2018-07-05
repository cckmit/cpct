/**
 * @(#)FilterRuleConfServiceImpl.java, 2018/7/3.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.campaign;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.FilterRuleConfMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.domain.campaign.FilterRuleConfDO;
import com.zjtelcom.cpct.dto.campaign.FilterRuleConf;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.FilterRuleConfService;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description:
 * author: linchao
 * date: 2018/07/03 17:05
 * version: V1.0
 */
@Transactional
@Service
public class FilterRuleConfServiceImpl extends BaseService implements FilterRuleConfService {

    @Autowired
    private FilterRuleConfMapper filterRuleConfMapper;

    @Autowired
    private FilterRuleMapper filterRuleMapper;

    @Override
    public Map<String, Object> saveFilterRuleConf(FilterRuleConf filterRuleConf) {
        Map<String, Object> filterRuleConfMap = new HashMap<>();
        try {
            FilterRuleConfDO filterRuleConfDO = new FilterRuleConfDO();
            CopyPropertiesUtil.copyBean2Bean(filterRuleConfDO, filterRuleConf);
            String filterRuleIds = "";
            for (int i = 0; i < filterRuleConf.getFilterRuleList().size(); i++) {
                if (i == 0) {
                    filterRuleIds = filterRuleIds + filterRuleConf.getFilterRuleList().get(i).getRuleId();
                } else {
                    filterRuleIds += "," + filterRuleConf.getFilterRuleList().get(i).getRuleId();
                }
            }
            filterRuleConfDO.setFilterRuleIds(filterRuleIds);
            filterRuleConfDO.setCreateStaff(UserUtil.loginId());
            filterRuleConfDO.setCreateDate(new Date());
            filterRuleConfDO.setUpdateStaff(UserUtil.loginId());
            filterRuleConfDO.setUpdateDate(new Date());
            filterRuleConfMapper.insert(filterRuleConfDO);
            Long filterRuleConfId = filterRuleConfDO.getFilterRuleConfId();
            filterRuleConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            filterRuleConfMap.put("resultMsg", ErrorCode.SAVE_FILTER_RULE_CONF_SUCCESS.getErrorMsg());
            filterRuleConfMap.put("filterRuleConfId", filterRuleConfId);
        } catch (Exception e) {
            logger.error("[op:FilterRuleConfServiceImpl] failed to save FilterRuleConf = {}", filterRuleConf);
            filterRuleConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            filterRuleConfMap.put("resultMsg", ErrorCode.SAVE_FILTER_RULE_CONF_FAILURE.getErrorMsg());
        }
        return filterRuleConfMap;
    }

    @Override
    public Map<String, Object> updateFilterRuleConf(FilterRuleConf filterRuleConf) {
        Map<String, Object> filterRuleConfMap = new HashMap<>();
        try {
            FilterRuleConfDO filterRuleConfDO = new FilterRuleConfDO();
            String filterRuleIds = "";
            CopyPropertiesUtil.copyBean2Bean(filterRuleConfDO, filterRuleConf);
            for (int i = 0; i < filterRuleConf.getFilterRuleList().size(); i++) {
                if (i == 0) {
                    filterRuleIds = filterRuleIds + filterRuleConf.getFilterRuleList().get(i).getRuleId();
                } else {
                    filterRuleIds += "," + filterRuleConf.getFilterRuleList().get(i).getRuleId();
                }
            }
            filterRuleConfDO.setFilterRuleIds(filterRuleIds);
            filterRuleConfDO.setUpdateStaff(UserUtil.loginId());
            filterRuleConfDO.setUpdateDate(new Date());
            filterRuleConfMapper.updateByPrimaryKey(filterRuleConfDO);
            Long filterRuleConfId = filterRuleConfDO.getFilterRuleConfId();
            filterRuleConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            filterRuleConfMap.put("resultMsg", ErrorCode.UPDATE_FILTER_RULE_CONF_SUCCESS.getErrorMsg());
            filterRuleConfMap.put("filterRuleConfId", filterRuleConfId);
        } catch (Exception e) {
            logger.error("[op:FilterRuleConfServiceImpl] failed to update FilterRuleConf = {}", JSON.toJSON(filterRuleConf));
            filterRuleConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            filterRuleConfMap.put("resultMsg", ErrorCode.UPDATE_FILTER_RULE_CONF_FAILURE.getErrorMsg());
        }
        return filterRuleConfMap;
    }

    @Override
    public Map<String, Object> getFilterRuleConf(Long filterRuleConfId) {
        Map<String, Object> filterRuleConfMap = new HashMap<>();
        FilterRuleConf filterRuleConf = new FilterRuleConf();
        try {
            FilterRuleConfDO filterRuleConfDO = filterRuleConfMapper.selectByPrimaryKey(filterRuleConfId);
            String filterRuleIds = filterRuleConfDO.getFilterRuleIds();
            String[] filterRuleIdArr = filterRuleIds.split(",");
            List<FilterRule> filterRuleList = new ArrayList<>();
            for (int i = 0; i < filterRuleIdArr.length; i++) {
                Long filterRuleId = Long.valueOf(filterRuleIdArr[i]);
                //TODO 根据filterRuleId去FILTER_RULE表查询对应的规则
                FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(filterRuleId);
                filterRuleList.add(filterRule);
            }
            filterRuleConf.setFilterRuleConfId(filterRuleConfId);
            filterRuleConf.setFilterRuleList(filterRuleList);

            filterRuleConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            filterRuleConfMap.put("resultMsg", ErrorCode.GET_FILTER_RULE_CONF_SUCCESS.getErrorMsg());
            filterRuleConfMap.put("filterRuleConf", filterRuleConf);
        } catch (Exception e) {
            logger.error("[op:FilterRuleConfServiceImpl] failed to get filterRuleConf = {} by filterRuleConfId = {}", JSON.toJSON(filterRuleConf), filterRuleConfId);
            filterRuleConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            filterRuleConfMap.put("resultMsg", ErrorCode.GET_FILTER_RULE_CONF_FAILURE.getErrorMsg());
        }
        return filterRuleConfMap;
    }

    @Override
    public Map<String, Object> deleteFilterRuleConf(Long filterRuleConfId) {
        Map<String, Object> filterRuleConfMap = new HashMap<>();
        try {
            filterRuleConfMapper.deleteByPrimaryKey(filterRuleConfId);
            filterRuleConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            filterRuleConfMap.put("resultMsg", ErrorCode.DELETE_FILTER_RULE_CONF_SUCCESS.getErrorMsg());
        } catch (Exception e) {
            logger.error("[op:FilterRuleConfServiceImpl] failed to delete filterRuleConf by filterRuleConfId = {}", filterRuleConfId);
            filterRuleConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            filterRuleConfMap.put("resultMsg", ErrorCode.DELETE_FILTER_RULE_CONF_FAILURE.getErrorMsg());
        }
        return filterRuleConfMap;
    }
}