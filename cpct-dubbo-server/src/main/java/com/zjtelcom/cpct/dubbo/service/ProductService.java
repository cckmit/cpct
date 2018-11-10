package com.zjtelcom.cpct.dubbo.service;

import java.util.Map;

public interface ProductService {

    //销售品关联活动查询
    Map<String,Object> selectProductCam(Map<String,Object> paramMap);
}
