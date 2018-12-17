package com.zjtelcom.cpct.open.controller.tarGrp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.dto.grouping.TarGrpDetail;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.HttpUtil;
import com.zjtelcom.cpct.open.base.controller.BaseController;
import com.zjtelcom.cpct.open.service.tarGrp.OpenTarGrpService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author: anson
 * @CreateDate: 2018-11-05 12:42:12
 * @version: V 1.0
 * @Description:试算目标分群
 */
@RestController
@RequestMapping("${openPath}")
public class OpenTarGrpController extends BaseController {


    @Autowired
    private OpenTarGrpService openTarGrpService;


    /**
     * 查询试算目标分群详情
     *
     * @param id
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/tarGrp/{id}", method = RequestMethod.GET)
    public String getContactChannel(@PathVariable String id, HttpServletResponse response) {
        try {
            Map<String, Object> map = openTarGrpService.queryById(id);
            return JSON.toJSONString(map.get("params"), SerializerFeature.WriteMapNullValue);
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return "";
        }
    }


    /**
     * 新增试算目标分群
     *
     * @param tarGrp
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/tarGrp", method = RequestMethod.POST)
    public String saveContactChannel(@RequestBody TarGrpDetail tarGrp, HttpServletResponse response) {
        Map<String, Object> map = openTarGrpService.addByObject(tarGrp);
        //成功返回http状态201
        response.setStatus(HttpStatus.SC_CREATED);
        return JSON.toJSONString(map.get("params"), SerializerFeature.WriteMapNullValue);
    }


    /**
     * 更新试算目标分群
     *
     * @param id
     * @return application/json-patch+json
     */
    @CrossOrigin
    @RequestMapping(value = "/tarGrp/{id}", method = RequestMethod.PATCH)
    public String updateContactChannel(@PathVariable String id, @RequestBody String params, HttpServletResponse response) {
        try {
            Map<String, Object> map = openTarGrpService.updateByParams(id, params);
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
     * 删除试算目标分群
     *
     * @param id
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/tarGrp/{id}", method = RequestMethod.DELETE)
    public void deleteContactChannel(@PathVariable String id, HttpServletResponse response) {
        try {
            openTarGrpService.deleteById(id);
            //删除成功返回http状态码 204
            response.setStatus(HttpStatus.SC_NO_CONTENT);
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
        }

    }


    /**
     * 分页查询试算目标分群列表
     *
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/tarGrp", method = RequestMethod.GET)
    public String listContactChannelPage(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> parameterMap = HttpUtil.getRequestMap(request);
            Map<String, Object> map = openTarGrpService.queryListByMap(parameterMap);
            //  X-Total-Count  符合条件的总数
            response.setHeader("X-Total-Count", (String) map.get("size"));
            return JSON.toJSONString(map.get("params"), SerializerFeature.WriteMapNullValue);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }


}