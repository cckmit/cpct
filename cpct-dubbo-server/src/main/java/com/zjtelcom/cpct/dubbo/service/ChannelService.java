package com.zjtelcom.cpct.dubbo.service;

import java.util.Map;

public interface ChannelService {

    Map<String,Object> getChannelDetail(String channelCode);

    Map<String,Object> getEventDetail(String evtCode);

}
