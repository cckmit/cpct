package com.zjtelcom.cpct.service.grouping;

import com.sun.corba.se.spi.ior.ObjectKey;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.dto.grouping.IssueTrialRequest;
import com.zjtelcom.cpct.dto.grouping.TrialOperationVO;

import java.util.Map;

public interface TrialOperationService  {
    Map<String,Object> createTrialOperation(TrialOperationVO operationVO);

/*    Map<String,Object> sampleFromES(TrialOperationVO operationVO);*/

    Map<String, Object> getTrialListByStrategyId(Long strategyId);

    Map<String,Object> findBatchHitsList(Long batchId);

    Map<String,Object> issueTrialResult(TrialOperation trialOperation);




}
