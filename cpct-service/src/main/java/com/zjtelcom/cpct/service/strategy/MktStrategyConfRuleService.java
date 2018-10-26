package com.zjtelcom.cpct.service.strategy;

import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRule;

import java.util.List;
import java.util.Map;

public interface MktStrategyConfRuleService {

    Map<String, Object> saveMktStrategyConfRule(MktStrategyConfRule mktStrategyConfRule);

    Map<String, Object> updateMktStrategyConfRule(MktStrategyConfRule mktStrategyConfRule);

    Map<String, Object> getMktStrategyConfRule(Long mktStrategyConfRuleId);

    Map<String, Object> listAllMktStrategyConfRule();

    Map<String, Object> listAllMktStrategyConfRuleForName(Long mktStrategyConfId);

    Map<String, Object> deleteMktStrategyConfRule(Long mktStrategyConfRuleId);

    Map<String, Object> copyMktStrategyConfRule(Long parentMktStrategyConfRuleId, Boolean isPublish)throws Exception;

    Map<String, Object> copyMktStrategyConfRule(List<MktStrategyConfRule> mktStrategyConfRuleList) throws Exception;

    Map<String, Object> updateProductIds(List<Long> productIdList, Long ruleId);

}
