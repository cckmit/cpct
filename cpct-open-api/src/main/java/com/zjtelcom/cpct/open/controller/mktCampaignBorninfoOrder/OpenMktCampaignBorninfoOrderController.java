package com.zjtelcom.cpct.open.controller.mktCampaignBorninfoOrder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.open.base.controller.BaseController;
import com.zjtelcom.cpct.open.entity.mktCampaignBorninfoOrder.CompleteMktCampaignBorninfoOrderDetailJtReq;
import com.zjtelcom.cpct.open.service.mktCampaignBorninfoOrder.OpenMktCampaignBorninfoOrderService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("${openPath}")
public class OpenMktCampaignBorninfoOrderController extends BaseController {

    @Autowired
    private OpenMktCampaignBorninfoOrderService openMktCampaignBorninfoOrderService;

    /**
     * 营服活动许可证申请反馈
     */
    @CrossOrigin
    @RequestMapping(value = "/completeMktCampaignBorninfoOrder", method = RequestMethod.POST)
    public String completeMktCampaignBorninfoOrder(@RequestBody Map<String,Object> param, HttpServletResponse response) {
        logger.info("营服活动许可证申请反馈入参：",param);
        CompleteMktCampaignBorninfoOrderDetailJtReq requestObject = JSON.parseObject(JSON.toJSONString(param.get("requestObject")),CompleteMktCampaignBorninfoOrderDetailJtReq.class);
        Map<String, Object> resultMap = openMktCampaignBorninfoOrderService.completeMktCampaignBorninfoOrder(requestObject);
        response.setStatus(HttpStatus.SC_CREATED);
        return JSON.toJSONString(resultMap, SerializerFeature.WriteMapNullValue);
    }
}
