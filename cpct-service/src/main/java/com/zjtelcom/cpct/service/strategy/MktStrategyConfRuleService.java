package com.zjtelcom.cpct.service.strategy;

import com.zjtelcom.cpct.dto.strategy.MktStrategyConfDetail;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRule;

import java.util.Map;

public interface MktStrategyConfRuleService {

    public Map<String, Object> saveMktStrategyConfRule(MktStrategyConfRule mktStrategyConfRule);

    public Map<String, Object> updateMktStrategyConfRule(MktStrategyConfRule mktStrategyConfRule);

    public Map<String, Object> getMktStrategyConfRule(Long mktStrategyConfRuleId);

    public Map<String, Object> listAllMktStrategyConfRule();

    public Map<String, Object> deleteMktStrategyConfRule(Long mktStrategyConfRuleId);

}
