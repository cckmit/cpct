package com.zjtelcom.cpct.open.controller.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.dto.event.ContactEvtType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.HttpUtil;
import com.zjtelcom.cpct.open.base.controller.BaseController;
import com.zjtelcom.cpct.open.entity.event.EventType;
import com.zjtelcom.cpct.open.service.event.OpenEventTypeService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("${openPath}")
public class OpenEventTypeController extends BaseController {

    @Autowired
    private OpenEventTypeService openEventTypeService;

    /*
    **根据事件类型id主键查询
    */
    @CrossOrigin
    @RequestMapping(value = "/eventType/{evtTypeId}", method = RequestMethod.GET)
    public String getEventType(@PathVariable String evtTypeId, HttpServletResponse response) {
        try{
            Map<String, Object> eventTypeMap = openEventTypeService.getEventType(Long.valueOf(evtTypeId));
            return (String) JSON.toJSONString(eventTypeMap.get("params"), SerializerFeature.WriteMapNullValue);
        }catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return "";
        }
    }

    /*
    **新增事件类型
    */
    @CrossOrigin
    @RequestMapping(value = "/eventType", method = RequestMethod.POST)
    public String saveEventType(@RequestBody EventType eventType, HttpServletResponse response) {
        Map<String, Object> eventTypeMap = openEventTypeService.saveEventType(eventType);
        response.setStatus(HttpStatus.SC_CREATED);
        return JSON.toJSONString(eventTypeMap.get("params"), SerializerFeature.WriteMapNullValue);
    }

    /*
    **修改事件类型
    */
    @CrossOrigin
    @RequestMapping(value = "/eventType/{evtTypeId}", method = RequestMethod.PATCH)
    public String updateEvent(@PathVariable String evtTypeId, @RequestBody String params, HttpServletResponse response) {
        try {
            Map<String, Object> eventTypeMap = openEventTypeService.updateEventType(evtTypeId, params);
            return JSON.toJSONString(eventTypeMap.get("params"), SerializerFeature.WriteMapNullValue);
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
    **删除事件类型
    */
    @CrossOrigin
    @RequestMapping(value = "/eventType/{evtTypeId}", method = RequestMethod.DELETE)
    public void deleteEvent(@PathVariable String evtTypeId, HttpServletResponse response) {
        try{
            openEventTypeService.deleteEventType(Long.valueOf(evtTypeId));
            response.setStatus(HttpStatus.SC_NO_CONTENT);
        }catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
        }
    }

    /*
    **查询事件类型列表
    */
    @CrossOrigin
    @RequestMapping(value = "/eventType", method = RequestMethod.GET)
    public String listEventPage(HttpServletRequest request, HttpServletResponse response) {
        try{
            Map<String, Object> map = HttpUtil.getRequestMap(request);
            Map<String, Object> eventMap = openEventTypeService.listEventPageType(map);
            response.setHeader("X-Total-Count", (String) eventMap.get("size"));
            return JSON.toJSONString(eventMap.get("params"), SerializerFeature.WriteMapNullValue);
        }catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
