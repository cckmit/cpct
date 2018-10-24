package com.zjtelcom.cpct.dubbo.service;

import com.zjtelcom.cpct.dubbo.model.FilterRuleInputReq;
import com.zjtelcom.cpct.dubbo.model.Ret;

import java.util.Map;

public interface ChannelService {

    Ret getChannelDetail(String channelCode);

    Ret getEventDetail(String evtCode);

    Ret importRuleUserList(FilterRuleInputReq req);





}
