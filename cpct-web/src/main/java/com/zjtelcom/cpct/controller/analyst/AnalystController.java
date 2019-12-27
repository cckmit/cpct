package com.zjtelcom.cpct.controller.analyst;

import com.zjtelcom.cpct.service.analyst.AnalystService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@RestController
@RequestMapping("${adminPath}/analyst")
public class AnalystController {

    protected Logger logger = LoggerFactory.getLogger(AnalystController.class);

    @Autowired(required = false)
    private AnalystService analystService;
    /*
     *es集群3 全量试算后统计分析
     */
    @PostMapping("/statisticalAnalysts")
    @CrossOrigin
    public Map<String, Object> statisticalAnalysts(@RequestBody HashMap<String, Object> params) {
        Map<String ,Object> result = new HashMap<>();
        try {
            result = analystService.statisticalAnalysts(params);
        }catch (Exception e){
            logger.error("[op:AnalystController] fail to statisticalAnalysts",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to statisticalAnalysts");
            return result;
        }
        return result;
    }


    /**
     * 查询自定义标签
     * @param name
     * @return
     */
    @PostMapping("/getCustomByLabel")
    @CrossOrigin
    public Map<String, Object> getCustomByLabel(String name) {
        Map<String ,Object> result = new HashMap<>();
        try {
            result = analystService.getCustomByLabel(name);
        }catch (Exception e){
            logger.error("[op:AnalystController] fail to getCustomByLabel",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getCustomByLabel");
            return result;
        }
        return result;
    }

}
