package com.zjtelcom.cpct.dubbo.service;

import java.util.Map;

public interface EventApiService {

    Map<String, Object> deal(Map<String, Object> map);

    Map<String, Object> CalculateCPC(Map<String, String> map);

    Map<String, Object> SecondChannelSynergy(Map<String, String> map);


}
