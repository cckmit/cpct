package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.channel.ServiceEntity;
import com.zjtelcom.cpct.service.channel.ServiceService;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@RestController
@RequestMapping("${adminPath}/service")
public class ServiceController extends BaseController{

    @Autowired
    private ServiceService serviceService;

    /*
    **服务模糊查询
     */
    @PostMapping("getServiceListByName")
    @CrossOrigin
    public Map<String, Object> getServiceListByName(@RequestBody HashMap<String,Object> params) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = serviceService.getServiceListByName(userId, params);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to getServiceListByName",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getServiceListByName");
            return result;
        }
        return result;
    }

    /*
    **获取服务列表
     */
    @PostMapping("getServiceList")
    @CrossOrigin
    public Map<String, Object> getServiceList(@RequestBody Map<String,Object> params) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = serviceService.getServiceList(userId, params);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to getServiceList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getServiceList");
            return result;
        }
        return result;
    }

    /*
    **创建服务
     */
    @PostMapping("createService")
    @CrossOrigin
    public Map<String, Object> createService(@RequestBody ServiceEntity serviceEntity) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = serviceService.createService(userId, serviceEntity);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to createService",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to createService");
            return result;
        }
        return result;
    }

    /*
    **编辑服务
     */
    @PostMapping("editService")
    @CrossOrigin
    public Map<String, Object> editService(@RequestBody ServiceEntity serviceEntity) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = serviceService.modService(userId, serviceEntity);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to editService",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to editService");
            return result;
        }
        return result;
    }

    /*
    **删除服务
     */
    @PostMapping("delService")
    @CrossOrigin
    public Map<String, Object> delService(@RequestBody ServiceEntity serviceEntity) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = serviceService.delService(userId, serviceEntity);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to delService",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to delService");
            return result;
        }
        return result;
    }

    /*
    **获取服务详情
     */
    @PostMapping("getServiceDetail")
    @CrossOrigin
    public Map<String, Object> getServiceDetail(Long serviceId) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = serviceService.getServiceDetail(userId, serviceId);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to getServiceDetail",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getServiceDetail");
            return result;
        }
        return result;
    }

}
