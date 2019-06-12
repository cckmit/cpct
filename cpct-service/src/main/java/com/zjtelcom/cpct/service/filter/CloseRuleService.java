package com.zjtelcom.cpct.service.filter;

import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.filter.FilterRuleAddVO;
import com.zjtelcom.cpct.request.filter.FilterRuleReq;

import java.util.List;
import java.util.Map;

public interface CloseRuleService {
    Map<String,Object> getFilterRule(List<Integer> filterRuleIdList);

    Map<String,Object> qryFilterRule(FilterRuleReq filterRuleReq);

    Map<String,Object> qryFilterRules(FilterRuleReq filterRuleReq);

    Map<String,Object> delFilterRule(FilterRule filterRule);

    Map<String,Object> createFilterRule(FilterRuleAddVO filterRule);

    Map<String,Object> modFilterRule(FilterRuleAddVO filterRule);

    Map<String,Object> getFilterRule(Long ruleId);
}
