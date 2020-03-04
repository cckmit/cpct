package com.zjtelcom.cpct.dubbo.out.impl;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.dubbo.out.EsSearchService;
import com.zjtelcom.es.es.service.EsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class EsSearchServiceImpl implements EsSearchService {

    @Autowired(required = false)
    private EsService esService;


    /**
     * 客户id查询资产对外
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> queryCustomerByCcustId4Out(Map<String,String> param) {
        log.info("【对外】客户id查询开始："+JSON.toJSONString(param));
        Map<String, Object> map = null;
        try {
            map = esService.queryCustomerByCustId(param);
        } catch (Exception e) {
            log.info("【对外】客户id查询错误"+e.getMessage());
            e.printStackTrace();
        }
        return map;
    }
}
