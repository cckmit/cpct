package com.zjtelcom.cpct.statistic.controller;


import com.zjtelcom.cpct.statistic.service.TrialLabelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@RestController
@RequestMapping("${adminPath}/analyst")//"${adminPath}
public class AnalystController {

    protected Logger logger = LoggerFactory.getLogger(AnalystController.class);

    @Autowired
    private TrialLabelService trialLabelService;
    /*
     *es集群3 全量试算后统计分析
     */
    @PostMapping("/statisticalAnalysts")
    @CrossOrigin
    public Map<String, Object> statisticalAnalysts(@RequestBody HashMap<String, Object> params) {
        Map<String ,Object> result = new HashMap<>();
        try {
            result = trialLabelService.statisticalAnalysts(params);
        }catch (Exception e){
            logger.error("[op:AnalystController] fail to statisticalAnalysts",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to statisticalAnalysts");
            return result;
        }
        return result;
    }
}
