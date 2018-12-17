package com.zjtelcom.cpct.open.controller.camEvtRel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.domain.campaign.MktCamEvtRelDO;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.HttpUtil;
import com.zjtelcom.cpct.open.base.controller.BaseController;
import com.zjtelcom.cpct.open.entity.mktCamEvtRel.OpenMktCamEvtRel;
import com.zjtelcom.cpct.open.service.camEvtRel.OpenMktCamEvtRelService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author: anson
 * @CreateDate: 2018-11-03 17:06:17
 * @version: V 1.0
 * @Description:营销活动关联事件
 */
@RestController
@RequestMapping("${openPath}")
public class OpenMktCamEvtRelController extends BaseController{



    @Autowired
    private OpenMktCamEvtRelService openOpenMktCamEvtRelService;


    /**
     * 查询营销活动关联事件
     * @param id
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/mktCamEvtRel/{id}", method = RequestMethod.GET)
    public String getMktCamEvtRel(@PathVariable String id, HttpServletResponse response) {
        try {
            Map<String, Object> map = openOpenMktCamEvtRelService.queryById(id);
            return JSON.toJSONString(map.get("params"));
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return "";
        }
    }


    /**
     * 新增营销活动关联事件
     * @param  openMktCamEvtRel
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/mktCamEvtRel", method = RequestMethod.POST)
    public String saveMktCamEvtRel(@RequestBody OpenMktCamEvtRel openMktCamEvtRel, HttpServletResponse response) {
        Map<String, Object> map = openOpenMktCamEvtRelService.addByObject(openMktCamEvtRel);
        //成功返回http状态201
        response.setStatus(HttpStatus.SC_CREATED);
        return JSON.toJSONString(map.get("params"));
    }


    /**
     * 更新营销活动关联事件
     * @param id
     * @return application/json-patch+json
     */
    @CrossOrigin
    @RequestMapping(value = "/mktCamEvtRel/{id}", method = RequestMethod.PATCH)
    public String updateMktCamEvtRel(@PathVariable String id, @RequestBody String params, HttpServletResponse response) {
        try {
            Map<String, Object> map = openOpenMktCamEvtRelService.updateByParams(id, params);
            return JSON.toJSONString(map.get("params"));
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
     * 删除营销活动关联事件
     * @param id
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/mktCamEvtRel/{id}", method = RequestMethod.DELETE)
    public void deleteMktCamEvtRel(@PathVariable String id, HttpServletResponse response) {
        try {
            openOpenMktCamEvtRelService.deleteById(id);
            //删除成功返回http状态码 204
            response.setStatus(HttpStatus.SC_NO_CONTENT);
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
        }

    }


    /**
     * 分页查询营销活动关联事件列表
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/mktCamEvtRel", method = RequestMethod.GET)
    public String listMktCamEvtRelPage(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> parameterMap = HttpUtil.getRequestMap(request);
            Map<String, Object> map = openOpenMktCamEvtRelService.queryListByMap(parameterMap);
            //  X-Total-Count  符合条件的总数
            response.setHeader("X-Total-Count", (String) map.get("size"));
            return JSON.toJSONString(map.get("params"), SerializerFeature.WriteMapNullValue);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }



}