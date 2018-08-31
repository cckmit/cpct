package com.zjtelcom.cpct.controller.grouping;


import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.dto.grouping.IssueTrialRequest;
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
     * 新增策略试运算
     * @param operationVO
     * @return
     */
    @PostMapping("createTrialOperation")
    @CrossOrigin
    public Map<String, Object> createTrialOperation(@RequestBody TrialOperationVO operationVO) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = operationService.createTrialOperation(operationVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to searchBatchInfo", e);
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", " fail to searchBatchInfo");
            return result;
        }
        return result;
    }

    /**
     * redis查询抽样试算结果清单
     *
     * @param param
     * @return
     */
    @PostMapping("findBatchHitsList")
    @CrossOrigin
    public Map<String, Object> findBatchHitsList(@RequestBody HashMap<String, Long> param) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = operationService.findBatchHitsList(param.get("batchId"));
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to findBatchHitsList", e);
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", " fail to findBatchHitsList");
            return result;
        }
        return result;
    }

    /**
     * 策略试运算下发
     *
     * @param trialOperation
     * @return
     */
    @PostMapping("issueTrialResult")
    @CrossOrigin
    public Map<String, Object> issueTrialResult(@RequestBody TrialOperation trialOperation) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = operationService.issueTrialResult(trialOperation);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to issueTrialResult", e);
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", " fail to issueTrialResult");
            return result;
        }
        return result;
    }

    /**
     * 刷新列表
     *
     * @param
     * @return
     */
    @PostMapping("getTrialListByStrategyId")
    @CrossOrigin
    public Map<String, Object> getTrialListByStrategyId(@RequestBody HashMap<String, Long> param) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = operationService.getTrialListByStrategyId(param.get("strategyId"));
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getTrialListByStrategyId", e);
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", " fail to getTrialListByStrategyId");
            return result;
        }
        return result;

    }



}
