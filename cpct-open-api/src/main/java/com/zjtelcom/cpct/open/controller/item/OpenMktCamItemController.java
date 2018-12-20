package com.zjtelcom.cpct.open.controller.item;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.domain.campaign.MktCamItem;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.HttpUtil;
import com.zjtelcom.cpct.open.base.controller.BaseController;
import com.zjtelcom.cpct.open.entity.mktCamItem.OpenMktCamItem;
import com.zjtelcom.cpct.open.service.item.OpenMktCamItemService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author: anson
 * @CreateDate: 2018-11-02 09:53:56
 * @version: V 1.0
 * @Description:营销活动推荐条目
 */
@RestController
@RequestMapping("${openPath}")
public class OpenMktCamItemController extends BaseController{



    @Autowired
    private OpenMktCamItemService openMktCamItemService;


    /**
     * 查询营销活动推荐条目
     * @param id
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/mktCamItem/{id}", method = RequestMethod.GET)
    public String getMktCamItem(@PathVariable String id, HttpServletResponse response) {
        try {
            Map<String, Object> map = openMktCamItemService.queryById(id);
            return JSON.toJSONString(map.get("params"), SerializerFeature.WriteMapNullValue);
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return "";
        }
    }


    /**
     * 新增营销活动推荐条目
     * @param  mktCamItem
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/mktCamItem", method = RequestMethod.POST)
    public String saveMktCamItem(@RequestBody MktCamItem mktCamItem, HttpServletResponse response) {
        Map<String, Object> map = openMktCamItemService.addByObject(mktCamItem);
        //成功返回http状态201
        response.setStatus(HttpStatus.SC_CREATED);
        return JSON.toJSONString(map.get("params"), SerializerFeature.WriteMapNullValue);
    }


    /**
     * 更新营销活动推荐条目
     * @param id
     * @return application/json-patch+json
     */
    @CrossOrigin
    @RequestMapping(value = "/mktCamItem/{id}", method = RequestMethod.PATCH)
    public String updateMktCamItem(@PathVariable String id, @RequestBody OpenMktCamItem openMktCamItem, HttpServletResponse response) {
        try {
            Map<String, Object> map = openMktCamItemService.updateByParams(id, openMktCamItem);
            return JSON.toJSONString(map.get("params"), SerializerFeature.WriteMapNullValue);
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
     * 删除营销活动推荐条目
     * @param id
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/mktCamItem/{id}", method = RequestMethod.DELETE)
    public void deleteMktCamItem(@PathVariable String id, HttpServletResponse response) {
        try {
            openMktCamItemService.deleteById(id);
            //删除成功返回http状态码 204
            response.setStatus(HttpStatus.SC_NO_CONTENT);
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
        }

    }


    /**
     * 分页查询营销活动推荐条目列表
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/mktCamItem", method = RequestMethod.GET)
    public String listMktCamItemPage(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> parameterMap = HttpUtil.getRequestMap(request);
            Map<String, Object> map = openMktCamItemService.queryListByMap(parameterMap);
            //  X-Total-Count  符合条件的总数
            response.setHeader("X-Total-Count", (String) map.get("size"));
            return JSON.toJSONString(map.get("params"), SerializerFeature.WriteMapNullValue);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }



}