package com.zjtelcom.cpct.service.impl.grouping;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.grouping.TrialOperationMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.dto.grouping.*;
import com.zjtelcom.cpct.dto.strategy.MktStrategy;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRuleRel;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.grouping.TrialOperationService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.HTTPSClientUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.*;

@Service
public class TrialOperationServiceImpl extends BaseService implements TrialOperationService {

    @Autowired
    private TrialOperationMapper trialOperationMapper;
    @Autowired
    private MktCampaignMapper campaignMapper;
    @Autowired
    private MktStrategyMapper strategyMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MktStrategyConfRuleRelMapper ruleRelMapper;
    @Autowired
    private RedisUtils redisUtils;


    /**
     *redis查询抽样试算结果清单
     * @param batchId
     * @return
     */
    @Override
    public Map<String, Object> findBatchHitsList(Long batchId) {
        Map<String, Object> result = new HashMap<>();
        TrialResponse response = new TrialResponse();
        try {
           response =  restTemplate.postForObject(FIND_BATCH_HITS_LIST_URL, batchId,TrialResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",response);
        return result;
    }

    /**
     * 下发策略试运算结果
     * @param request
     * @return
     */
    @Override
    public Map<String, Object> issueTrialResult(IssueTrialRequest request) {
        Map<String, Object> result = new HashMap<>();
        //todo 入参： 批次号、销售品列表、渠道信息列表
        try {
            //todo 待验证
            restTemplate.postForObject(STRATEGY_TRIAL_TO_REDIS_URL, request,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",null);
        return result;
    }

    /**
     * 新增策略试运算记录
     * @param operationVO
     * @return
     */
    @Override
    public Map<String, Object> createTrialOperation( TrialOperationVO operationVO) {
        Map<String, Object> result = new HashMap<>();
        //生成批次号
        String batchNumSt = DateUtil.date2String(new Date())+System.currentTimeMillis();
        MktCampaignDO campaign = campaignMapper.selectByPrimaryKey(operationVO.getCampaignId());
        MktStrategy strategy = strategyMapper.selectByPrimaryKey(operationVO.getStrategyId());
        if (campaign==null || strategy==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","活动策略信息有误");
            return result;
        }
        TrialOperation trialOp = BeanUtil.create(operationVO,new TrialOperation());
        trialOp.setCampaignName(campaign.getMktCampaignName());
        trialOp.setStrategyName(strategy.getStrategyName());
        trialOp.setBatchNum(Long.valueOf(batchNumSt));
        trialOp.setCreateDate(new Date());
        trialOp.setStatusCd("1000");
        trialOperationMapper.insert(trialOp);
        operationVO.setTrialId(trialOp.getId());
        List<TrialOperation> operationList = trialOperationMapper.findOperationListByStrategyId(operationVO.getStrategyId());
        //todo 调用es的抽样接口
       final TrialOperationVO vo = operationVO;
        new Thread(){
            public void run(){
                sampleFromES(vo);
            }
        }.start();

        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",operationList);
        return result;
    }


    /**
     * es抽样接口
     * @param operationVO
     * @return
     */
    @Override
    public Map<String, Object> sampleFromES(TrialOperationVO operationVO)  {
        Map<String, Object> result = new HashMap<>();
        TrialOperation trialOperation = trialOperationMapper.selectByPrimaryKey(operationVO.getTrialId());
        MktCampaignDO campaign = campaignMapper.selectByPrimaryKey(operationVO.getCampaignId());
        MktStrategy strategy = strategyMapper.selectByPrimaryKey(operationVO.getStrategyId());
        if (campaign==null || strategy==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","活动策略信息有误");
            return result;
        }
        //TODO 通过活动id获取关联的标签字段数组
        String[] fieldList = new String[10];

        TrialRequest request = new TrialRequest();
        request.setFieldList(fieldList);
        List<TrialOperationParam> paramList = new ArrayList<>();
        List<MktStrategyConfRuleRelDO> ruleRelList = ruleRelMapper.selectByMktStrategyConfId(operationVO.getStrategyId());
        for (MktStrategyConfRuleRelDO ruleRelDO : ruleRelList){
            TrialOperationParam param = new TrialOperationParam();
            param.setRuleId(ruleRelDO.getMktStrategyConfRuleId());
            param.setBatchNum(trialOperation.getBatchNum());
            //redis取规则
            String rule = redisUtils.get("EVENT_RULE_"+operationVO.getCampaignId()+"_"+operationVO.getStrategyId()+"_"+ruleRelDO.getMktStrategyConfRuleId()).toString();
            param.setRule(rule);
            paramList.add(param);
        }
        request.setOperationVOList(paramList);
        HashMap<String,Object> response = new HashMap<>();

        try {
            restTemplate.postForObject(SEARCH_INFO_FROM_ES_URL, request,null);
        } catch (Exception e) {
            e.printStackTrace();
            // 抽样试算失败
            trialOperation.setStatusCd("2000");
            trialOperationMapper.updateByPrimaryKey(trialOperation);
        }
        // 抽样试算成功
        trialOperation.setStatusCd("3000");
        trialOperationMapper.updateByPrimaryKey(trialOperation);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",null);
        return result;
    }

    //弃用
    private JSONObject searchInfoFromEs(List<TrialOperationParam> operationVOList, String[] fieldList)throws Exception {
        HttpClient httpClient = HttpClients.createDefault();

        String url = "https://localhost/es/searchBatchInfo";

        Map<String, String> paramHeader = new HashMap<>();
        paramHeader.put("Accept", "application/xml");
        Map<String, String> paramBody = new HashMap<>();
        paramBody.put("operationVOList", operationVOList.toString());
        paramBody.put("fieldList", fieldList.toString());
        String result = HTTPSClientUtil.doPost(httpClient, url, paramHeader, paramBody);
        //String result = HTTPSClientUtil.doGet(httpsClient, url, null, null);
        System.out.println(result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        return jsonObject;
    }


    /**
     * 刷新列表
     * @param strategyId
     * @return
     */
    @Override
    public Map<String, Object> getTrialListByStrategyId(Long strategyId){
        Map<String,Object> result = new HashMap<>();
        List<TrialOperation> trialOperations = trialOperationMapper.findOperationListByStrategyId(strategyId);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",trialOperations);
        return result;
    }

}