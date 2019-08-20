package com.zjtelcom.cpct.controller.grouping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.controller.BaseController;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
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
public class ServicePackageController extends BaseController {

    public static final Logger logger = LoggerFactory.getLogger(ServicePackageController.class);

    @Autowired
    private ServicePackageService servicePackageService;


    @RequestMapping("/saveServicePackage")
    @CrossOrigin
    public String saveServicePackage(String servicePackageName, MultipartFile file) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = servicePackageService.saveServicePackage(servicePackageName, file);
        } catch (Exception e) {
            logger.error("[op:ServicePackageController] fail to saveServicePackage name = {}, multipartFile = {} !" +
                    " Exception: ", servicePackageName, JSON.toJSONString(file), e);
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



    /**
     * 下发文件导入模板下载
     */
    @RequestMapping("/downloadServicePackageTemplate")
    @CrossOrigin
    public String downloadServicePackageTemplate(HttpServletRequest request, HttpServletResponse response) {
        OutputStream ouputStream = null;
        try {
            String fileName = "服务包模板.xlsx";

            byte[] buffer = new byte[1024];
            FileInputStream fis = null;  //文件输入流
            BufferedInputStream bis = null;
            fis = new FileInputStream("/app/ServicePackageTemplate.xlsx");
            bis = new BufferedInputStream(fis);

            //处理导出问题
            response.reset();
            response.setContentType(CommonConstant.CONTENTTYPE);
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            ouputStream = response.getOutputStream();

            int len = 0;
            while ((len = bis.read(buffer)) > 0) {
                ouputStream.write(buffer, 0, len);
            }
            int i = bis.read(buffer);
            ouputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ouputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return initSuccRespInfo("导出成功");
    }
}