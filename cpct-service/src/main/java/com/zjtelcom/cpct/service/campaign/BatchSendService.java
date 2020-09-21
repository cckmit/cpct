package com.zjtelcom.cpct.service.campaign;

import com.zjtelcom.cpct.domain.campaign.BatchSendDO;

import java.util.List;

public interface BatchSendService {
    //获取批次下发列表
    List<BatchSendDO> selectByBatchTrialId(Long trialId);
    //下发
    boolean uploadFile2ProdBatch(Long batchId);

}
