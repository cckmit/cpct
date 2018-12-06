package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.controller.BaseController;
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
    *服务模糊查询
     */
    @PostMapping("getServiceListByName")
    @CrossOrigin
    public Map<String, Object> getServiceListByName(@RequestBody HashMap<String,Object> params) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = serviceService.getServiceListByName(params);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to getServiceListByName",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getServiceList");
            return result;
        }
        return result;
    }
}
