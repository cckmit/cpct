package com.zjtelcom.cpct.open.controller.label;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ctzj.smt.bss.cooperate.service.dubbo.IContactTaskService;
import com.zjpii.biz.service.crm.AssetQueryService;
import com.zjtelcom.cpct.open.base.common.HttpUtil;
import com.zjtelcom.cpct.open.base.controller.BaseController;
import com.zjtelcom.cpct.open.service.label.OpenLabelService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.MD5Util;
import org.apache.commons.lang.StringUtils;
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

    @Autowired(required = false)
    private AssetQueryService assetQueryService;


    @CrossOrigin
    @RequestMapping(value = "/crmQuery", method = RequestMethod.GET)
    public String crm(HttpServletRequest request, HttpServletResponse response) {
        try {
            //客户id   资产号码

            Map<String, Object> parameterMap = HttpUtil.getRequestMap(request);
            //
            String custId= (String) parameterMap.get("custId");
            String number= (String) parameterMap.get("number");
            Map<String, Object> eventExtMap=new HashMap<>();
            Map<String, Object> headMap=headMap();
            System.out.println(headMap);
            //具体要查的参数
            Map<String, Object> paramMap=new HashMap<>();
            if(!StringUtils.isBlank(custId)){
                //通过客户id查
                paramMap.put("paramType","ACCOUNTNUMBER");
                paramMap.put("paramValue",custId);
            }else if(!StringUtils.isBlank(number)){
                //通过资产号码查
                paramMap.put("paramType","SERVICEID");
                paramMap.put("paramValue",number);
            }else{
                return "查询条件不存在";
            }
            paramMap.put("needInactiveFlg",false);
            paramMap.put("cityName","");
            Map<String, Object> stringObjectMap = assetQueryService.assetQueryList(headMap, paramMap, eventExtMap);
            System.out.println("请求返回："+stringObjectMap);
            return JSON.toJSONString(stringObjectMap, SerializerFeature.WriteMapNullValue);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    @Autowired(required = false)
    private IContactTaskService iContactTaskService;

    @CrossOrigin
    @RequestMapping(value = "/task", method = RequestMethod.GET)
    public String task(HttpServletRequest request, HttpServletResponse response) {
        try {
            //客户id   资产号码
            Map<String, Object> parameterMap = HttpUtil.getRequestMap(request);
            String taskId= (String) parameterMap.get("taskId");
            Map<String,String> stringObjectMap=new HashMap<>();
            stringObjectMap.put("contactTaskId",taskId);
            Map<String, String> stringObjectMap1 = iContactTaskService.queryTaskDetail4openApi(stringObjectMap);

            System.out.println("请求返回："+stringObjectMap1);
            return JSON.toJSONString(stringObjectMap1, SerializerFeature.WriteMapNullValue);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }



    public static Map<String, Object> headMap(){
        Map<String, Object> headMap=new HashMap<>();
        String config="{\n" +
                "\"isOpen\":\"1\",\n" +
                "\"channel\":\"CLZX\",\n" +
                "\"channel_token\":\"ZZ2hhmTCbnBAl3XS\",\n" +
                "\"bis_module\":\"内容策略中心\",\n" +
                "\"bis_detail\":\"内容策略中心\",\n" +
                "\"version\":\"v1.0\"\t\n" +
                "}";
        System.out.println("门户参数："+config);
        //获取统一平台配置参数
        JSONObject json= JSONObject.parseObject(config);
        headMap.put("channel",json.get("channel"));
        //渠道秘钥+ channel +年月日（20160101）做MD5（32位大写）加密。
        String token=json.getString("channel_token")+json.getString("channel")+ DateUtil.getNowTime();
        headMap.put("channel_token", MD5Util.encodePassword(token).toUpperCase());
        headMap.put("bis_module",json.get("bis_module"));
        headMap.put("bis_detail",json.get("bis_detail"));
        headMap.put("version",json.get("version"));
        return  headMap;

    }





}
