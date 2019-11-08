package com.zjtelcom.cpct.service.report;

import java.util.Map;

public interface XinNewAactivityService {
    Map<String,Object> activityTheme(Map<String, Object> params);

    Map<String,Object> contactNumber(Map<String, Object> params);

    Map<String,Object> orderSuccessRate(Map<String, Object> params);

    Map<String,Object> incomePull(Map<String, Object> params);
}
