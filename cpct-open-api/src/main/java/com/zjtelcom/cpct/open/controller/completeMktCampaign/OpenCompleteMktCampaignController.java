package com.zjtelcom.cpct.open.controller.completeMktCampaign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.open.service.completeMktCampaign.OpenCompleteMktCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("${openPath}")
public class OpenCompleteMktCampaignController {

    @Autowired
    private OpenCompleteMktCampaignService openCompleteMktCampaignService;

    @CrossOrigin
    @RequestMapping(value = "/completeMktCampaign", method = RequestMethod.POST)
    public String completeMktCampaign(Long mktCampaignId, String tacheCd) {
        Map<String, Object> resultMap = openCompleteMktCampaignService.completeMktCampaign(mktCampaignId, tacheCd);
        return JSON.toJSONString(resultMap, SerializerFeature.WriteMapNullValue);
    }
}
