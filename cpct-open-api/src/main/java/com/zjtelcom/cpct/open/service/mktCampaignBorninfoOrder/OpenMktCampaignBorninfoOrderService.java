package com.zjtelcom.cpct.open.service.mktCampaignBorninfoOrder;

import com.zjtelcom.cpct.open.entity.mktCampaignBorninfoOrder.CompleteMktCampaignBorninfoOrderDetailJtReq;

import java.util.Map;

public interface OpenMktCampaignBorninfoOrderService {

    Map<String, Object> completeMktCampaignBorninfoOrder(CompleteMktCampaignBorninfoOrderDetailJtReq requestObject);
}
