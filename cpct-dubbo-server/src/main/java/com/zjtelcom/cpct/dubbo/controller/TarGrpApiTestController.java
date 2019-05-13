package com.zjtelcom.cpct.dubbo.controller;


import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.dubbo.model.RetCamResp;
import com.zjtelcom.cpct.dubbo.service.TarGrpApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}/tarGrpTest")
public class TarGrpApiTestController {

    @Autowired(required = false)
    private TarGrpApiService tarGrpApiService;

    /**
     * 客户分群接口
     * @param paramsMap
     * @return
     */
    @RequestMapping(value = "/getCpcTargrp", method = RequestMethod.POST)
    @CrossOrigin
    public String qryMktCampaignDetail(@RequestBody Map<String, Object> paramsMap) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            resultMap = tarGrpApiService.getCpcTargrp(paramsMap);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return JSON.toJSONString(resultMap);
    }
}
