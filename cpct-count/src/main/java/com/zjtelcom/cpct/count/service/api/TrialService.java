package com.zjtelcom.cpct.count.service.api;

import com.zjtelcom.es.es.entity.model.TrialResponseES;

import java.util.Map;

public interface TrialService  {

    TrialResponseES  trialTarGrp(Map<String,Object> tarMap);
}
