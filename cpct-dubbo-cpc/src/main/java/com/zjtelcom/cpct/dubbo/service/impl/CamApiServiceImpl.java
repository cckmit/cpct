package com.zjtelcom.cpct.dubbo.service.impl;


import com.ql.util.express.DefaultContext;
import com.zjtelcom.cpct.dubbo.service.CamApiService;
import com.zjtelcom.cpct.service.dubbo.CamCpcService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CamApiServiceImpl implements CamApiService {

    @Value("${thread.maxPoolSize}")
    private int maxPoolSize;
    @Value("${table.infallible}")
    private String defaultInfallibleTable;

    @Autowired(required = false)
    private CamCpcService camCpcService;

    /**
     * 活动级别验证
     */
    @Override
    public Map<String, Object> ActivityTask(Map<String, String> params, Long activityId, Map<String, String> privateParams, Map<String, String> laubelItems, List<Map<String, Object>> evtTriggers, List<Map<String, Object>> strategyMapList, DefaultContext<String, Object> context) {
        Map<String, Object> activity = camCpcService.ActivityCpcTask(params, activityId, privateParams, laubelItems, evtTriggers, strategyMapList, context);
        return activity;
    }

}