package com.zjtelcom.cpct.dubbo.out;

import java.util.Map;

public interface TrialStatusUpService {

    Map<String,Object> updateOperationStatus(Map<String,Object> params);

    Map<String,Object> campaignIndexTask(Map<String, Object> param);

    Map<String,Object> cpcLog2WriteFileLabel();

    Map<String, Object> dueMktCampaign();
}
