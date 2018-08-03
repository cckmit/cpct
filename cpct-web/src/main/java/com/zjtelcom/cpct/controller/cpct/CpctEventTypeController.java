package com.zjtelcom.cpct.controller.cpct;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.pojo.CatalogDetailPo;
import com.zjtelcom.cpct.dto.pojo.CpcGroupRequest;
import com.zjtelcom.cpct.dto.pojo.CpcGroupResponse;
import com.zjtelcom.cpct.dto.pojo.EventPo;
import com.zjtelcom.cpct.service.cpct.CpctEventTypeService;
import com.zjtelcom.cpct.util.CpcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${adminPath}/cpctEventType")
public class CpctEventTypeController extends BaseController {

    @Autowired
    private CpctEventTypeService eventTypeService;

    @PostMapping("createEventCatalogJt")
    @CrossOrigin
    public CpcGroupResponse createEventCatalogJt(@RequestBody  CpcGroupRequest<CatalogDetailPo> object) {
        logger.info("createContactEvtJt param is " + (null == object ? " null " : JSONObject.toJSONString(object, SerializerFeature.WriteMapNullValue)));
        CpcGroupResponse cpcGroupResponse = new CpcGroupResponse();
        try {
            cpcGroupResponse = eventTypeService.createEventCatalogJt(object);
        } catch (Exception e) {
            logger.error("AddEvent error.",e);
            cpcGroupResponse = CpcUtil.buildErrorResponse(object,e);
        }
        return cpcGroupResponse;
    }

    @PostMapping("modEventCatalogJtReq")
    @CrossOrigin
    public CpcGroupResponse modEventCatalogJtReq(@RequestBody CpcGroupRequest<CatalogDetailPo> object) {
        CpcGroupResponse cpcGroupResponse = new CpcGroupResponse();
        logger.info("modContactEvtJt param is " + (null == object ? " null " : JSONObject.toJSONString(object, SerializerFeature.WriteMapNullValue)));
        try {
            cpcGroupResponse = eventTypeService.modEventCatalogJtReq(object);
        } catch (Exception e) {
            logger.error("ModEvent error.",e);
            cpcGroupResponse = CpcUtil.buildErrorResponse(object,e);
        }
        return cpcGroupResponse;
    }


}
