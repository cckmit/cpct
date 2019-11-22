package com.zjtelcom.cpct.service.event;

import java.util.Map;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/11/14 14:15
 * @version: V1.0
 */
public interface EventInstService {

    Map<String, Object> queryEventInst(Map<String, String> paramsMap);

    Map<String, Object> queryEventInstLog(Map<String, String> paramsMap);

}