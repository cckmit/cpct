package com.zjtelcom.cpct.dubbo.controller;


import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.dubbo.model.RetCamResp;
import com.zjtelcom.cpct.dubbo.service.TarGrpApiService;
import com.zjtelcom.cpct.service.grouping.TrialOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}/tarGrpTest")
public class TarGrpApiTestController {

    @Autowired(required = false)
    private TarGrpApiService tarGrpApiService;

    @Autowired(required = false)
    private TrialOperationService operationService;

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

    @PostMapping("importUserList4File")
    @CrossOrigin
    public Map<String,Object> importUserList4File (MultipartFile file){
        Map<String, Object> result = new HashMap<>();
        try {
            result = operationService.importUserList4File(file);
        } catch (Exception e) {
        }
        return result;
    }

}
