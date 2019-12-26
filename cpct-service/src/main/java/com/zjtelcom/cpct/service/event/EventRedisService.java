package com.zjtelcom.cpct.service.event;

import java.util.Map;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/12/10 11:09
 * @version: V1.0
 */
public interface EventRedisService {

    Map<String, Object> getRedis(String key);

    Map<String, Object> getRedis(String key, Long id);

    Map<String, Object> getRedis(String key, Map<String, Object> params);

    Map<String, Object> getRedis(String key, Long id, Map<String, Object> params);

    Map<String, Object> delRedisByEventCode(String eventCode);

    Map<String, Object> delRedisByKey(String key);

    Map<String, Object> getRedisByKey(String key);
}