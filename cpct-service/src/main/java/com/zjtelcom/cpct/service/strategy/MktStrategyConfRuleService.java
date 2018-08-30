package com.zjtelcom.cpct.service.strategy;

import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRule;

import java.util.Map;

public interface MktStrategyConfRuleService {

    Map<String, Object> saveMktStrategyConfRule(MktStrategyConfRule mktStrategyConfRule);

    Map<String, Object> updateMktStrategyConfRule(MktStrategyConfRule mktStrategyConfRule);

    Map<String, Object> getMktStrategyConfRule(Long mktStrategyConfRuleId);

    Map<String, Object> listAllMktStrategyConfRule();

    Map<String, Object> listAllMktStrategyConfRuleForName(Long mktStrategyConfId);

    Map<String, Object> deleteMktStrategyConfRule(Long mktStrategyConfRuleId);

    Map<String, Object> copyMktStrategyConfRule(Long parentMktStrategyConfRuleId);

}
