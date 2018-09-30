package com.zjtelcom.cpct.dubbo.service;

import java.util.Map;

public interface EventApiService {

    Map<String, Object> CalculateCPC(Map<String, Object> map);

    Map<String, Object> SecondChannelSynergy(Map<String, String> map);


}
