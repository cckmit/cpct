package com.zjtelcom.cpct.controller.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.event.EventRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/12/25 20:19
 * @version: V1.0
 */
@RestController
@RequestMapping("${adminPath}/eventRedis")
public class EventRedisController extends BaseController {

    @Autowired
    private EventRedisService eventRedisService;

    @RequestMapping("/delRedisByEventCode")
    @CrossOrigin
    public String delRedisByEventCode(@RequestBody Map<String, Object> param) {
        Map<String, Object> maps = new HashMap<>();
        try {
            String eventCode = (String) param.get("eventCode");
                maps = eventRedisService.delRedisByEventCode(eventCode);
        } catch (Exception e) {
            logger.error("[op:EventRedisController] fail to delRedisByEventCode {}! Exception: ", JSONArray.toJSON(maps), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    @RequestMapping("/getRedisByKey")
    @CrossOrigin
    public String getRedisByKey(@RequestBody Map<String, Object> param) {
        Map<String, Object> maps = new HashMap<>();
        try {
            String key = (String) param.get("key");
            maps = eventRedisService.getRedisByKey(key);
        } catch (Exception e) {
            logger.error("[op:EventRedisController] fail to getRedisByKey {}! Exception: ", JSONArray.toJSON(maps), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    @RequestMapping("/delRedisByKey")
    @CrossOrigin
    public String eventApiCount(@RequestBody Map<String, Object> param) {
        Map<String, Object> maps = new HashMap<>();
        try {
            String key = (String) param.get("key");
            maps = eventRedisService.delRedisByKey(key);
        } catch (Exception e) {
            logger.error("[op:EventRedisController] fail to delRedisByKey {}! Exception: ", JSONArray.toJSON(maps), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }
}