package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.dubbo.service.EventApiService;
import com.zjtelcom.cpct.dubbo.service.SyncEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Description:
 * author: hyf
 * date: 2018/07/17 11:11
 * version: V1.0
 */
@Service
public class SyncEventServiceImpl implements SyncEventService {

    public static final Logger logger = LoggerFactory.getLogger(SyncEventServiceImpl.class);

    @Autowired(required = false)
    private EventApiService eventApiService;



    /**
     * 标签同步对外接口
     *
     * @param params
     * @return
     */
    @Override
    public void syncEvent(Map<String, Object> params) {
        try {
            if (params != null) {
                Map<String, Object> map = new HashMap<>();
                if (params.containsKey("ISI")) {
                    map.put("reqId", params.get("ISI"));
                } else {
                    logger.error("传入参数ISI不存在");
                }
                if (params.containsKey("eventId")) {
                    map.put("eventCode", params.get("eventId"));
                } else {
                    logger.error("传入参数eventId不存在");
                }
                if (params.containsKey("channelId")) {
                    map.put("channelCode", params.get("channelId"));
                } else {
                    logger.error("传入参数channelId不存在");
                }
                if (params.containsKey("lanId")) {
                    map.put("lanId", params.get("lanId"));
                } else {
                    logger.error("传入参数lanId不存在");
                }
                JSONObject json = new JSONObject();
                if (params.containsKey("triggers")) {
                    List<Map<String, Object>> targets = (List<Map<String, Object>>) params.get("triggers");
                    if (targets != null && targets.size() > 0) {
                        for (Map<String, Object> tar : targets) {
                            Map<String, Object> target = (Map<String, Object>) tar.get("trigger");
                            if(target.containsKey("ACC_NBR")) {
                                map.put("accNbr", target.get("ACC_NBR"));
                            } else if(target.containsKey("INTEGRATION_ID")) {
                                map.put("integrationId", target.get("INTEGRATION_ID"));
                            } else if(target.containsKey("CUST_NBR")) {
                                map.put("custId", target.get("CUST_NBR"));
                            } else {
                                json = json.fluentPutAll(target);
                            }

                        }
                    }
                } else {
                    logger.error("传入参数triggers不存在");
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                map.put("evtCollectTime", simpleDateFormat.format(new Date())); //事件触发时间
                map.put("evtContent", json.toString()); //事件采集项

                System.out.println(map.toString());
                eventApiService.CalculateCPCSync(map);

            } else {
                logger.error("传入参数为null");
            }
        } catch (Exception e) {
            logger.error("[op:SyncLabelServiceImpl] fail to syncEventTest", e);
        }
    }


}
