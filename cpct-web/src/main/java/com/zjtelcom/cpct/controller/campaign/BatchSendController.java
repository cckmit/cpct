package com.zjtelcom.cpct.controller.campaign;

import com.zjtelcom.cpct.domain.campaign.BatchSendDO;
import com.zjtelcom.cpct.service.campaign.BatchSendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@RestController
@RequestMapping("${adminPath}/batchSend")
public class BatchSendController {
    protected Logger logger = LoggerFactory.getLogger(BatchSendController.class);
    @Autowired
    private  BatchSendService batchSendService;
    /*
     **获取下发批次列表
     */
    @PostMapping("getBatchListByTrialId")
    @CrossOrigin
    public Map<String, Object> getMktAlgorithmsList(@RequestBody HashMap<String,Object> params) {
        Map<String ,Object> result = new HashMap<>();
        Integer trialId =(Integer)params.get("trialId");
        try {
            Long trialIdL = Long.parseLong(trialId.toString());
            List<BatchSendDO> batchSendDOList = batchSendService.selectByBatchTrialId(trialIdL);
            result.put("result",batchSendDOList);
        }catch (Exception e){
            logger.error("[op:BatchSendController] fail to getBatchListByTrialId",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getBatchListByTrialId");
            return result;
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","获取批次列表成功");
        return result;
    }

    /*
     **下发文件
     */
    @PostMapping("uploadFile2ProdBatch")
    @CrossOrigin
    public Map<String, Object> uploadFile2ProdBatch(@RequestBody HashMap<String,Object> params) {
        Map<String ,Object> result = new HashMap<>();
        List<Integer> batchIdList =(List)params.get("batchId");
        ArrayList<Boolean> resultList = new ArrayList<>();
        ArrayList<Boolean> falseResultList = new ArrayList<>();
        try {
            for(Integer id: batchIdList){
                Long batchId = Long.parseLong(id.toString());
                boolean isSuccess = batchSendService.uploadFile2ProdBatch(batchId);
                if(isSuccess == false){
                    falseResultList.add(isSuccess);
                }
                resultList.add(isSuccess);
            }
            result.put("result",resultList);
        }catch (Exception e){
            logger.error("[op:BatchSendController] fail to uploadFile2ProdBatch",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to uploadFile2ProdBatch");
            return result;
        }
       if(falseResultList.size() > 1){
           result.put("resultCode",CODE_FAIL);
           result.put("resultMsg","多条下发失败");
           return result;
       }else if(falseResultList.size() == 1){
           result.put("resultCode",CODE_FAIL);
           result.put("resultMsg","1条下发失败");
           return result;
       }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","下发成功");
        return result;
    }
}
