package com.zjtelcom.cpct.open.controller.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.HttpUtil;
import com.zjtelcom.cpct.open.base.controller.BaseController;
import com.zjtelcom.cpct.open.service.channel.OpenChannelService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/10/31
 * @Description:触点渠道
 */
@RestController
@RequestMapping("${openPath}")
public class OpenContactChannelController extends BaseController{



    @Autowired
    private OpenChannelService openChannelService;


    /**
     * 查询触点渠道
     * @param id
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/contactChannel/{id}", method = RequestMethod.GET)
    public String getContactChannel(@PathVariable String id, HttpServletResponse response) {
        try {
            Map<String, Object> contactChannelMap = openChannelService.queryById(id);
            return (String) contactChannelMap.get("params");
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return "";
        }
    }


    /**
     * 新增触点渠道
     * @param contactChannel
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/contactChannel", method = RequestMethod.POST)
    public String saveContactChannel(@RequestBody Channel contactChannel, HttpServletResponse response) {
        Map<String, Object> contactChannelMap = openChannelService.addByObject(contactChannel);
        //成功返回http状态201
        response.setStatus(HttpStatus.SC_CREATED);
        return JSON.toJSONString(contactChannelMap.get("params"));
    }


    /**
     * 更新触点渠道
     * @param id
     * @return application/json-patch+json
     */
    @CrossOrigin
    @RequestMapping(value = "/contactChannel/{id}", method = RequestMethod.PATCH)
    public String updateContactChannel(@PathVariable String id, @RequestBody String params, HttpServletResponse response) {
        try {
            Map<String, Object> contactChannelMap = openChannelService.updateByParams(id, params);
            return JSON.toJSONString(contactChannelMap.get("params"));
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
     * 删除触点渠道
     * @param id
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/contactChannel/{id}", method = RequestMethod.DELETE)
    public void deleteContactChannel(@PathVariable String id, HttpServletResponse response) {
        try {
            openChannelService.deleteById(id);
            //删除成功返回http状态码 204
            response.setStatus(HttpStatus.SC_NO_CONTENT);
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
        }

    }


    /**
     * 分页查询触点渠道列表
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/contactChannel", method = RequestMethod.GET)
    public String listContactChannelPage(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> parameterMap = HttpUtil.getRequestMap(request);
            Map<String, Object> contactChannelMap = openChannelService.queryListByMap(parameterMap);
            //  X-Total-Count  符合条件的总数
            response.setHeader("X-Total-Count", (String) contactChannelMap.get("size"));
            return JSON.toJSONString(contactChannelMap.get("params"), SerializerFeature.WriteMapNullValue);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }



}
