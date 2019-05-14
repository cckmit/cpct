package com.zjtelcom.cpct.dubbo.out.impl;


import com.zjtelcom.cpct.dao.grouping.TrialOperationMapper;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.dubbo.out.TrialStatusUpService;
import com.zjtelcom.cpct.enums.TrialStatus;
import com.zjtelcom.cpct.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class TrialStatusUpServiceImpl implements TrialStatusUpService {
    @Autowired
    private TrialOperationMapper  trialOperationMapper;

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
        String remark = MapUtil.getString(params.get("remark"));
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
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","修改成功");
        return  result;
    }


}
