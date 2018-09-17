package com.zjtelcom.cpct.service.impl.synchronize.filter;

import com.zjtelcom.cpct.service.synchronize.filter.SynFilterRuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/17
 * @Description:
 */
@Service
@Transactional
public class SynFilterRuleServiceImpl implements SynFilterRuleService {
    @Override
    public Map<String, Object> synchronizeSingleFilterRule(Long ruleId, String roleName) {
        return null;
    }

    @Override
    public Map<String, Object> synchronizeBatchFilterRule(String roleName) {
        return null;
    }
}
