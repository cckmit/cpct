package com.zjtelcom.cpct.controller.cpct;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.pojo.CpcGroupRequest;
import com.zjtelcom.cpct.dto.pojo.CpcGroupResponse;
import com.zjtelcom.cpct.dto.pojo.EventPo;
import com.zjtelcom.cpct.service.cpct.CpctEventService;
import com.zjtelcom.cpct.util.CpcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${adminPath}/cpctEvent")
public class CpctEventController extends BaseController {

    @Autowired
    private CpctEventService cpctEventService;

    @RequestMapping("createContactEvtJt")
    @CrossOrigin
    public CpcGroupResponse createContactEvtJt(@RequestBody CpcGroupRequest<EventPo> object) {
        logger.info("createContactEvtJt param is " + (null == object ? " null " : JSONObject.toJSONString(object, SerializerFeature.WriteMapNullValue)));
        CpcGroupResponse cpcGroupResponse = new CpcGroupResponse();
        try {
            cpcGroupResponse = cpctEventService.createContactEvtJt(object);
        } catch (Exception e) {
            logger.error("AddEvent error.",e);
            cpcGroupResponse = CpcUtil.buildErrorResponse(object);
        }
        return cpcGroupResponse;
    }

    @RequestMapping("modContactEvtJt")
    @CrossOrigin
    public CpcGroupResponse modContactEvtJt(
            @RequestBody CpcGroupRequest<EventPo> object) {
        CpcGroupResponse cpcGroupResponse = new CpcGroupResponse();
        logger.info("modContactEvtJt param is " + (null == object ? " null " : JSONObject.toJSONString(object, SerializerFeature.WriteMapNullValue)));
        try {
            cpcGroupResponse = cpctEventService.modContactEvtJt(object);
        } catch (Exception e) {
            logger.error("ModEvent error.",e);
            cpcGroupResponse = CpcUtil.buildErrorResponse(object);
        }
        return cpcGroupResponse;
    }



}
