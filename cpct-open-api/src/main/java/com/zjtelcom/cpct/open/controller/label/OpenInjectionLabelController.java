package com.zjtelcom.cpct.open.controller.label;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.HttpUtil;
import com.zjtelcom.cpct.open.base.controller.BaseController;
import com.zjtelcom.cpct.open.entity.label.InjectionLabel;
import com.zjtelcom.cpct.open.service.label.OpenInjectionLabelService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author: anson
 * @CreateDate: 2018-11-06 15:01:47
 * @version: V 1.0
 * @Description:注智标签
 */
@RestController
@RequestMapping("${openPath}")
public class OpenInjectionLabelController extends BaseController{

    @Autowired
    private OpenInjectionLabelService openInjectionLabelService;

    /**
     * 查询注智标签
     * @param id
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/injectionLabel/{id}", method = RequestMethod.GET)
    public String getInjectionLabel(@PathVariable String id, HttpServletResponse response) {
        try {
            Map<String, Object> map = openInjectionLabelService.queryById(id);
            return JSON.toJSONString(map.get("params"),SerializerFeature.WriteMapNullValue);
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return "";
        }
    }


    /**
     * 新增注智标签
     * @param  injectionLabel
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/injectionLabel", method = RequestMethod.POST)
    public String saveInjectionLabel(@RequestBody InjectionLabel injectionLabel, HttpServletResponse response) {
        Map<String, Object> map = openInjectionLabelService.addByObject(injectionLabel);
        //成功返回http状态201
        response.setStatus(HttpStatus.SC_CREATED);
        return JSON.toJSONString(map.get("params"),SerializerFeature.WriteMapNullValue);
    }


    /**
     * 更新注智标签
     * @param id
     * @return application/json-patch+json
     */
    @CrossOrigin
    @RequestMapping(value = "/injectionLabel/{id}", method = RequestMethod.PATCH)
    public String updateInjectionLabel(@PathVariable String id, @RequestBody String params, HttpServletResponse response) {
        try {
            Map<String, Object> map = openInjectionLabelService.updateByParams(id, params);
            return JSON.toJSONString(map.get("params"),SerializerFeature.WriteMapNullValue);
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
     * 删除注智标签
     * @param id
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/injectionLabel/{id}", method = RequestMethod.DELETE)
    public void deleteInjectionLabel(@PathVariable String id, HttpServletResponse response) {
        try {
            openInjectionLabelService.deleteById(id);
            //删除成功返回http状态码 204
            response.setStatus(HttpStatus.SC_NO_CONTENT);
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
        }

    }


    /**
     * 分页查询注智标签列表
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/injectionLabel", method = RequestMethod.GET)
    public String listInjectionLabelPage(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> parameterMap = HttpUtil.getRequestMap(request);
            Map<String, Object> map = openInjectionLabelService.queryListByMap(parameterMap);
            //  X-Total-Count  符合条件的总数
            response.setHeader("X-Total-Count", (String) map.get("size"));
            return JSON.toJSONString(map.get("params"), SerializerFeature.WriteMapNullValue);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }



}