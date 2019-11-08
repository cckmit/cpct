package com.zjtelcom.cpct.dubbo.out.impl;

import com.zjtelcom.cpct.dao.grouping.TrialOperationMapper;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.dubbo.out.TrialStatusUpService;
import com.zjtelcom.cpct.enums.TrialStatus;
import com.zjtelcom.cpct.service.campaign.MktCampaignApiService;
import com.zjtelcom.cpct.service.campaign.MktCampaignService;
import com.zjtelcom.cpct.service.grouping.TrialOperationService;
import com.zjtelcom.cpct.service.grouping.TrialProdService;
import com.zjtelcom.cpct.util.MapUtil;
import com.zjtelcom.es.es.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    @Autowired
    private MktCampaignService mktCampaignService;
    @Autowired(required = false)
    private TrialOperationService trialOperationService;

    @Autowired
    private MktCampaignApiService mktCampaignApiService;


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
            if (!remark.equals("") && status.equals(TrialStatus.ISEE_ANALYZE_FAIL.getValue())){
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

    //es清单日志 大数据解析 转对外接口
    @Override
    public Map<String, Object> cpcLog2WriteFileLabel() {
        HashMap<String, Object> hashMap = new HashMap<>();
        Map<String, Object> map = esService.esLog2WriteFile();
        if ("200".equals(map.get("resultCode"))){
            hashMap.put("msg","success");
            hashMap.put("resultCode","200");
        }
        return hashMap;
    }

    /**
     * 定时过期活动
     * @return
     */
    @Override
    public Map<String, Object> dueMktCampaign() {
        Map<String, Object> result = new HashMap<>();
        result = mktCampaignService.dueMktCampaign();
        return result;
    }

    /**
     * 批量excel定时清单导入
     * @return
     * @throws IOException
     */
    @Override
    public Map<String, Object> importUserListByExcel() throws IOException {
        return trialOperationService.importUserListByExcel();
    }

    /**
     * 销售品下架发送短信 调用 MktCampaignApiServiceImpl salesOffShelf
     * @return
     */
    @Override
    public Map<String, Object> sendMsgByOfferOver() {
        Map<String, Object> stringObjectMap = mktCampaignApiService.salesOffShelf(new HashMap<String, Object>());
        return stringObjectMap;

    }

}
