package com.zjtelcom.cpct.service.report;

import java.util.Map;

public interface ActivityStatisticsService {

    Map<String,Object> getStoreForUser(Map<String, Object> params);

    Map<String,Object> getStore(Map<String, Object> params);

    Map<String,Object> getChannel(Map<String, Object> params);
}
