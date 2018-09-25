package com.zjtelcom.cpct.dubbo.service;

import com.zjtelcom.cpct.dubbo.model.FilterRuleInputReq;

import java.util.Map;

public interface ChannelService {

    Map<String,Object> getChannelDetail(String channelCode);

    Map<String,Object> getEventDetail(String evtCode);

    Map<String,Object> importRuleUserList(FilterRuleInputReq req);





}
