package com.zjtelcom.cpct.controller.grouping;


import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.dto.grouping.TrialOperationVO;
import com.zjtelcom.cpct.service.grouping.TrialOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
     * 抽样业务校验
     * @param operation
     * @return
     */
    @PostMapping("businessCheck")
    @CrossOrigin
    public Map<String, Object> businessCheck(@RequestBody TrialOperationVO operation){
        Map<String, Object> result = new HashMap<>();
        try {
            result = operationService.businessCheck(operation);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to businessCheck", e);
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", " fail to businessCheck");
            return result;
        }
        return result;
    }

    /**
     * 客户清单导入试运算
     * @param multipartFile
     * @param operation
     * @param ruleId
     * @return
     * @throws IOException
     */
    @PostMapping("importUserList")
    @CrossOrigin
    public Map<String, Object> importUserList(MultipartFile multipartFile, TrialOperationVO operation, Long ruleId)throws IOException{
        Map<String, Object> result = new HashMap<>();
        try {
            result = operationService.importUserList(multipartFile,operation,ruleId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to importUserList", e);
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", " fail to importUserList");
            return result;
        }
        return result;
    }

    /**
     * 策略试运算统计查询
     * @return
     */
    @PostMapping("searchCountInfo")
    @CrossOrigin
    public Map<String, Object> searchCountInfo(@RequestBody HashMap<String, Long> param) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = operationService.searchCountInfo(param.get("batchId"));
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to searchCountInfo", e);
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "fail to searchCountInfo");
            return result;
        }
        return result;
    }

    /**
     * 策略试运算区域统计查询
     * @return
     */
    @PostMapping("searchCountAllByArea")
    @CrossOrigin
    public Map<String, Object> searchCountAllByArea(@RequestBody HashMap<String, Long> param) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = operationService.searchCountAllByArea(param.get("batchId"));
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to searchCountAllByArea", e);
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "fail to searchCountAllByArea");
            return result;
        }
        return result;
    }

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
            result.put("resultMsg", "fail to searchBatchInfo");
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
