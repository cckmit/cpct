package com.zjtelcom.cpct.controller.campaign;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.campaign.MktDttsLog;
import com.zjtelcom.cpct.service.campaign.MktDttsLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@RestController
@RequestMapping("${adminPath}/dtts")
public class MktDttsLogController extends BaseController {

    @Autowired
    private MktDttsLogService mktDttsLogService;

    /**
     * 新增定时任务日志
     * @param
     * @return
     */
    @RequestMapping(value = "saveMktDttsLog", method = RequestMethod.POST)
    @CrossOrigin
    public Map<String,Object> saveMktDttsLog(String dttsType , String dttsState, Date beginTime, Date endTime, String dttsResult,String remark) {
        Map<String,Object> result = new HashMap<>();
        try{
            result = mktDttsLogService.saveMktDttsLog(dttsType ,dttsState,beginTime,endTime,dttsResult,remark);
        }catch (Exception e) {
            logger.error("[op:MktDttsLogController] fail to saveMktDttsLog",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to saveMktDttsLog");
            return result;
        }
        return result;
    }

    /**
     * 编辑定时任务日志
     * @param mktDttsLog
     * @return
     */
    @RequestMapping(value = "updateMktDttsLog", method = RequestMethod.POST)
    @CrossOrigin
    public Map<String,Object> updateMktDttsLog(@RequestBody MktDttsLog mktDttsLog) {
        Map<String,Object> result = new HashMap<>();
        try{
            result = mktDttsLogService.updateMktDttsLog(mktDttsLog);
        }catch (Exception e) {
            logger.error("[op:MktDttsLogController] fail to updateMktDttsLog",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to updateMktDttsLog");
            return result;
        }
        return result;
    }

    /**
     * 定时任务详情
     * @param dttsLogId
     * @return
     */
    @RequestMapping(value = "getMktDttsLog", method = RequestMethod.POST)
    @CrossOrigin
    public Map<String,Object> getMktDttsLog(Long dttsLogId) {
        Map<String,Object> result = new HashMap<>();
        try{
            result = mktDttsLogService.getMktDttsLog(dttsLogId);
        }catch (Exception e) {
            logger.error("[op:MktDttsLogController] fail to getMktDttsLog",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getMktDttsLog");
            return result;
        }
        return result;
    }

    @RequestMapping(value = "getMktDttsLogList", method = RequestMethod.POST)
    @CrossOrigin
    public Map<String,Object> getMktDttsLogList(@RequestBody Map<String, Object> params) {
        Map<String,Object> result = new HashMap<>();
        try{
            result = mktDttsLogService.getMktDttsLogList(params);
        }catch (Exception e) {
            logger.error("[op:MktDttsLogController] fail to getMktDttsLogList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getMktDttsLogList");
            return result;
        }
        return result;
    }
}
