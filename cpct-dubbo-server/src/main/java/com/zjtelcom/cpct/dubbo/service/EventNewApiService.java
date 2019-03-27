package com.zjtelcom.cpct.dubbo.service;

import java.util.Map;

public interface EventNewApiService {

    Map<String, Object> CalculateCPC(Map<String, Object> map);

    Map<String, Object> secondChannelSynergy(Map<String, Object> map);

    /**
     * 首次协同cpc同步
     * @param map
     * @return
     */
    Map<String, Object> CalculateCPCSync(Map<String, Object> map);

}
