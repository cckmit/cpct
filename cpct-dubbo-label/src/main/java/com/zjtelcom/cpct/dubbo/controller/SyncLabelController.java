package com.zjtelcom.cpct.dubbo.controller;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.dubbo.model.RecordModel;
//import com.zjtelcom.cpct.dubbo.out.CampaignService;
import com.zjtelcom.cpct.dubbo.service.SyncEventService;
import com.zjtelcom.cpct.dubbo.service.SyncLabelService;
import com.zjtelcom.cpct.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/label")
public class SyncLabelController {

    @Autowired
    private SyncLabelService syncLabelService;
    @Autowired
    private SyncEventService syncEventService;
//    @Autowired
//    private CampaignService campaignService;

    @RequestMapping(value = "syncLabel", method = RequestMethod.POST)
    @CrossOrigin
    public String syncLabel(@RequestBody HashMap<String,Object> model) {
        Map result = new HashMap();

//        result = syncLabelService.initialization();
        result = syncLabelService.syncLabelInfo(model);
        return JSON.toJSON(result).toString();
    }

    @RequestMapping(value = "syncEvent", method = RequestMethod.POST)
    @CrossOrigin
    public String syncEvent(@RequestBody Map<String,Object> param) {
        syncEventService.syncEvent(param);
        return "调用成功";
    }

    @RequestMapping(value = "initLabelCatalog", method = RequestMethod.POST)
    @CrossOrigin
    public String initLabelCatalog() {
        syncLabelService.initLabelCatalog();
        return "调用成功";
    }

//    @PostMapping("campaignDelayNotice")
//    @CrossOrigin
//    public void campaignDelayNotice() {
//        /*String endTime = "2019-10-02";
//        Date date = DateUtil.string2DateTime4Minute(endTime);
//        int i = DateUtil.daysBetween(new Date(), date);*/
//
//        campaignService.campaignDelayNotice();
//    }
}
