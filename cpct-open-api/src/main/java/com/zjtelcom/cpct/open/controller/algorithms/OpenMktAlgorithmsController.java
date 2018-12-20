package com.zjtelcom.cpct.open.controller.algorithms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.HttpUtil;
import com.zjtelcom.cpct.open.base.controller.BaseController;
import com.zjtelcom.cpct.open.entity.mktAlgorithms.OpenMktAlgorithms;
import com.zjtelcom.cpct.open.service.algorithms.OpenMktAlgorithmsService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("${openPath}")
public class OpenMktAlgorithmsController extends BaseController {

    @Autowired
    private OpenMktAlgorithmsService openMktAlgorithmsService;

    /*
    **查询算法定义详情
    */
    @CrossOrigin
    @RequestMapping(value = "/mktAlgorithms/{id}", method = RequestMethod.GET)
    public String getEvent(@PathVariable String id, HttpServletResponse response) {
        try{
            Map<String, Object> eventMap = openMktAlgorithmsService.getMktAlgorithms(id);
            return JSON.toJSONString(eventMap.get("params"), SerializerFeature.WriteMapNullValue);
        }catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return "";
        }
    }

    /*
    **新建算法定义
    */
    @CrossOrigin
    @RequestMapping(value = "/mktAlgorithms", method = RequestMethod.POST)
    public String saveEvent(@RequestBody OpenMktAlgorithms openMktAlgorithms, HttpServletResponse response) {
        Map<String, Object> eventMap = openMktAlgorithmsService.saveMktAlgorithms(openMktAlgorithms);
        response.setStatus(HttpStatus.SC_CREATED);
        return JSON.toJSONString(eventMap.get("params"), SerializerFeature.WriteMapNullValue);
    }

    /*
    **修改算法定义
    */
    @CrossOrigin
    @RequestMapping(value = "/mktAlgorithms/{id}", method = RequestMethod.PATCH)
    public String updateEvent(@PathVariable String id, @RequestBody String params, HttpServletResponse response) {
        try {
            Map<String, Object> eventMap = openMktAlgorithmsService.updateMktAlgorithms(id, params);
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
    **删除算法定义
    */
    @CrossOrigin
    @RequestMapping(value = "/mktAlgorithms/{id}", method = RequestMethod.DELETE)
    public void deleteEvent(@PathVariable String id, HttpServletResponse response) {
        try{
            openMktAlgorithmsService.deleteMktAlgorithms(id);
            response.setStatus(HttpStatus.SC_NO_CONTENT);
        }catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
        }
    }

    /*
    **根据多种查询条件查询算法定义
    */
    @CrossOrigin
    @RequestMapping(value = "/mktAlgorithms", method = RequestMethod.GET)
    public String listEventPage(HttpServletRequest request, HttpServletResponse response) {
        try{
            Map<String, Object> map = HttpUtil.getRequestMap(request);
            Map<String, Object> eventMap = openMktAlgorithmsService.listMktAlgorithms(map);
            response.setHeader("X-Total-Count", (String) eventMap.get("size"));
            return JSON.toJSONString(eventMap.get("params"), SerializerFeature.WriteMapNullValue);
        }catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
