package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.domain.channel.PpmProduct;

import java.util.List;
import java.util.Map;

public interface ProductService {

    Map<String,Object> getProductNameById(Long userId,List<Long> productIdList);

    List<PpmProduct>  getProductList(Long userId,String productName);

    Map<String,Object> addProductRule(Long userId,List<Long> productIdList);

    Map<String,Object> editProductRule(Long userId,Long ruleId,String remark);

    Map<String,Object> delProductRule(Long userId,Long ruleId);

    Map<String,Object> getProductRuleList(Long userId,List<Long> ruleIdList);




}
