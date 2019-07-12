///**
// * @(#)MktCampaignJtController.java, 2018/8/7.
// * <p/>
// * Copyright 2018 Netease, Inc. All rights reserved.
// * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
// */
//package com.zjtelcom.cpct.controller.cpct;
//
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import com.zjtelcom.cpct.controller.campaign.CampaignController;
//import com.zjtelcom.cpct.dto.pojo.CpcGroupRequest;
//import com.zjtelcom.cpct.dto.pojo.CpcGroupResponse;
//import com.zjtelcom.cpct.dto.pojo.Result;
//import com.zjtelcom.cpct.pojo.MktCampaignDetailReq;
//import com.zjtelcom.cpct.service.cpct.MktCampaignJTService;
//import com.zjtelcom.cpct.util.CpcUtil;
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
///**
// * Description:
// * author: linchao
// * date: 2018/08/07 10:36
// * version: V1.0
// */
//@RestController
//@RequestMapping("${adminPath}/group/cpc/service")
//public class MktCampaignJtController {
//    private static final Logger LOG = Logger.getLogger(CampaignController.class);
//
//    @Autowired
//    private MktCampaignJTService mktCampaignJTService;
//
//    @RequestMapping("/createMktCampaignJt")
//    @CrossOrigin
//    public CpcGroupResponse createMktCampaignJt(@RequestBody CpcGroupRequest<MktCampaignDetailReq> request) {
//        LOG.info("createMktCampaignJt param is " + (null == request ? " null " : JSONObject.toJSONString(request, SerializerFeature.WriteMapNullValue)));
//        String transactionId = request.getContractRoot().getTcpCont().getTransactionId();
//        String sign = request.getContractRoot().getTcpCont().getSign();
//        try {
//            MktCampaignDetailReq requestObject = request.getContractRoot().getSvcCont().getRequestObject();
//            requestObject.setTransactionId(transactionId);
//            Result result = mktCampaignJTService.saveBatch(requestObject);
//
//            return CpcUtil.buildResponse(result, sign, transactionId);
//        } catch (Exception e) {
//            LOG.error("transactionId: " + transactionId, e);
//            return CpcUtil.buildErrorResponse(e, sign, transactionId);
//        }
//    }
//
//    @RequestMapping(value = "/modMktCampaignJt", method = RequestMethod.POST)
//    @ResponseBody
//    public CpcGroupResponse modMktCampaignJt(@RequestBody CpcGroupRequest<MktCampaignDetailReq> request) {
//        LOG.info("modMktCampaignJt param is " + (null == request ? " null " : JSONObject.toJSONString(request, SerializerFeature.WriteMapNullValue)));
//        String transactionId = request.getContractRoot().getTcpCont().getTransactionId();
//        String sign = request.getContractRoot().getTcpCont().getSign();
//        try {
//            MktCampaignDetailReq requestObject = request.getContractRoot().getSvcCont().getRequestObject();
//            requestObject.setTransactionId(transactionId);
//            Result result = mktCampaignJTService.updateBatch(requestObject);
//            return CpcUtil.buildResponse(result, sign, transactionId);
//        } catch (Exception e) {
//            LOG.error("transactionId: " + transactionId, e);
//            return CpcUtil.buildErrorResponse(e, sign, transactionId);
//        }
//    }
//}