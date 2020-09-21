package com.zjtelcom.cpct.service.impl.campaign;

import com.zjtelcom.cpct.dao.campaign.BatchSendMapper;
import com.zjtelcom.cpct.dao.grouping.TrialOperationMapper;
import com.zjtelcom.cpct.domain.campaign.BatchSendDO;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.dto.grouping.TrialOperationVO;
import com.zjtelcom.cpct.service.campaign.BatchSendService;
import com.zjtelcom.cpct.service.es.EsHitsService;
import com.zjtelcom.es.es.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BatchSendServiceImpl implements BatchSendService {
    @Autowired
    private  TrialOperationMapper trialOperationMapper;
    @Autowired
    private BatchSendMapper batchSendMapper;
    @Autowired(required = false)
    private  EsService esService;

    /*获取批次列表*/
    @Override
    public List<BatchSendDO> selectByBatchTrialId(Long trialId) {
        TrialOperation trialOperation = trialOperationMapper.selectByPrimaryKey(trialId);
        Long batchNum = trialOperation.getBatchNum();
        List<BatchSendDO> batchSendDOList = batchSendMapper.selectByBatchNum(batchNum.toString());
        return batchSendDOList;
    }

    /*小文件分批下发*/
    @Override
    public boolean uploadFile2ProdBatch(Long batchId) {
        Map<String ,Object> batchParams = new HashMap<>();
        batchParams.put("batchId",batchId);
        boolean isSuccess = esService.uploadFile2ProdBatch(batchParams);
        return isSuccess;
    }


}
