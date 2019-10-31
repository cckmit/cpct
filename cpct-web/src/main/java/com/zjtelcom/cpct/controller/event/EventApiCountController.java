package com.zjtelcom.cpct.controller.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.request.event.QryContactEvtTypeReq;
import com.zjtelcom.cpct.service.event.EventApiCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/10/29 20:19
 * @version: V1.0
 */
@Controller
@RequestMapping("${adminPath}/eventApiCount")
public class EventApiCountController extends BaseController {

    @Autowired
    private EventApiCountService eventApiCountService;
    /**
     * 查询事件目录树
     */
    @RequestMapping("/count")
    @CrossOrigin
    public String eventApiCount(@RequestBody Map<String, Object> param) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = eventApiCountService.eventApiCount(param);
        } catch (Exception e) {
            logger.error("[op:EventApiCountController] fail to eventApiCount {}! Exception: ", JSONArray.toJSON(maps), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }
}