package com.zjtelcom.cpct.open.controller.script;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.dto.channel.MktScript;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.HttpUtil;
import com.zjtelcom.cpct.open.base.controller.BaseController;
import com.zjtelcom.cpct.open.service.script.OpenScriptService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/10/30
 * @Description:营销脚本
 */
@RestController
@RequestMapping("${openPath}")
public class OpenScriptController extends BaseController{




    @Autowired
    private OpenScriptService openScriptService;


    /**
     * 查询营销脚本详情
     * @param id
     * @param response
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/mktScript/{id}", method = RequestMethod.GET)
    public String getMktScript(@PathVariable String id,HttpServletResponse response){
        Long scriptId = Long.valueOf(id);
        try {
            Map<String, Object> eventSorceMap = openScriptService.selectByPrimaryKey(scriptId);
            return JSON.toJSONString(eventSorceMap.get("params"));
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return "";
        }
    }



    /**
     * @return
     * 查询营销脚本列表
     */
    @CrossOrigin
    @RequestMapping(value = "/mktScript", method = RequestMethod.GET)
    public String getMktScriptList(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> parameterMap = HttpUtil.getRequestMap(request);
            Map<String, Object> map = openScriptService.selectScriptList(parameterMap);
            //  X-Total-Count  符合条件的总数
            response.setHeader("X-Total-Count", (String) map.get("size"));
            return JSON.toJSONString(map.get("params"), SerializerFeature.WriteMapNullValue);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 新建营销脚本
     * @param mktScript
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/mktScript", method = RequestMethod.POST)
    public String createMktScript(@RequestBody MktScript mktScript, HttpServletResponse response) {
        // 得到创建脚本的人角色信息  暂时写死
        long userId=getUserId();
        Map<String, Object> mktScriptMap = openScriptService.createMktScript(userId,mktScript);
        //成功返回http状态201
        response.setStatus(HttpStatus.SC_CREATED);
        return JSON.toJSONString(mktScriptMap.get("params"));
    }


    /**
     * 修改营销脚本
     *
     * @param evtSrcId
     * @return application/json-patch+json
     */
    @CrossOrigin
    @RequestMapping(value = "/mktScript/{id}", method = RequestMethod.PATCH)
    public String updateMktScript(@PathVariable String evtSrcId, @RequestBody String params, HttpServletResponse response) {
        try {
            Map<String, Object> eventSorceMap = openScriptService.modMktScript(1L, null);
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
     * 删除营销脚本
     *
     * @param id
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/mktScript/{id}", method = RequestMethod.DELETE)
    public void deleteMktScript(@PathVariable String id, HttpServletResponse response) {
        Long scriptId = Long.valueOf(id);
        try {
            openScriptService.delMktScript(scriptId);
            //删除成功返回http状态码 204
            response.setStatus(HttpStatus.SC_NO_CONTENT);
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
        }

    }


    /**
     * 操作人角色 先写死
     * @return
     */
    public static Long getUserId(){
        return 1L;
    }


}
