package com.zjtelcom.cpct.dubbo.service;

import com.zjtelcom.cpct.dubbo.model.FilterRuleInputReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class testController  {
    @Autowired
    private ChannelService channelService;


    @RequestMapping("getChannelDetail")
    public Map<String, Object> getChannelDetail(String channelCode) {
        return channelService.getChannelDetail(channelCode);
    }

    @RequestMapping("getEventDetail")
    public Map<String, Object> getEventDetail(String evtCode){
        return channelService.getEventDetail(evtCode);
    }


    @RequestMapping("importRuleUserList")
    public Map<String, Object> importRuleUserList(FilterRuleInputReq req) {
        return channelService.importRuleUserList(req);
    }
}
