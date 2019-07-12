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

    Map<String, Object> copyMktStrategyConfRule(Long parentMktStrategyConfRuleId, Long childMktCampaignId, Boolean isPublish)throws Exception;

    Map<String, Object> copyMktStrategyConfRule(List<MktStrategyConfRule> mktStrategyConfRuleList) throws Exception;

    Map<String, Object> updateProductIds(List<Long> productIdList, Long ruleId);

    Map<String, Object> insertTarGrpBatch(List<Integer> ruleIdList, Long tarGrpNewId);

    Map<String, Object> updateTarGrpBatch(List<Integer> ruleIdList, Long tarGrpNewId);

    Map<String, Object> deleteTarGrpBatch(List<Integer> ruleIdList, Long tarGrpNewId);

    Map<String, Object> insertCamItemBatch(List<Integer> ruleIdList, List<Integer> camitemIdList);

    Map<String, Object> updateCamItemBatch(List<Integer> ruleIdList, List<Integer> camitemIdList);

    Map<String, Object> deleteCamItemBatch(List<Integer> ruleIdList, List<Integer> camitemIdList);

    Map<String, Object> getRuleTemplate(Long preStrategyConfId);

    Map<String, Object> test(Long ruleId ,List<Long> orgList);

    Map<String, Object> copyMktStrategyConfRuleForAdjust(Long parentMktStrategyConfRuleId, Long parentMktCampaignId , Long MktCampaignId, Map<Long, Long> itemMap) throws Exception;

}
