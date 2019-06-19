package com.zjtelcom.cpct.dubbo.out.impl;


import com.zjtelcom.cpct.dao.grouping.TrialOperationMapper;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.dubbo.out.TrialStatusUpService;
import com.zjtelcom.cpct.enums.TrialStatus;
import com.zjtelcom.cpct.service.grouping.TrialProdService;
import com.zjtelcom.cpct.util.MapUtil;
import com.zjtelcom.es.es.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class TrialStatusUpServiceImpl implements TrialStatusUpService {
    @Autowired
    private TrialOperationMapper  trialOperationMapper;
    @Autowired(required = false)
    private EsService esService;
    @Autowired
    private TrialProdService trialProdService;


    @Override
    public Map<String,Object> campaignIndexTask(Map<String,Object> param) {
        Map<String, Object> result = new HashMap<>();
        System.out.println("全量试算活动入参："+param);
        result = trialProdService.campaignIndexTask(param);
        return result;
    }

    /**
     * 更新试算记录状态
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> updateOperationStatus(Map<String,Object> params) {
        Map<String,Object> result = new HashMap<>();
        String batchNum = MapUtil.getString(params.get("batchNum"));
        String status = MapUtil.getString(params.get("status"));
        String remark = MapUtil.getString(params.get("message"));
        TrialOperation operation = trialOperationMapper.selectByBatchNum(batchNum);
        if (operation==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","试算记录不存在");
            return  result;
        }
        operation.setStatusCd(status);
        if (status.equals(TrialStatus.SAMPEL_SUCCESS.getValue())){
            operation.setUpdateDate(new Date());
            operation.setRemark("抽样试算成功");
        }else if (status.equals(TrialStatus.ALL_SAMPEL_SUCCESS.getValue())){
            operation.setRemark("全量试算成功");
        }else if (status.equals(TrialStatus.UPLOAD_SUCCESS.getValue())){
            operation.setRemark("文件下发成功");
        }else {
            operation.setRemark(remark);
        }
        trialOperationMapper.updateByPrimaryKey(operation);
        try {
            Map<String,Object> param = new HashMap<>();
            param.put("batchNum",batchNum);
            if (status.equals(TrialStatus.ISEE_ANALYZE_FAIL.getValue())){
                param.put("data",remark);
            }else {
                param.put("data",TrialStatus.getNameByCode(status).getName());
            }
            esService.addLogByBatchNum(param);
        }catch (Exception e){
            e.printStackTrace();
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","修改成功");
        return  result;
    }


}
