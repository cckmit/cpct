package com.zjtelcom.cpct.controller.cpct;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.pojo.CpcGroupRequest;
import com.zjtelcom.cpct.dto.pojo.CpcGroupResponse;
import com.zjtelcom.cpct.dto.pojo.EventScenePo;
import com.zjtelcom.cpct.service.cpct.CpctEventSceneService;
import com.zjtelcom.cpct.util.CpcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${adminPath}/cpctEventScene")
public class CpctEventSceneController extends BaseController {

    @Autowired
    private CpctEventSceneService eventSceneService;



    @PostMapping( "/createEventSceneJt")
    @CrossOrigin
    public CpcGroupResponse createEventSceneJt(@RequestBody CpcGroupRequest<EventScenePo> cpcGroupRequest){
        logger.info("createEventSceneJt param is " + (null == cpcGroupRequest ? " null " : JSONObject.toJSONString(cpcGroupRequest,SerializerFeature.WriteMapNullValue)));
        CpcGroupResponse cpcGroupResponse = new CpcGroupResponse();
        try {
            cpcGroupResponse = eventSceneService.createEventSceneJt(cpcGroupRequest);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error", e);
            cpcGroupResponse = CpcUtil.buildErrorResponse(cpcGroupRequest,e);
        }
        return cpcGroupResponse;
    }


    @PostMapping( "/modEventSceneJt")
    @CrossOrigin
    public CpcGroupResponse modEventSceneJt(@RequestBody CpcGroupRequest<EventScenePo> cpcGroupRequest){
        CpcGroupResponse cpcGroupResponse = new CpcGroupResponse();
        logger.info("modEventSceneJt param is " + (null == cpcGroupRequest ? " null " : JSONObject.toJSONString(cpcGroupRequest, SerializerFeature.WriteMapNullValue)));
        try {
            cpcGroupResponse = eventSceneService.modEventSceneJt(cpcGroupRequest);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" modEventSceneJt error", e);
            cpcGroupResponse = CpcUtil.buildErrorResponse(cpcGroupRequest,e);
        }
        return cpcGroupResponse;
    }
}
