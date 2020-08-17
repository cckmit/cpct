package com.zjtelcom.cpct.dubbo.service;

import com.zjtelcom.cpct.dubbo.model.*;

import java.util.Map;

public interface ChannelService {

    RetChannel getChannelDetail(String channelCode);

    RetEvent getEventDetail(String evtCode);

    Ret importRuleUserList(FilterRuleInputReq req);

   Map<String,Object> getUamServicePswd( String accountID, String areaCode, String custID);


}
