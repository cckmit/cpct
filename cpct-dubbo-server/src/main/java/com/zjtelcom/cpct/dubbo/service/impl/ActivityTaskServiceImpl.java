package com.zjtelcom.cpct.dubbo.service.impl;

import com.ql.util.express.DefaultContext;
import com.zjtelcom.cpct.dubbo.service.ActivityTaskService;
import com.zjtelcom.cpct.dubbo.service.CamApiSerService;
import com.zjtelcom.cpct.dubbo.service.CamApiService;
import com.zjtelcom.cpct.enums.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Service
@Transactional
public class ActivityTaskServiceImpl implements ActivityTaskService,Callable {
    private static final Logger log = LoggerFactory.getLogger(ActivityTaskServiceImpl.class);

    private Long activityId;
    private String type;
//    private String reqId;
    private Map<String, String> params;
    private Map<String, String> privateParams;
    private Map<String, String> labelItems;
    private List<Map<String, Object>> evtTriggers;
    private List<Map<String, Object>> strategyMapList;
    private DefaultContext<String, Object> context;



    public ActivityTaskServiceImpl(HashMap<String, Object> hashMap) {
        this.activityId = (Long)hashMap.get("mktCampaginId");
        this.type = (String) hashMap.get("type");
        this.params = (Map<String, String>)hashMap.get("map");
        this.privateParams = (Map<String, String>)hashMap.get("privateParams");
        this.labelItems = (Map<String, String>)hashMap.get("labelItems");
        this.evtTriggers =(List<Map<String, Object>>)hashMap.get("evtTriggers");
        this.strategyMapList = (List<Map<String, Object>>)hashMap.get("strategyMapList");
//        this.reqId = params.get("reqId");
        this.context = (DefaultContext<String, Object>)hashMap.get("defaultContext");
    }

    @Autowired(required = false)
    private CamApiService camApiService; // 活动任务

    @Autowired(required = false)
    private CamApiSerService camApiSerService; // 服务活动任务

    @Override
    public Object call() throws Exception {
        Map<String, Object> activityTaskResultMap = new HashMap<>();
        if(StatusCode.SERVICE_CAMPAIGN.getStatusCode().equals(type) || StatusCode.SERVICE_SALES_CAMPAIGN.getStatusCode().equals(type)){
            log.info("服务活动进入camApiSerService.ActivitySerTask");
            activityTaskResultMap = camApiSerService.ActivitySerTask(params, activityId, privateParams, labelItems, evtTriggers, strategyMapList, context);
        } else {
            log.info("进入camApiService.ActivityTask");
            activityTaskResultMap = camApiService.ActivityTask(params, activityId, privateParams, labelItems, evtTriggers, strategyMapList, context);
        }
        return activityTaskResultMap;
    }
}
