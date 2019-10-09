package com.zjtelcom.cpct.service.campaign;

import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/07/17 11:11
 * version: V1.0
 */

public interface MktCampaignApiService {

    Map<String, Object> qryMktCampaignDetail(Long mktCampaignId) throws Exception;

    //销售品下架 遍历活动销售品下架 发短信给活动创建人 xyl
    Map<String,Object> salesOffShelf(Map<String,Object> map);
}
