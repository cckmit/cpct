package com.zjtelcom.cpct.open.controller.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.domain.event.EventSorceDO;
import com.zjtelcom.cpct.dto.event.EventSorce;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.HttpUtil;
import com.zjtelcom.cpct.open.base.controller.BaseController;
import com.zjtelcom.cpct.open.service.event.OpenEventSourceService;
import com.zjtelcom.cpct.open.serviceImpl.channel.OpenChannelServiceImpl;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/10/26
 * @Description:事件源openapi接口
 */
@RestController
@RequestMapping("${openPath}")
public class OpenEventSourceController extends BaseController {




    @Autowired
    private OpenEventSourceService openEventSourceService;


    /**
     * 查询事件源  资源不存在返回404  存在返回200   "如果参数格式不对 该返回何种参数"
     * 目前请求的参数  和项目实际情况还需要进一步匹配
     * @param evtSrcId
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/eventSorce/{evtSrcId}", method = RequestMethod.GET)
    public String getEventSorce(@PathVariable String evtSrcId, HttpServletResponse response) {
        Long sourceId = Long.valueOf(evtSrcId);
        try {
            Map<String, Object> eventSorceMap = openEventSourceService.getEventSorce(sourceId);
            return (String) eventSorceMap.get("params");
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return "";
        }
    }


    /**
     * 新增事件源
     * @param eventSorce
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/eventSorce", method = RequestMethod.POST)
    public String saveEventSorce(@RequestBody EventSorce eventSorce, HttpServletResponse response) {
        Map<String, Object> eventSorceMap = openEventSourceService.saveEventSorce(eventSorce);
        //成功返回http状态201
        response.setStatus(HttpStatus.SC_CREATED);
        return JSON.toJSONString(eventSorceMap.get("params"));
    }


    /**
     * 更新事件源
     *
     * @param evtSrcId
     * @return application/json-patch+json
     */
    @CrossOrigin
    @RequestMapping(value = "/eventSorce/{evtSrcId}", method = RequestMethod.PATCH)
    public String updateEventSorce(@PathVariable String evtSrcId, @RequestBody String params, HttpServletResponse response) {
        try {
            Map<String, Object> eventSorceMap = openEventSourceService.updateEventSorce(evtSrcId, params);
            return JSON.toJSONString(eventSorceMap.get("params"));
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

    /**
     * 删除事件源
     *
     * @param evtSrcId
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/eventSorce/{evtSrcId}", method = RequestMethod.DELETE)
    public void deleteEventSorce(@PathVariable String evtSrcId, HttpServletResponse response) {
        Long sourceId = Long.valueOf(evtSrcId);
        try {
            openEventSourceService.deleteEventSorce(sourceId);
            //删除成功返回http状态码 204
            response.setStatus(HttpStatus.SC_NO_CONTENT);
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
        }

    }


    /**
     * 分页查询事件源列表
     * https://api。189。com/place/?offset=2&limit=100   offset当前页  limit每页的大小  offset默认为1  limit默认为30
     * https://api。189。com/place/?evtSrcId=1&offset=34> rel="last"
     * rel="first"表示首页  rel="prev" 表示前一页  rel="next" 表示下一页   rel="last" 表示最后一页
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/eventSorce", method = RequestMethod.GET)
    public String listEventSorcePage(HttpServletRequest request,HttpServletResponse response) {
        try {
            Map<String, Object> parameterMap = HttpUtil.getRequestMap(request);
            Map<String, Object> eventSorceMap = openEventSourceService.listEventSorcePage(parameterMap);
            //  X-Total-Count  符合条件的总数
            response.setHeader("X-Total-Count", (String) eventSorceMap.get("size"));
            return JSON.toJSONString(eventSorceMap.get("params"), SerializerFeature.WriteMapNullValue);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }





}
