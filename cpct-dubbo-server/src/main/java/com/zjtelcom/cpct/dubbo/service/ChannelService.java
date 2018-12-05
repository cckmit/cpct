package com.zjtelcom.cpct.dubbo.service;

import com.zjtelcom.cpct.dubbo.model.FilterRuleInputReq;
import com.zjtelcom.cpct.dubbo.model.Ret;
import com.zjtelcom.cpct.dubbo.model.RetChannel;
import com.zjtelcom.cpct.dubbo.model.RetEvent;

public interface ChannelService {

    RetChannel getChannelDetail(String channelCode);

    RetEvent getEventDetail(String evtCode);

    Ret importRuleUserList(FilterRuleInputReq req);





}
