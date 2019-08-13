package com.zjtelcom.cpct.controller.grouping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.service.grouping.ServicePackageService;
import com.zjtelcom.cpct.util.FastJsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/08/12 11:15
 * @version: V1.0
 */
@RestController
@RequestMapping("${adminPath}/servicepackage")
public class ServicePackageController {

    public static final Logger logger = LoggerFactory.getLogger(ServicePackageController.class);

    @Autowired
    private ServicePackageService servicePackageService;


    @RequestMapping("/saveServicePackage")
    @CrossOrigin
    public String saveServicePackage(String servicePackageName, MultipartFile multipartFile) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = servicePackageService.saveServicePackage(servicePackageName, multipartFile);
        } catch (Exception e) {
            logger.error("[op:ServicePackageController] fail to saveServicePackage name = {}, multipartFile = {} !" +
                    " Exception: ", servicePackageName, JSON.toJSONString(multipartFile), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }




    @RequestMapping("/getServicePackageList")
    @CrossOrigin
    public String getServicePackageList(@RequestBody Map<String,Object> params) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = servicePackageService.getServicePackageList(params);
        } catch (Exception e) {
            logger.error("[op:ServicePackageController] fail to getServicePackageList = {}!" +
                    " Exception: ", JSONArray.toJSON(maps), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }

    @RequestMapping("/deleteServicePackage")
    @CrossOrigin
    public String deleteServicePackage(@RequestBody Map<String,Object> params) {
        Map<String, Object> maps = new HashMap<>();
        try {
            Long servicePackageId = Long.valueOf(params.get("servicePackageId").toString());
            maps = servicePackageService.deleteServicePackage(servicePackageId);
        } catch (Exception e) {
            logger.error("[op:ServicePackageController] fail to selectByName by servicePackageName = {}!" +
                    " Exception: ", JSONArray.toJSON(maps), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }


    @RequestMapping("/selectByName")
    @CrossOrigin
    public String selectByName(@RequestBody Map<String,Object> params) {
        Map<String, Object> maps = new HashMap<>();
        try {
            String servicePackageName= (String) params.get("servicePackageName");
            maps = servicePackageService.selectByName(servicePackageName);
        } catch (Exception e) {
            logger.error("[op:ServicePackageController] fail to selectByName by servicePackageName = {}!" +
                    " Exception: ", JSONArray.toJSON(maps), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }
}