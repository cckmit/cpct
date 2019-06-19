package com.zjtelcom.cpct.dubbo.service;

import java.util.List;
import java.util.Map;

public interface ProductService {

    //销售品关联活动查询
    Map<String,Object> selectProductCam(List<Map<String,Object>> paramList);

    Map<String,Object> getCloseCampaign(Map<String, Object> paramMap);
}
