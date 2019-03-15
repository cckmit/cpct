package com.zjtelcom.cpct.controller.channel;


import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.channel.ServiceEntity;
import com.zjtelcom.cpct.domain.channel.ServiceType;
import com.zjtelcom.cpct.service.channel.ServiceTypeService;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@RestController
@RequestMapping("${adminPath}/serviceType")
public class ServiceTypeController extends BaseController{

    @Autowired
    private ServiceTypeService serviceTypeService;

    /*
    *服务类型列表
    */
    @PostMapping("getServiceTypeList")
    @CrossOrigin
    public Map<String, Object> getServiceTypeList() {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = serviceTypeService.getServiceTypeList();
        }catch (Exception e){
            logger.error("[op:ServiceTypeController] fail to getServiceTypeList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getServiceTypeList");
            return result;
        }
        return result;
    }

    /*
     *创建服务类型
     */
    @PostMapping("createServiceType")
    @CrossOrigin
    public Map<String, Object> createServiceType(@RequestBody ServiceType serviceType) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = serviceTypeService.createServiceType(userId, serviceType);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to createService",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to createService");
            return result;
        }
        return result;
    }

    /*
     **编辑服务类型
     */
    @PostMapping("editServiceType")
    @CrossOrigin
    public Map<String, Object> editServiceType(@RequestBody ServiceType serviceType) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = serviceTypeService.modServiceType(userId, serviceType);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to editService",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to editService");
            return result;
        }
        return result;
    }

    /*
     **删除服务类型
     */
    @PostMapping("delServiceType")
    @CrossOrigin
    public Map<String, Object> delServiceType(@RequestBody ServiceType serviceType) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = serviceTypeService.delServiceType(userId, serviceType);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to delService",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to delService");
            return result;
        }
        return result;
    }

    /*
     **查询服务类型
     */
    @PostMapping("getServiceTypeByCondition")
    @CrossOrigin
    public Map<String, Object> getServiceTypeByCondition(@RequestBody HashMap<String,Object> params) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = serviceTypeService.getServiceTypeByCondition(userId, params);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to getServiceListByName",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getServiceListByName");
            return result;
        }
        return result;
    }

    /*
     **获取服务类型详情
     */
    @PostMapping("getServiceTypeDetail")
    @CrossOrigin
    public Map<String, Object> getServiceDetail(@RequestBody HashMap<String,Long> param) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            Long serviceTypeId = param.get("serviceTypeId");
            result = serviceTypeService.getServiceTypeDetail(userId, serviceTypeId);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to getServiceDetail",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to delService");
            return result;
        }
        return result;
    }



}