package com.zjtelcom.cpct.service.api;

import java.util.Map;

public interface EventApiService {

    Map<String, Object> CalculateCPC(Map<String, Object> map);

    Map<String, Object> secondChannelSynergy(Map<String, Object> map);

    /**
     * 首次协同cpc同步
     * @param map
     * @return
     */
    Map<String, Object> CalculateCPCSync(Map<String, Object> map);



}
