package com.zjtelcom.cpct.controller.grouping;


import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.grouping.TrialOperationVO;
import com.zjtelcom.cpct.service.grouping.TrialOperationService;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;


/**
 * @Description 策略试运算Controller
 * @Author hyf
 * @Date 2018/6/20 17:55
 */
@RestController
@RequestMapping("${adminPath}/trialOperation")
public class TrialOperationController extends BaseController {
    @Autowired
    private TrialOperationService operationService;


    /**
     * 策略试运算
     * @param operationVO
     * @return
     */
    @PostMapping("searchBatchInfo")
    @CrossOrigin
    public Map<String, Object> searchBatchInfo(@RequestBody TrialOperationVO operationVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = operationService.searchBatchInfo(operationVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to searchBatchInfo",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to searchBatchInfo");
            return result;
        }
        return result;

    }

    /**
     * 点击抽样试算
     * @param operationVO
     * @return
     */
    @PostMapping("sampleFromES")
    @CrossOrigin
    public Map<String, Object> sampleFromES(@RequestBody TrialOperationVO operationVO)  {
        Map<String,Object> result = new HashMap<>();
        try {
            result = operationService.sampleFromES(operationVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to sampleFromES",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to sampleFromES");
            return result;
        }
        return result;
    }


    /**
     * 刷新列表
     * @param strategyId
     * @return
     */
    @PostMapping("getTrialListByStrategyId")
    @CrossOrigin
    public Map<String, Object> getTrialListByStrategyId(Long strategyId){
        Map<String,Object> result = new HashMap<>();
        try {
            result = operationService.getTrialListByStrategyId(strategyId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getTrialListByStrategyId",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getTrialListByStrategyId");
            return result;
        }
        return result;

    }



}
