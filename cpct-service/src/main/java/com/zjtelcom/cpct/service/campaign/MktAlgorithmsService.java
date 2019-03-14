package com.zjtelcom.cpct.service.campaign;

import com.zjtelcom.cpct.domain.campaign.MktAlgorithms;

import java.util.Map;

public interface MktAlgorithmsService {

    Map<String, Object> getMktAlgorithms(Long userId, Long algoId);

    Map<String, Object> saveMktAlgorithms(Long userId, MktAlgorithms addVO);

    Map<String, Object> updateMktAlgorithms(Long userId, MktAlgorithms editVO);

    Map<String, Object> deleteMktAlgorithms(Long userId, MktAlgorithms delVO);

    Map<String, Object> listMktAlgorithms(Long userId, Map<String, Object> params);

}
