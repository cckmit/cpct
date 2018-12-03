package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.channel.ResourceService;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@RestController
@RequestMapping("${adminPath}/resource")
public class ResourceController extends BaseController{

    @Autowired
    private ResourceService resourceService;

    /*
    *促销券模糊查询
    */
    @PostMapping("getResourceListByName")
    @CrossOrigin
    public Map<String, Object> getResourceListByName(@RequestBody HashMap<String,Object> params) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = resourceService.getResourceListByName(params);
        }catch (Exception e){
            logger.error("[op:ResourceController] fail to getResourceListByName",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getResourceList");
            return result;
        }
        return result;
    }
}
