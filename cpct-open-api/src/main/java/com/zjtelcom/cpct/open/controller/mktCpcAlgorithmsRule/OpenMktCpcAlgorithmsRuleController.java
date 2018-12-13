package com.zjtelcom.cpct.open.controller.mktCpcAlgorithmsRule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.domain.campaign.MktCpcAlgorithmsRulDO;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.HttpUtil;
import com.zjtelcom.cpct.open.base.controller.BaseController;
import com.zjtelcom.cpct.open.service.mktCpcAlgorithmsRule.OpenMktCpcAlgorithmsRuleService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author: anson
 * @CreateDate: 2018-11-07 15:05:02
 * @version: V 1.0
 * @Description:CPC算法规则
 */
@RestController
@RequestMapping("${openPath}")
public class OpenMktCpcAlgorithmsRuleController extends BaseController {


    @Autowired
    private OpenMktCpcAlgorithmsRuleService openMktCpcAlgorithmsRuleService;


    /**
     * 查询CPC算法规则
     *
     * @param id
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/mktCpcAlgorithmsRul/{id}", method = RequestMethod.GET)
    public String getContactChannel(@PathVariable String id, HttpServletResponse response) {
        try {
            Map<String, Object> map = openMktCpcAlgorithmsRuleService.queryById(id);
            return JSON.toJSONString(map.get("params"), SerializerFeature.WriteMapNullValue);
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return "";
        }
    }


    /**
     * 新增CPC算法规则
     *
     * @param mktCpcAlgorithmsRulDO
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/mktCpcAlgorithmsRul", method = RequestMethod.POST)
    public String saveContactChannel(@RequestBody MktCpcAlgorithmsRulDO mktCpcAlgorithmsRulDO, HttpServletResponse response) {
        Map<String, Object> map = openMktCpcAlgorithmsRuleService.addByObject(mktCpcAlgorithmsRulDO);
        //成功返回http状态201
        response.setStatus(HttpStatus.SC_CREATED);
        return JSON.toJSONString(map.get("params"), SerializerFeature.WriteMapNullValue);
    }


    /**
     * 更新CPC算法规则
     *
     * @param id
     * @return application/json-patch+json
     */
    @CrossOrigin
    @RequestMapping(value = "/mktCpcAlgorithmsRul/{id}", method = RequestMethod.PATCH)
    public String updateContactChannel(@PathVariable String id, @RequestBody String params, HttpServletResponse response) {
        try {
            Map<String, Object> map = openMktCpcAlgorithmsRuleService.updateByParams(id, params);
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
     * 删除CPC算法规则
     *
     * @param id
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/mktCpcAlgorithmsRul/{id}", method = RequestMethod.DELETE)
    public void deleteContactChannel(@PathVariable String id, HttpServletResponse response) {
        try {
            openMktCpcAlgorithmsRuleService.deleteById(id);
            //删除成功返回http状态码 204
            response.setStatus(HttpStatus.SC_NO_CONTENT);
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
        }

    }


    /**
     * 分页查询CPC算法规则列表
     *
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/mktCpcAlgorithmsRul", method = RequestMethod.GET)
    public String listContactChannelPage(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> parameterMap = HttpUtil.getRequestMap(request);
            Map<String, Object> map = openMktCpcAlgorithmsRuleService.queryListByMap(parameterMap);
            //  X-Total-Count  符合条件的总数
            response.setHeader("X-Total-Count", (String) map.get("size"));
            return JSON.toJSONString(map.get("params"), SerializerFeature.WriteMapNullValue);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }


}