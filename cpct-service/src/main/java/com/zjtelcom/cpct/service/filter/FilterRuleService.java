package com.zjtelcom.cpct.service.filter;

import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.request.filter.FilterRuleReq;
import java.util.Map;

/**
 * @Description 规律规则Service
 * @Author pengy
 * @Date 2018/6/21 9:45
 */
public interface FilterRuleService {

    Map<String,Object> qryFilterRule(FilterRuleReq filterRuleReq);

    Map<String,Object> delFilterRule(FilterRule filterRule);

    Map<String,Object> getFilterRule(FilterRule filterRule);

    Map<String,Object> createFilterRule(FilterRule filterRule);

    Map<String,Object> modFilterRule(FilterRule filterRule);

}
