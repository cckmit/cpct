package com.zjtelcom.cpct.dubbo.dubboThreadPool.impl;

import com.ql.util.express.DefaultContext;
import com.zjtelcom.cpct.dubbo.dubboThreadPool.ActivityTaskService;
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
    private String reqId;
    private Map<String, String> params;
    private Map<String, String> privateParams;
    private Map<String, String> labelItems;
    private List<Map<String, Object>> evtTriggers;
    private List<Map<String, Object>> strategyMapList;
    private DefaultContext<String, Object> context;

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getPrivateParams() {
        return privateParams;
    }

    public void setPrivateParams(Map<String, String> privateParams) {
        this.privateParams = privateParams;
    }

    public Map<String, String> getLabelItems() {
        return labelItems;
    }

    public void setLabelItems(Map<String, String> labelItems) {
        this.labelItems = labelItems;
    }

    public List<Map<String, Object>> getEvtTriggers() {
        return evtTriggers;
    }

    public void setEvtTriggers(List<Map<String, Object>> evtTriggers) {
        this.evtTriggers = evtTriggers;
    }

    public List<Map<String, Object>> getStrategyMapList() {
        return strategyMapList;
    }

    public void setStrategyMapList(List<Map<String, Object>> strategyMapList) {
        this.strategyMapList = strategyMapList;
    }

    public DefaultContext<String, Object> getContext() {
        return context;
    }

    public void setContext(DefaultContext<String, Object> context) {
        this.context = context;
    }

    public ActivityTaskServiceImpl(Map<String, String> params, Long activityId, String type, Map<String, String> privateParams, Map<String, String> labelItems, List<Map<String, Object>> evtTriggers, List<Map<String, Object>> strategyMapList, DefaultContext<String, Object> context) {
        this.activityId = activityId;
        this.type = type;
        this.params = params;
        this.privateParams = privateParams;
        this.labelItems = labelItems;
        this.evtTriggers = evtTriggers;
        this.strategyMapList = strategyMapList;
        this.reqId = params.get("reqId");
        this.context = context;
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
