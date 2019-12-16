package com.zjtelcom.cpct.statistic.service;

import java.util.Map;

public interface TrialLabelService {

    Map<String, Object> trialUerLabelLog(String s, String messageID, String key);

    Map<String,Object> statisticalAnalysts(Map<String, Object> params);
}
