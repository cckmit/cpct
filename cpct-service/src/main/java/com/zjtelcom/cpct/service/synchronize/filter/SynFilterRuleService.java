package com.zjtelcom.cpct.service.synchronize.filter;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/17
 * @Description:
 */
public interface SynFilterRuleService {

    Map<String,Object> synchronizeSingleFilterRule(Long ruleId, String roleName);

    Map<String,Object> synchronizeBatchFilterRule(String roleName);

    Map<String,Object> deleteSingleFilterRule(Long ruleId, String roleName);
}
