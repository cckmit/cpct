package com.zjtelcom.cpct.open.controller.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.HttpUtil;
import com.zjtelcom.cpct.open.base.controller.BaseController;
import com.zjtelcom.cpct.open.entity.event.Event;
import com.zjtelcom.cpct.open.service.event.OpenEventService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("${openPath}")
public class OpenEventController extends BaseController{

    @Autowired(required = false)
    private OpenEventService openEventService;

    /*
    **根据事件id主键查询
    */
    @CrossOrigin
    @RequestMapping(value = "/event/{eventId}", method = RequestMethod.GET)
    public String getEvent(@PathVariable String eventId, HttpServletResponse response) {
        Long contactEvtId = Long.valueOf(eventId);
        try{
            Map<String, Object> eventMap = openEventService.getEvent(contactEvtId);
            return JSON.toJSONString(eventMap.get("params"), SerializerFeature.WriteMapNullValue);
        }catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return "";
        }
    }

    /*
    **新增事件
    */
    @CrossOrigin
    @RequestMapping(value = "/event", method = RequestMethod.POST)
    public String saveEvent(@RequestBody Event event, HttpServletResponse response) {
        Map<String, Object> eventMap = openEventService.saveEvent(event);
        response.setStatus(HttpStatus.SC_CREATED);
        return JSON.toJSONString(eventMap.get("params"), SerializerFeature.WriteMapNullValue);
    }

    /*
    **修改事件
    */
    @CrossOrigin
    @RequestMapping(value = "/event/{eventId}", method = RequestMethod.PATCH)
    public String updateEvent(@PathVariable String eventId, @RequestBody String params, HttpServletResponse response) {
        try {
            Map<String, Object> eventMap = openEventService.updateEvent(eventId, params);
            return JSON.toJSONString(eventMap.get("params"), SerializerFeature.WriteMapNullValue);
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            //参数错误
            response.setStatus(HttpStatus.SC_CONFLICT);
            return "";
        }
    }

    /*
    **删除事件
    */
    @CrossOrigin
    @RequestMapping(value = "/event/{eventId}", method = RequestMethod.DELETE)
    public void deleteEvent(@PathVariable String eventId, HttpServletResponse response) {
        Long contactEvtId = Long.valueOf(eventId);
        try{
            openEventService.deleteEvent(contactEvtId);
            response.setStatus(HttpStatus.SC_NO_CONTENT);
        }catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
        }
    }

    /*
    **查询事件列表
    */
    @CrossOrigin
    @RequestMapping(value = "/event", method = RequestMethod.GET)
    public String listEventPage(HttpServletRequest request, HttpServletResponse response) {
        try{
            Map<String, Object> map = HttpUtil.getRequestMap(request);
            Map<String, Object> eventMap = openEventService.listEventPage(map);
            response.setHeader("X-Total-Count", (String) eventMap.get("size"));
            return JSON.toJSONString(eventMap.get("params"), SerializerFeature.WriteMapNullValue);
        }catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
