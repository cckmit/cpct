package com.zjtelcom.cpct.dubbo.service;

import java.util.Map;

public interface EventApiService {

    Map<String, Object> deal(Map<String, Object> map) throws Exception;

    Map<String, Object> CalculateCPC(Map<String, Object> map) throws Exception;

    void cpc();

}
