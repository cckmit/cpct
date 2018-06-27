package com.zjtelcom.cpct.service.strategy;

import com.zjtelcom.cpct.dto.campaign.MktStrategyConf;
import com.zjtelcom.cpct.dto.campaign.MktStrategyConfDetail;

import java.util.Map;

public interface MktStrategyConfService {

    public Map<String, Object> saveMktStrategyConf(MktStrategyConfDetail mktStrategyConfDetail);

    public Map<String, Object> updateMktStrategyConf(MktStrategyConfDetail mktStrategyConfDetail);

    public Map<String, Object> getMktStrategyConf(Long mktStrategyConfId);

    public Map<String, Object> listAllMktStrategyConf();

    public Map<String, Object> deleteMktStrategyConf(Long mktStrategyConfId);

}
