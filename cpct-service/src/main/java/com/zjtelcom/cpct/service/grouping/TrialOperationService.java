package com.zjtelcom.cpct.service.grouping;

import com.sun.corba.se.spi.ior.ObjectKey;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.dto.grouping.IssueTrialRequest;
import com.zjtelcom.cpct.dto.grouping.TrialOperationVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface TrialOperationService  {
    Map<String,Object> createTrialOperation(TrialOperationVO operationVO);

/*    Map<String,Object> sampleFromES(TrialOperationVO operationVO);*/

    Map<String, Object> getTrialListByStrategyId(Long strategyId);

    Map<String,Object> findBatchHitsList(Long batchId);

    Map<String,Object> issueTrialResult(TrialOperation trialOperation);

    Map<String,Object> importUserList(MultipartFile multipartFile ,TrialOperationVO operation, Long ruleId ) throws IOException;




}
