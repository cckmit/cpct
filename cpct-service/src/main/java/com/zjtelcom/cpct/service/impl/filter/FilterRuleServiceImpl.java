package com.zjtelcom.cpct.service.impl.filter;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.request.filter.FilterRuleReq;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.filter.FilterRuleService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 规律规则实现类
 * @Author pengy
 * @Date 2018/6/21 9:46
 */
@Service
@Transactional
public class FilterRuleServiceImpl extends BaseService implements FilterRuleService {

    @Autowired
    private FilterRuleMapper filterRuleMapper;

    @Override
    public Map<String, Object> qryFilterRule(FilterRuleReq filterRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        Page pageInfo = filterRuleReq.getPageInfo();
        PageHelper.startPage(pageInfo.getPage(), pageInfo.getPageSize());
        List<FilterRule> filterRules = filterRuleMapper.qryFilterRule(filterRuleReq.getFilterRule());
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("filterRules", filterRules);
        maps.put("pageInfo", new Page(new PageInfo(filterRules)));
        return maps;
    }

    /**
     * 删除过滤规则
     */
    @Override
    public Map<String, Object> delFilterRule(FilterRule filterRule) {
        Map<String, Object> maps = new HashMap<>();
        filterRuleMapper.delFilterRule(filterRule);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 查询单个过滤规则
     */
    @Override
    public Map<String, Object> getFilterRule(FilterRule filterRule) {
        Map<String, Object> map = new HashMap<>();
        FilterRule filterRuleT = filterRuleMapper.getFilterRule(filterRule);
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("filterRule", filterRuleT);
        return map;
    }

    /**
     * 新建过滤规则
     */
    @Override
    public Map<String, Object> createFilterRule(FilterRule filterRule) {
        Map<String, Object> maps = new HashMap<>();
        filterRule.setCreateDate(DateUtil.getCurrentTime());
        filterRule.setUpdateDate(DateUtil.getCurrentTime());
        filterRule.setStatusDate(DateUtil.getCurrentTime());
        filterRule.setUpdateStaff(UserUtil.loginId());
        filterRule.setCreateStaff(UserUtil.loginId());
        filterRule.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        filterRuleMapper.createFilterRule(filterRule);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("filterRule", filterRule);
        return maps;
    }

    /**
     * 修改过滤规则
     */
    @Override
    public Map<String, Object> modFilterRule(FilterRule filterRule) {
        Map<String, Object> maps = new HashMap<>();
        filterRule.setUpdateDate(DateUtil.getCurrentTime());
        filterRule.setUpdateStaff(UserUtil.loginId());
        filterRuleMapper.modFilterRule(filterRule);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("filterRule", filterRule);
        return maps;
    }

}
