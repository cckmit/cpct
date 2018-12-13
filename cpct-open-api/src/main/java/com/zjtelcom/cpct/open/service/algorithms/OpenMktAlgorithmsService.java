package com.zjtelcom.cpct.open.service.algorithms;

import com.zjtelcom.cpct.open.entity.mktAlgorithms.OpenMktAlgorithms;

import java.util.Map;

public interface OpenMktAlgorithmsService {

    Map<String, Object> getMktAlgorithms(String id);

    Map<String, Object> saveMktAlgorithms(OpenMktAlgorithms openMktAlgorithms);

    Map<String, Object> updateMktAlgorithms(String id, String params);

    Map<String, Object> deleteMktAlgorithms(String id);

    Map<String, Object> listMktAlgorithms(Map<String, Object> params);
}
