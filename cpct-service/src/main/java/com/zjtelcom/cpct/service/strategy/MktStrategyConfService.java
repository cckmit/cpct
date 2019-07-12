package com.zjtelcom.cpct.service.strategy;

import com.zjtelcom.cpct.dto.strategy.MktStrategyConfDetail;

import java.util.Map;

public interface MktStrategyConfService {

    Map<String, Object> saveMktStrategyConf(MktStrategyConfDetail mktStrategyConfDetail) throws Exception;

    Map<String, Object> updateMktStrategyConf(MktStrategyConfDetail mktStrategyConfDetail) throws Exception;

    Map<String, Object> getMktStrategyConf(Long mktStrategyConfId) throws Exception;

    Map<String, Object> listAllMktStrategyConf();

    Map<String, Object> deleteMktStrategyConf(Long mktStrategyConfId) throws Exception;

    Map<String, Object> copyMktStrategyConf(Long parentMktStrategyConfId, Long parentMktCampaignId, Long childMktCampaignId, Boolean isPublish, Long LanId)  throws Exception;

    Map<String, Object> copyMktStrategyConf(MktStrategyConfDetail mktStrategyConfDetail) throws Exception;

    Map<String, Object> getStrategyTemplate(Long preMktStrategyConfId) throws Exception;

    Map<String, Object> copyMktStrategyConfForAdjust(Long parentMktStrategyConfId, Long childMktCampaignId,  Long newMktCampaignId, Long LanId, Map<Long, Long> itemMap) throws Exception;

}
