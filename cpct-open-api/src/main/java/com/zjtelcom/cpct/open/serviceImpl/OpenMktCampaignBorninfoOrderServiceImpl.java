package com.zjtelcom.cpct.open.serviceImpl;

import com.zjtelcom.cpct.open.base.service.BaseService;
import com.zjtelcom.cpct.open.entity.mktCampaignBorninfoOrder.CompleteMktCampaignBorninfoOrderDetailJtReq;
import com.zjtelcom.cpct.open.service.mktCampaignBorninfoOrder.OpenMktCampaignBorninfoOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class OpenMktCampaignBorninfoOrderServiceImpl extends BaseService implements OpenMktCampaignBorninfoOrderService {

    @Override
    public Map<String, Object> completeMktCampaignBorninfoOrder(CompleteMktCampaignBorninfoOrderDetailJtReq requestObject) {
        Map<String, Object> result = new HashMap<>();
        result.put("resultCode","0");
        result.put("resultMsg", "处理成功");
        return result;
    }
}
