package com.zjtelcom.cpct.service.grouping;

import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.dto.grouping.TrialOperationVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface TrialOperationService  {
    Map<String,Object> createTrialOperation(TrialOperationVO operationVO);

/*    Map<String,Object> sampleFromES(TrialOperationVO operationVO);*/

    Map<String, Object> getTrialListByStrategyId(Long strategyId);

    Map<String, Object> getTrialListByRuleId(Long ruleId);

    Map<String,Object> findBatchHitsList(Long batchId);

    Map<String,Object> issueTrialResult(TrialOperation trialOperation);

    Map<String,Object> importUserList(MultipartFile multipartFile , TrialOperationVO operation, Long ruleId ) throws IOException;

    Map<String,Object> businessCheck(TrialOperationVO operationVO);

    Map<String,Object> searchCountInfo(Long batchId);

    Map<String,Object> searchCountAllByArea(Long batchId);

    Map<String,Object> searchCountByLabelList(String labelCodes);

    Map<String,Object> uploadFile(Long batchId);

    Map<String,Object> trialLog(Long batchId);

    Map<String,Object> importFromCust4Ppm(Long batchId);

    Map<String,Object> conditionCheck(Map<String,Object> param);




}
