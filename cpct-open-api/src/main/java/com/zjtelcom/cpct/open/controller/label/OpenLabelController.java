package com.zjtelcom.cpct.open.controller.label;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.open.base.common.HttpUtil;
import com.zjtelcom.cpct.open.base.controller.BaseController;
import com.zjtelcom.cpct.open.service.label.OpenLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/12/17
 * @Description: 通过资产编码或者业务号码 查询客户标签信息
 * 1.客户编码           "custId":"171100020152783"
   2.资产编码+业务号码   1-GB1Jg033776   15356152333
 */
@RestController
@RequestMapping("${openPath}")
public class OpenLabelController extends BaseController {

    @Autowired
    private OpenLabelService openLabelService;


    @CrossOrigin
    @RequestMapping(value = "/custInjectionLabel", method = RequestMethod.GET)
    public String listInjectionLabelPage(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> parameterMap = HttpUtil.getRequestMap(request);
            Map<String, Object> map = openLabelService.queryListByMap(parameterMap);
            return JSON.toJSONString(map.get("params"), SerializerFeature.WriteMapNullValue);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }
}
