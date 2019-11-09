package com.zjtelcom.cpct.dubbo.out;

import java.io.IOException;
import java.util.Map;

public interface TrialStatusUpService {

    Map<String,Object> updateOperationStatus(Map<String,Object> params);

    Map<String,Object> campaignIndexTask(Map<String, Object> param);

    Map<String,Object> cpcLog2WriteFileLabel();

    Map<String, Object> dueMktCampaign();

    //xyl excel文件清单批量导入
    Map<String,Object> importUserListByExcel() throws IOException;

    //xyl 销售品下架发送短信
    Map<String,Object> sendMsgByOfferOver();
}
