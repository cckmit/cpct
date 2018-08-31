package com.zjtelcom.cpct.service.impl.grouping;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.grouping.TrialOperationMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamChlResultConfRelDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.MktProductRule;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConf;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.grouping.*;
import com.zjtelcom.cpct.dto.strategy.MktStrategy;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConf;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRule;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRuleRel;
import com.zjtelcom.cpct.dto.user.UserList;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.MktCamChlConfService;
import com.zjtelcom.cpct.service.channel.ProductService;
import com.zjtelcom.cpct.service.grouping.TrialOperationService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfRuleService;
import com.zjtelcom.cpct.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.*;

@Service
public class TrialOperationServiceImpl extends BaseService implements TrialOperationService {

    @Autowired
    private TrialOperationMapper trialOperationMapper;
    @Autowired
    private MktCampaignMapper campaignMapper;
    @Autowired
    private MktStrategyConfMapper strategyMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MktStrategyConfRuleRelMapper ruleRelMapper;
    @Autowired
    private RedisUtils redisUtils;
    /**
     * 销售品service
     */
    @Autowired
    private ProductService productService;
    /**
     * 规则Service
     */
    @Autowired
    private MktStrategyConfRuleService mktStrategyConfRuleService;
    /**
     * 推送渠道service
     */
    @Autowired
    private MktCamChlConfService mktCamChlConfService;



    /**
     * 导入试运算清单
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> importUserList(MultipartFile multipartFile, Long ruleId) throws IOException {
        Map<String, Object> maps = new HashMap<>();

        InputStream inputStream = multipartFile.getInputStream();
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        Sheet sheet = wb.getSheetAt(0);
        Integer rowNums = sheet.getLastRowNum() + 1;
        for (int i = 0; i < rowNums; i++) {
            Map<String,Object> customers = new HashMap<>();
            Row row = sheet.getRow(i);
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
//                customers.put(cell.get,cell.getStringCellValue());

            }
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }



    /**
     * 新增策略试运算记录
     *
     * @param operationVO
     * @return
     */
    @Override
    public Map<String, Object> createTrialOperation(TrialOperationVO operationVO) {
        Map<String, Object> result = new HashMap<>();
        //生成批次号
        String batchNumSt = DateUtil.date2String(new Date()) + ChannelUtil.getRandomStr(2);
        MktCampaignDO campaign = campaignMapper.selectByPrimaryKey(operationVO.getCampaignId());
        MktStrategyConfDO strategy = strategyMapper.selectByPrimaryKey(operationVO.getStrategyId());
        if (campaign == null || strategy == null) {
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "活动策略信息有误");
            return result;
        }
        TrialOperation trialOp = BeanUtil.create(operationVO, new TrialOperation());
        trialOp.setCampaignName(campaign.getMktCampaignName());
        trialOp.setStrategyName(strategy.getMktStrategyConfName());
        trialOp.setBatchNum(Long.valueOf(batchNumSt));
        trialOp.setCreateDate(new Date());
        trialOp.setStatusCd("1000");
        trialOperationMapper.insert(trialOp);
        operationVO.setTrialId(trialOp.getId());
        List<TrialOperation> operationList = trialOperationMapper.findOperationListByStrategyId(operationVO.getStrategyId());
        // 调用es的抽样接口
        final TrialOperationVO vo = operationVO;
        new Thread() {
            public void run() {
                sampleFromES(vo);
            }
        }.start();

        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", operationList);
        return result;
    }


    /**
     * es抽样接口
     *
     * @param operationVO
     * @return
     */
    public Map<String, Object> sampleFromES(TrialOperationVO operationVO) {
        Map<String, Object> result = new HashMap<>();
        TrialOperation trialOperation = trialOperationMapper.selectByPrimaryKey(operationVO.getTrialId());
        MktCampaignDO campaign = campaignMapper.selectByPrimaryKey(operationVO.getCampaignId());
        MktStrategyConfDO strategy = strategyMapper.selectByPrimaryKey(operationVO.getStrategyId());
        if (campaign == null || strategy == null) {
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "活动策略信息有误");
            return result;
        }
        // 通过活动id获取关联的标签字段数组
        String[] fieldList = new String[10];

        TrialRequest request = new TrialRequest();
        request.setFieldList(fieldList);
        List<TrialOperationParam> paramList = new ArrayList<>();
        List<MktStrategyConfRuleRelDO> ruleRelList = ruleRelMapper.selectByMktStrategyConfId(operationVO.getStrategyId());
        for (MktStrategyConfRuleRelDO ruleRelDO : ruleRelList) {
            TrialOperationParam param = getTrialOperationParam(operationVO, trialOperation.getBatchNum(), ruleRelDO.getMktStrategyConfRuleId());
            paramList.add(param);
        }
        request.setOperationVOList(paramList);
        TrialResponse response = new TrialResponse();

        try {
            response = restTemplate.postForObject(SEARCH_INFO_FROM_ES_URL, request, TrialResponse.class);
            if (!response.getResultCode().equals(CODE_SUCCESS)) {
                trialOperation.setStatusCd("2000");
                trialOperationMapper.updateByPrimaryKey(trialOperation);
            } else {
                trialOperation.setStatusCd("3000");
                trialOperationMapper.updateByPrimaryKey(trialOperation);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 抽样试算失败
            trialOperation.setStatusCd("2000");
            trialOperationMapper.updateByPrimaryKey(trialOperation);
        }
        // 抽样试算成功
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", null);
        return result;
    }


    private TrialOperationParam getTrialOperationParam(TrialOperationVO operationVO,Long batchNum,Long ruleId) {
        TrialOperationParam param = new TrialOperationParam();

        param.setRuleId(ruleId);

        // 获取规则信息
        Map<String, Object> mktStrategyConfRuleMap = mktStrategyConfRuleService.getMktStrategyConfRule(ruleId);
        MktStrategyConfRule mktStrategyConfRule = (MktStrategyConfRule) mktStrategyConfRuleMap.get("mktStrategyConfRule");

        // 获取销售品集合
        Map<String, Object> productRuleListMap = productService.getProductRuleList(UserUtil.loginId(), mktStrategyConfRule.getProductIdlist());
        List<MktProductRule> mktProductRuleList = (List<MktProductRule>) productRuleListMap.get("ruleList");
        param.setMktProductRuleList(mktProductRuleList);

        // 获取推送渠道
        List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
        List<MktCamChlConf> mktCamChlConfList = mktStrategyConfRule.getMktCamChlConfList();
        for (MktCamChlConf mktCamChlConf : mktCamChlConfList) {
            Map<String, Object> mktCamChlConfDetailMap = mktCamChlConfService.getMktCamChlConf(mktCamChlConf.getEvtContactConfId());
            MktCamChlConfDetail mktCamChlConfDetail = (MktCamChlConfDetail) mktCamChlConfDetailMap.get("mktCamChlConfDetail");
            mktCamChlConfDetailList.add(mktCamChlConfDetail);
        }
        param.setMktCamChlConfDetailList(mktCamChlConfDetailList);

        // 设置批次号
        param.setBatchNum(batchNum);
        //redis取规则
        String rule = redisUtils.get("EVENT_RULE_" + operationVO.getCampaignId() + "_" + operationVO.getStrategyId() + "_" + ruleId).toString();
        System.out.println("*************************" + rule);
        param.setRule(rule);
        return param;
    }


    /**
     * redis查询抽样试算结果清单
     *
     * @param batchId
     * @return
     */
    @Override
    public Map<String, Object> findBatchHitsList(Long batchId) {
        Map<String, Object> result = new HashMap<>();
        TrialResponse response = new TrialResponse();
        try {
            response = restTemplate.postForObject(FIND_BATCH_HITS_LIST_URL, batchId, TrialResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", response);
        return result;
    }

    /**
     * 下发策略试运算结果
     *
     * @param trialOperation
     * @return
     */
    @Override
    public Map<String, Object> issueTrialResult(TrialOperation trialOperation) {
        Map<String, Object> result = new HashMap<>();
        //todo 入参： 批次号、销售品列表、渠道信息列表
        // 通过活动id获取关联的标签字段数组
        String[] fieldList = new String[10];

        TrialRequest request = new TrialRequest();
        request.setFieldList(fieldList);
        List<TrialOperationParam> paramList = new ArrayList<>();
        List<MktStrategyConfRuleRelDO> ruleRelList = ruleRelMapper.selectByMktStrategyConfId(trialOperation.getStrategyId());
        for (MktStrategyConfRuleRelDO ruleRelDO : ruleRelList) {
            TrialOperationParam param = new TrialOperationParam();
            // 获取规则Id
            Long ruleId = ruleRelDO.getMktStrategyConfRuleId();
            param.setRuleId(ruleId);

            // 获取规则信息
            Map<String, Object> mktStrategyConfRuleMap = mktStrategyConfRuleService.getMktStrategyConfRule(ruleId);
            MktStrategyConfRule mktStrategyConfRule = (MktStrategyConfRule) mktStrategyConfRuleMap.get("mktStrategyConfRule");

            // 获取销售品集合
            Map<String, Object> productRuleListMap = productService.getProductRuleList(UserUtil.loginId(), mktStrategyConfRule.getProductIdlist());
            List<MktProductRule> mktProductRuleList = (List<MktProductRule>) productRuleListMap.get("ruleList");
            param.setMktProductRuleList(mktProductRuleList);

            // 获取推送渠道
            List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
            List<MktCamChlConf> mktCamChlConfList = mktStrategyConfRule.getMktCamChlConfList();
            for (MktCamChlConf mktCamChlConf : mktCamChlConfList) {
                Map<String, Object> mktCamChlConfDetailMap = mktCamChlConfService.getMktCamChlConf(mktCamChlConf.getEvtContactConfId());
                MktCamChlConfDetail mktCamChlConfDetail = (MktCamChlConfDetail) mktCamChlConfDetailMap.get("mktCamChlConfDetail");
                mktCamChlConfDetailList.add(mktCamChlConfDetail);
            }
            param.setMktCamChlConfDetailList(mktCamChlConfDetailList);

            // 设置批次号
            param.setBatchNum(trialOperation.getBatchNum());
            //redis取规则
            String rule = redisUtils.get("EVENT_RULE_" + trialOperation.getCampaignId() + "_" + trialOperation.getStrategyId() + "_" + ruleRelDO.getMktStrategyConfRuleId()).toString();
            System.out.println("*************************" + rule);
            param.setRule(rule);
            paramList.add(param);
        }
        request.setOperationVOList(paramList);
        try {
            //todo 待验证
            restTemplate.postForObject(STRATEGY_TRIAL_TO_REDIS_URL, request, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", null);
        return result;
    }


    //弃用
/*
    private JSONObject searchInfoFromEs(List<TrialOperationParam> operationVOList, String[] fieldList) throws Exception {
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

*/

    /**
     * 刷新列表
     *
     * @param strategyId
     * @return
     */
    @Override
    public Map<String, Object> getTrialListByStrategyId(Long strategyId) {
        Map<String, Object> result = new HashMap<>();
        List<TrialOperation> trialOperations = trialOperationMapper.findOperationListByStrategyId(strategyId);
        List<TrialOperationDetail> operationDetailList = new ArrayList<>();
        for (TrialOperation trialOperation : trialOperations) {
            TrialOperationDetail detail = BeanUtil.create(trialOperation, new TrialOperationDetail());
            if (trialOperation.getUpdateDate() != null) {
                Double cost = (double) ((trialOperation.getUpdateDate().getTime() - trialOperation.getCreateDate().getTime()) / 1000);
                detail.setCost(cost + "s");
            }
            operationDetailList.add(detail);
        }
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", operationDetailList);
        return result;
    }

}
