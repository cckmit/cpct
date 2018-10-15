package com.zjtelcom.cpct.controller.campaign;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.campaign.MktOperatorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}/operator")
public class MktOperatorLogController extends BaseController {

    @Autowired
    MktOperatorLogService mktOperatorLogService;

    /**
     * 查询操作记录列表
     *
     * @return
     */
    @RequestMapping(value = "listOperatorLog", method = RequestMethod.POST)
    @CrossOrigin
    public String listOperatorLog(@RequestBody Map<String,String> params) {
        Map result = new HashMap();
        try {
            result = mktOperatorLogService.selectByPrimaryKey(params);
        }catch (Exception e) {
            logger.error("[op:MktOperatorLogController] fail to listOperatorLog Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return JSON.toJSON(result).toString();
    }
}
