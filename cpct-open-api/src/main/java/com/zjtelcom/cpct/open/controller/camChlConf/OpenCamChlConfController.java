package com.zjtelcom.cpct.open.controller.camChlConf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.HttpUtil;
import com.zjtelcom.cpct.open.base.controller.BaseController;
import com.zjtelcom.cpct.open.entity.event.EventType;
import com.zjtelcom.cpct.open.entity.mktCamChlConf.OpenMktCamChlConf;
import com.zjtelcom.cpct.open.service.camChlConf.OpenCamChlConfService;
import com.zjtelcom.cpct.open.service.event.OpenEventTypeService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("${openPath}")
public class OpenCamChlConfController extends BaseController {
    @Autowired
    OpenCamChlConfService openCamChlConfService;

    /*
    **查询营销执行渠道推送规则详情
    */
    @CrossOrigin
    @RequestMapping(value = "/mktCamChlConf/{id}", method = RequestMethod.GET)
    public String getMktCamChlConf(@PathVariable String id, HttpServletResponse response) {
        try{
            Map<String, Object> resultMap = openCamChlConfService.queryById(id);
            return JSON.toJSONString(resultMap.get("params"), SerializerFeature.WriteMapNullValue);
        }catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return "";
        }
    }

    /*
    **新建营销执行渠道推送规则
    */
    @CrossOrigin
    @RequestMapping(value = "/mktCamChlConf", method = RequestMethod.POST)
    public String saveMktCamChlConf(@RequestBody OpenMktCamChlConf openMktCamChlConf, HttpServletResponse response) {
        Map<String, Object> resultMap = openCamChlConfService.addByObject(openMktCamChlConf);
        response.setStatus(HttpStatus.SC_CREATED);
        return JSON.toJSONString(resultMap.get("params"), SerializerFeature.WriteMapNullValue);
    }

    /*
    **修改营销执行渠道推送规则
    */
    @CrossOrigin
    @RequestMapping(value = "/mktCamChlConf/{id}", method = RequestMethod.PATCH)
    public String updateMktCamChlConf(@PathVariable String id, @RequestBody String params, HttpServletResponse response) {
        try {
            Map<String, Object> resultMap = openCamChlConfService.updateByParams(id, params);
            return JSON.toJSONString(resultMap.get("params"), SerializerFeature.WriteMapNullValue);
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
    **删除营销执行渠道推送规则
    */
    @CrossOrigin
    @RequestMapping(value = "/mktCamChlConf/{id}", method = RequestMethod.DELETE)
    public void deleteMktCamChlConf(@PathVariable String id, HttpServletResponse response) {
        try{
            openCamChlConfService.deleteById(id);
            response.setStatus(HttpStatus.SC_NO_CONTENT);
        }catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
        }
    }

    /*
    **查询营销执行渠道推送规则列表
    */
    @CrossOrigin
    @RequestMapping(value = "/mktCamChlConf", method = RequestMethod.GET)
    public String listMktCamChlConfPage(HttpServletRequest request, HttpServletResponse response) {
        try{
            Map<String, Object> map = HttpUtil.getRequestMap(request);
            Map<String, Object> resultMap = openCamChlConfService.queryListByMap(map);
            response.setHeader("X-Total-Count", (String) resultMap.get("size"));
            return JSON.toJSONString(resultMap.get("params"), SerializerFeature.WriteMapNullValue);
        }catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
