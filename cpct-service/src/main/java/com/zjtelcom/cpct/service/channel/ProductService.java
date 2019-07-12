package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.dto.channel.ProductParam;

import java.util.List;
import java.util.Map;

public interface ProductService {

    Map<String,Object> getProductNameById(Long userId,List<Long> productIdList);

    Map<String,Object>  getProductList(Long userId,Map<String,Object> params);

    Map<String,Object> addProductRule(ProductParam param);

    Map<String,Object> editProductRule(Long userId,Long ruleId,String remark,Long priority);

    Map<String,Object> delProductRule(Long campaignId,Long ruleId);

    Map<String,Object> getProductRuleList(Long userId,List<Long> ruleIdList);

    Map<String,Object> copyProductRule(Long userId,List<Long> ItemIdList);

    Map<String,Object> getProductListByName( Map<String,Object> params);

    Map<String,Object> getProductRuleListByCampaign(ProductParam param);

    Map<String,Object> copyItemByCampaignPublish(Long oldCampaignId,Long newCampaignId, String mktCampaignCategory);

    Map<String, Object> copyItemByCampaign(Long oldCampaignId, Long newCampaignId);


}
