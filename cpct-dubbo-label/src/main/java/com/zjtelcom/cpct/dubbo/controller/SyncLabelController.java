package com.zjtelcom.cpct.dubbo.controller;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.dubbo.out.OpenApiScheService;
import com.zjtelcom.cpct.dubbo.service.SyncEventService;
import com.zjtelcom.cpct.dubbo.service.SyncLabelService;
import com.zjtelcom.cpct.service.campaign.MktCamDirectoryService;
import com.zjtelcom.cpct.service.campaign.OpenCampaignScheService;
import com.zjtelcom.cpct.util.RedisUtils;
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
    private OpenApiScheService openApiScheService;
    @Autowired
    private OpenCampaignScheService openCampaignScheService;
    @Autowired
    private MktCampaignMapper campaignMapper;
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MktCamDirectoryService mktCamDirectoryService;

    @RequestMapping(value = "/listAllDirectoryTree", method = RequestMethod.POST)
    @CrossOrigin
    public String listAllDirectoryTree() throws Exception {
        Map<String, Object> directoryMap = mktCamDirectoryService.listAllDirectoryTree();
        return JSON.toJSONString(directoryMap);
    }



    @RequestMapping(value = "openApimktCampaignBorninfoOrder", method = RequestMethod.POST)
    @CrossOrigin
    public String openCampaignScheForDay(@RequestBody HashMap<String,Object> id) {
        Map result = new HashMap();
        MktCampaignDO campaignDO = campaignMapper.selectByPrimaryKey(Long.valueOf(id.get("id").toString()));
        result = openCampaignScheService.openApimktCampaignBorninfoOrder(campaignDO);
//        try {
//            redisUtils.setRedisUnit("TEST_001","123",5);
//            System.out.println(redisUtils.get("TEST_001"));
//            Thread.sleep(7000);
//            System.out.println(redisUtils.get("TEST_001"));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return JSON.toJSON(result).toString();
    }


    @RequestMapping(value = "openCampaignScheForDay", method = RequestMethod.POST)
    @CrossOrigin
    public String openCampaignScheForDay() {
        Map result = new HashMap();
        result = openApiScheService.openCampaignScheForDay();
        return JSON.toJSON(result).toString();
    }


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


}
