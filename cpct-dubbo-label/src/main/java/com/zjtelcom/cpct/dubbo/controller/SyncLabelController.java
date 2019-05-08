package com.zjtelcom.cpct.dubbo.controller;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.dubbo.model.RecordModel;
import com.zjtelcom.cpct.dubbo.out.TrialStatusUpService;
import com.zjtelcom.cpct.dubbo.service.SyncEventService;
import com.zjtelcom.cpct.dubbo.service.SyncLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/label")
public class SyncLabelController {
    @Autowired
    private SyncLabelService syncLabelService;

    @Autowired
    private SyncEventService syncEventService;
    @Autowired
    private TrialStatusUpService trialStatusUpService;

    @RequestMapping(value = "syncLabel", method = RequestMethod.POST)
    @CrossOrigin
    public String syncLabel(@RequestBody HashMap<String,Object> model) {
        Map result = new HashMap();

//        result = syncLabelService.initialization();
        result = syncLabelService.syncLabelInfo(model);
        return JSON.toJSON(result).toString();
    }

    @RequestMapping(value = "updateOperationStatus", method = RequestMethod.POST)
    @CrossOrigin
    public Map<String,Object> syncEvent(@RequestBody Map<String,Object> param) {
        Map<String,Object> result = trialStatusUpService.updateOperationStatus(param);
        return result;
    }



}
