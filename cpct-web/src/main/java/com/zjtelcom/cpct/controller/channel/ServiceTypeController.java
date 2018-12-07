package com.zjtelcom.cpct.controller.channel;


import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.channel.ServiceTypeService;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}