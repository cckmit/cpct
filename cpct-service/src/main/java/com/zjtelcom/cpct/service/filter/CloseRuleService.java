package com.zjtelcom.cpct.service.filter;

import com.zjtelcom.cpct.dto.filter.CloseRule;
import com.zjtelcom.cpct.dto.filter.CloseRuleAddVO;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.filter.FilterRuleAddVO;
import com.zjtelcom.cpct.request.filter.CloseRuleReq;
import com.zjtelcom.cpct.request.filter.FilterRuleReq;

import java.util.List;
import java.util.Map;

public interface CloseRuleService {
    Map<String,Object> getFilterRule(List<Integer> closeRuleIdList);

    Map<String,Object> qryFilterRule(CloseRuleReq closeRuleReq);

    Map<String,Object> qryFilterRules(CloseRuleReq closeRuleReq);

    Map<String,Object> delFilterRule(CloseRule closeRule);

    Map<String,Object> createFilterRule(CloseRuleAddVO closeRule);

    Map<String,Object> modFilterRule(CloseRuleAddVO closeRule);

    Map<String,Object> getFilterRule(Long ruleId);
}
