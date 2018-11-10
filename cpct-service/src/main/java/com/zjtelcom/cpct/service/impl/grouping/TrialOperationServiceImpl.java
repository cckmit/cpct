package com.zjtelcom.cpct.service.impl.grouping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamChlConfMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.MktCamScriptMapper;
import com.zjtelcom.cpct.dao.channel.OfferMapper;
import com.zjtelcom.cpct.dao.grouping.TrialOperationMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.dao.system.SysStaffMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamChlConfDO;
import com.zjtelcom.cpct.domain.campaign.MktCamChlResultConfRelDO;
import com.zjtelcom.cpct.domain.campaign.MktCamItem;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.grouping.GroupingVO;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.domain.system.SysStaff;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConf;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.campaign.MktCamChlResult;
import com.zjtelcom.cpct.dto.channel.LabelDTO;
import com.zjtelcom.cpct.dto.grouping.*;
import com.zjtelcom.cpct.dto.strategy.MktStrategy;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConf;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRule;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRuleRel;
import com.zjtelcom.cpct.dto.user.UserList;
import com.zjtelcom.cpct.pojo.MktCamScript;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.MktCamChlConfService;
import com.zjtelcom.cpct.service.channel.MessageLabelService;
import com.zjtelcom.cpct.service.channel.ProductService;
import com.zjtelcom.cpct.service.grouping.TrialOperationService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfRuleService;
import com.zjtelcom.cpct.util.*;
import com.zjtelcom.es.service.EsService;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.*;
import static com.zjtelcom.cpct.constants.ResponseCode.SUCCESS;

@Service
public class TrialOperationServiceImpl extends BaseService implements TrialOperationService {

    @Value("${spring.esurl.machfile}")
    private String machFile;
    @Value("${spring.esurl.batchinfo}")
    private String batchInfo;
    @Value("${spring.esurl.hitslist}")
    private String hitsList;
    @Value("${spring.esurl.countinfo}")
    private String countInfo;

//    private static String machFile = CPC_MATCH_FILE_TO_FTP;
//    private static String batchInfo = SEARCH_INFO_FROM_ES_URL;
//    private static String hitsList = FIND_BATCH_HITS_LIST_URL;
//    private static String countInfo = SEARCH_COUNT_INFO_URL;

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
    @Autowired
    private MessageLabelService messageLabelService;
    @Autowired
    private MktStrategyConfRuleMapper ruleMapper;
    @Autowired
    private InjectionLabelMapper labelMapper;
    @Autowired
    private OfferMapper offerMapper;
    @Autowired
    private MktCamChlConfMapper chlConfMapper;
    @Autowired
    private MktCamScriptMapper scriptMapper;
    @Autowired
    private SysStaffMapper sysStaffMapper;
    @Autowired
    private SysParamsMapper sysParamsMapper;
    @Autowired(required = false)
    private EsService esService;

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
     * 抽样业务校验
     * @param operationVO
     * @return
     */
    @Override
    public Map<String, Object> businessCheck(TrialOperationVO operationVO) {
        Map<String, Object> result = new HashMap<>();
        MktCampaignDO campaign = campaignMapper.selectByPrimaryKey(operationVO.getCampaignId());
        if (campaign == null) {
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "活动策略信息有误");
            return result;
        }
        // 通过活动id获取关联的标签字段数组
        String[] fieldList = getStrings(campaign);

        TrialOperationVO request = BeanUtil.create(operationVO,new TrialOperationVO());
        request.setFieldList(fieldList);
        //抽样业务校验
        request.setSample(false);
        List<TrialOperationParam> paramList = new ArrayList<>();
        List<MktStrategyConfRuleRelDO> ruleRelList = ruleRelMapper.selectByMktStrategyConfId(operationVO.getStrategyId());
        for (MktStrategyConfRuleRelDO ruleRelDO : ruleRelList) {
            TrialOperationParam param = getTrialOperationParam(operationVO,null, ruleRelDO.getMktStrategyConfRuleId(),true);
            paramList.add(param);
        }
        request.setParamList(paramList);
        TrialResponse response = new TrialResponse();

        try {
            //todo
//            com.zjtelcom.es.model.TrialOperationVO requests = BeanUtil.create(request,new com.zjtelcom.es.model.TrialOperationVO());
//            com.zjtelcom.es.model.TrialResponse response = esService.searchBatchInfo(requests);
            response = restTemplate.postForObject(batchInfo, request, TrialResponse.class);

            if (response.getResultCode().equals(CODE_FAIL)){
                result.put("resultCode", CODE_FAIL);
                result.put("resultMsg", "抽样校验失败");
                return result;
            }
            //todo 返回信息结果封装
        } catch (Exception e) {
            e.printStackTrace();
            // 抽样试算失败
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "抽样校验失败");
            return result;
        }
        if (!response.getResultCode().equals(CODE_SUCCESS)){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "抽样校验失败");
            return result;
        }

        List<Map<String,Object>> customers = new ArrayList<>();
        //抽样数据结果拼装
        for (String ruleIdSt : response.getHitsList().keySet()){
            Long ruleId = Long.valueOf(ruleIdSt);
            List<Map<String,Object>> customerList = (List<Map<String,Object>>) response.getHitsList().get(ruleIdSt);
            for (Map<String,Object> customer : customerList) {
                customer.putAll(getProductAndChannelByRuleId(ruleId));
                customers.add(customer);
            }
        }
        result.put("data",customers);
        // 抽样试算成功
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", null);
        return result;
    }

    public Map<String,Object> getProductAndChannelByRuleId(Long ruleId){
        Map<String,Object> result = new HashMap<>();
        //添加规则下的销售品
        MktStrategyConfRuleDO rule = ruleMapper.selectByPrimaryKey(ruleId);
        if (rule.getProductId()!=null && !rule.getProductId().equals("")){
            List<Long> itemIdList = ChannelUtil.StringToIdList(rule.getProductId());
            List<String> itemList = offerMapper.listByOfferIdList(itemIdList);
            result.put("product",ChannelUtil.StringList2String(itemList));
        }
        StringBuffer st = new StringBuffer();
        if (rule.getEvtContactConfId()!=null && !rule.getEvtContactConfId().equals("")){
            List<Long> channelIdList = ChannelUtil.StringToIdList(rule.getEvtContactConfId());
            List<MktCamChlConfDO> chlConfList = chlConfMapper.listByIdList(channelIdList);
            for (MktCamChlConfDO chlConf : chlConfList){
                CamScript script = scriptMapper.selectByConfId(chlConf.getEvtContactConfId());
                st.append(chlConf.getEvtContactConfName()).append("(")
                        .append(script.getScriptDesc()==null? "" : script.getScriptDesc())
                        .append(")；");
            }
            result.put("channel",st);
        }
        //todo 渠道及推荐指引
        return result;
    }



    /**
     * 导入试运算清单
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> importUserList(MultipartFile multipartFile, TrialOperationVO operation, Long ruleId) throws IOException {
        Map<String, Object> maps = new HashMap<>();

        String batchNumSt = DateUtil.date2String(new Date()) + ChannelUtil.getRandomStr(2);
        //获取销售品及规则列表
        TrialOperationParam param = getTrialOperationParam(operation, Long.valueOf(batchNumSt), ruleId,false);

        InputStream inputStream = multipartFile.getInputStream();
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        Sheet sheet = wb.getSheetAt(0);
        Integer rowNums = sheet.getLastRowNum() + 1;
        for (int i = 1; i < rowNums - 1; i++) {
            Map<String, Object> customers = new HashMap<>();
            Row rowFirst = sheet.getRow(0);
            Row row = sheet.getRow(i);
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cellTitle = rowFirst.getCell(j);
                Cell cell = row.getCell(j);
                customers.put(cellTitle.getStringCellValue(), ChannelUtil.getCellValue(cell));
            }
            Map<String, Object> mktIssueDetailMap = new HashMap<>();
            mktIssueDetailMap.put("batchNum", batchNumSt);
            mktIssueDetailMap.put("mktProductRule", param.getRule());
            mktIssueDetailMap.put("mktCamChlConfDetail", param.getMktCamChlConfDetailList());
            mktIssueDetailMap.put("mktStrategyConfRuleId", param.getMktProductRuleList());
            mktIssueDetailMap.put("customerMap", customers);
            // 将客户信息，销售品，推送渠道存入redis
            redisUtils.add("ISSUE_" + batchNumSt + "customerId", mktIssueDetailMap);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", "导入成功");
        return maps;
    }


    /**
     * 策略试运算区域统计查询
     * @param batchId
     * @return
     */
    @Override
    public Map<String, Object> searchCountAllByArea(Long batchId) {
        Map<String, Object> result = new HashMap<>();
        TrialOperation operation = trialOperationMapper.selectByPrimaryKey(batchId);
        if (operation == null) {
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "统计查询记录出错啦！");
            return result;
        }
        Map<String,Object> resultMap =  (Map<String, Object>) redisUtils.get("HITS_COUNT_INFO_"+operation.getBatchNum());
        if (resultMap==null || resultMap.get("countMap")==null){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "统计查询记录出错啦！");
            return result;
        }
        Map<String,Object> countMap = (Map<String, Object>) resultMap.get("countMap");
        List<GroupingVO> groupingVOS = new ArrayList<>();
        Iterator iterator = countMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String,Object> entry = (Map.Entry<String, Object>) iterator.next();
            GroupingVO vo = new GroupingVO();
            vo.setName(entry.getKey());
            vo.setValue(entry.getValue().toString());
            groupingVOS.add(vo);
        }
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", groupingVOS);
        return result;
    }

    /**
     * 策略试运算统计查询
     * @param batchId
     *
     * @return
     */
    @Override
    public Map<String, Object> searchCountInfo(Long batchId) {
        Map<String, Object> result = new HashMap<>();
        TrialOperation operation = trialOperationMapper.selectByPrimaryKey(batchId);
        if (operation == null) {
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "统计查询记录出错啦！");
            return result;
        }
        Map<String,Object> resultMap =  (Map<String, Object>) redisUtils.get("HITS_COUNT_INFO_"+operation.getBatchNum());
        if (resultMap.get("countMap")!=null){
            resultMap.remove("countMap");
        }
        Map<String,Object> map = new HashMap<>();
        List<String> ruleList = new ArrayList<>();
        List<GroupingVO> groupingVOS = new ArrayList<>();

        Iterator iterator = resultMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String,Object> entry = (Map.Entry<String,Object>)iterator.next();
            ruleList.add(entry.getKey());
            Map<String,Object> valueMap = (Map<String, Object>) entry.getValue();
            List<String> valueList = new ArrayList<>();
            Iterator iter = valueMap.entrySet().iterator();
            while (iter.hasNext()){
                Map.Entry<String,Object> valueEntry = (Map.Entry<String, Object>) iter.next();
                valueList.add(valueEntry.getValue().toString());
            }
            GroupingVO vo = new GroupingVO();
            vo.setName(entry.getKey());
            vo.setValueList(valueList);
            groupingVOS.add(vo);
        }
        Collections.reverse(ruleList);
        Collections.reverse(groupingVOS);
        map.put("rules",ruleList);
        map.put("values",groupingVOS);
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", map);
        return result;
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
        if (campaign == null) {
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "活动策略信息有误");
            return result;
        }
        String[] fieldList = getStrings(campaign);


        TrialOperationVO request = BeanUtil.create(operationVO,new TrialOperationVO());
        request.setBatchNum(trialOperation.getBatchNum());
        request.setFieldList(fieldList);
        //策略试运算
        request.setSample(true);
        //todo 待测试
        List<TrialOperationParam> paramList = new ArrayList<>();
        List<MktStrategyConfRuleRelDO> ruleRelList = ruleRelMapper.selectByMktStrategyConfId(operationVO.getStrategyId());
        for (MktStrategyConfRuleRelDO ruleRelDO : ruleRelList) {
            TrialOperationParam param = getTrialOperationParam(operationVO, trialOperation.getBatchNum(), ruleRelDO.getMktStrategyConfRuleId(),true);
            paramList.add(param);
        }
        request.setParamList(paramList);
        TrialResponse response = new TrialResponse();
        TrialResponse countResponse = new TrialResponse();
        try {
            //todo
//            com.zjtelcom.es.model.TrialOperationVO request = BeanUtil.create(request,new com.zjtelcom.es.model.TrialOperationVO());
//            com.zjtelcom.es.model.TrialResponse response = esService.searchBatchInfo(request));

            response = restTemplate.postForObject(batchInfo, request, TrialResponse.class);
            //同时调用统计查询的功能

//            com.zjtelcom.es.model.TrialResponse countResponse = esService.searchCountInfo(request));
            countResponse = restTemplate.postForObject(countInfo,request,TrialResponse.class);

            if (countResponse.getResultCode().equals(CODE_SUCCESS)){
                redisUtils.set("HITS_COUNT_INFO_"+request.getBatchNum(),countResponse.getHitsList());
            }
            if (!response.getResultCode().equals(CODE_SUCCESS)) {
                trialOperation.setStatusCd("2000");
                trialOperation.setUpdateDate(new Date());
                trialOperation.setRemark(response.getResultMsg());
                trialOperationMapper.updateByPrimaryKey(trialOperation);
            } else {
                trialOperation.setStatusCd("3000");
                trialOperation.setUpdateDate(new Date());
                trialOperation.setRemark(response.getResultMsg());
                trialOperationMapper.updateByPrimaryKey(trialOperation);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 抽样试算失败
            trialOperation.setStatusCd("2000");
            trialOperation.setUpdateDate(new Date());
            trialOperation.setRemark("ES查询错误");
            trialOperationMapper.updateByPrimaryKey(trialOperation);
        }
        // 抽样试算成功
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", null);
        return result;
    }

    private String[] getStrings(MktCampaignDO campaign) {
        // 通过活动id获取关联的标签字段数组
        DisplayColumn req = new DisplayColumn();
        req.setDisplayColumnId(campaign.getCalcDisplay());
        Map<String, Object> labelMap = messageLabelService.queryLabelListByDisplayId(req);
        List<LabelDTO> labelDTOList = (List<LabelDTO>) labelMap.get("labels");
        List<String> codeList = new ArrayList<>();
        for (LabelDTO labelDTO : labelDTOList) {
            codeList.add(labelDTO.getLabelCode());
        }
        //添加固定查询标签
        if (!codeList.contains("ACCS_NBR")){
            codeList.add("ACC_NBR");
        }
        if (!codeList.contains("LATN_NAME")){
            codeList.add("LATN_NAME");
        }
        if (!codeList.contains("CCUST_NAME")){
            codeList.add("CCUST_NAME");
        } if (!codeList.contains("CCUST_ID")){
            codeList.add("CCUST_ID");
        } if (!codeList.contains("CCUST_TEL")){
            codeList.add("CCUST_TEL");
        }

        String[] fieldList = new String[codeList.size()];
        for (int i = 0; i < codeList.size(); i++) {
            fieldList[i] = codeList.get(i);
        }
        return fieldList;
    }


    private TrialOperationParam getTrialOperationParam(TrialOperationVO operationVO, Long batchNum, Long ruleId,boolean isSample) {
        TrialOperationParam param = new TrialOperationParam();
        param.setRuleId(ruleId);
        MktStrategyConfRuleDO confRule = ruleMapper.selectByPrimaryKey(ruleId);
        if (confRule != null) {
            param.setRuleName(confRule.getMktStrategyConfRuleName());
            param.setTarGrpId(confRule.getTarGrpId());
        }
        if (!isSample){
            // 获取规则信息
            Map<String, Object> mktStrategyConfRuleMap = mktStrategyConfRuleService.getMktStrategyConfRule(ruleId);
            MktStrategyConfRule mktStrategyConfRule = (MktStrategyConfRule) mktStrategyConfRuleMap.get("mktStrategyConfRule");

            // 获取销售品集合
            Map<String, Object> productRuleListMap = productService.getProductRuleList(UserUtil.loginId(), mktStrategyConfRule.getProductIdlist());
            List<MktProductRule> mktProductRuleList = (List<MktProductRule>) productRuleListMap.get("resultMsg");
            param.setMktProductRuleList(mktProductRuleList);

            // 获取推送渠道
            List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
            List<MktCamChlConfDetail> mktCamChlConfList = mktStrategyConfRule.getMktCamChlConfDetailList();
            if (mktCamChlConfList != null) {
                for (MktCamChlConfDetail mktCamChlConf : mktCamChlConfList) {
                    Map<String, Object> mktCamChlConfDetailMap = mktCamChlConfService.getMktCamChlConf(mktCamChlConf.getEvtContactConfId());
                    MktCamChlConfDetail mktCamChlConfDetail = (MktCamChlConfDetail) mktCamChlConfDetailMap.get("mktCamChlConfDetail");
                    mktCamChlConfDetailList.add(mktCamChlConfDetail);
                }
            }
            param.setMktCamChlConfDetailList(mktCamChlConfDetailList);
        }
        // 设置批次号
        param.setBatchNum(batchNum);
        //redis取规则
        Object ruleOb = redisUtils.get("EVENT_RULE_" + operationVO.getCampaignId() + "_" + operationVO.getStrategyId() + "_" + ruleId);
        String rule = "";
        if (ruleOb!=null){
            rule = ruleOb.toString();
            System.out.println("*************************" + rule);
        }
        param.setRule(rule);
        List<LabelResult> labelResultList = new ArrayList<>();
        if (redisUtils.get("EVENT_RULE_" + operationVO.getCampaignId() + "_" + operationVO.getStrategyId() + "_" + ruleId+"_LABEL")!=null){
            JSONArray objects = JSONArray.parseArray((String) redisUtils.get("EVENT_RULE_" + operationVO.getCampaignId() + "_" + operationVO.getStrategyId() + "_" + ruleId+"_LABEL"));
            labelResultList = objects.toJavaList(LabelResult.class);
        }
        param.setLabelResultList(labelResultList);
        return param;
    }


    /**
     * redis查询抽样试算结果清单
     *
     * @param operationId
     * @return
     */
    @Override
    public Map<String, Object> findBatchHitsList(Long operationId) {
        Map<String, Object> result = new HashMap<>();

        TrialOperation operation = trialOperationMapper.selectByPrimaryKey(operationId);
        if (operation == null) {
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "试运算记录不存在");
            return result;
        }
//        com.zjtelcom.es.model.TrialResponse response = new com.zjtelcom.es.model.TrialResponse();
        TrialResponse response = new TrialResponse();
        try {
            Map<String, Long> param = new HashMap<>();
            param.put("batchId", operation.getBatchNum());
//            response = esService.findBatchHitsList(operation.getBatchNum().toString());
            response = restTemplate.postForObject(hitsList, param, TrialResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TrialOperationListVO vo = new TrialOperationListVO();
        List<String> labelCodeList = new ArrayList<>();
        List<Map<String, Object>> userList = new ArrayList<>();

        Map<String, Object> hitsList = (Map<String, Object>) response.getHitsList();
        if (hitsList == null) {
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "未命中任何客户");
            return result;
        }

        List<Map<String,Object>> mapList = (List<Map<String,Object>>) hitsList.get("result");

        for (Map<String,Object> hitMap : mapList){
            Map<String, Object> searchMap = (Map<String, Object>) hitMap.get("searchHitMap");
            Map<String, Object> ruleInfoMap = new HashMap<>();
            if ((Map<String, Object>)hitMap.get("ruleInfo") != null) {
                ruleInfoMap = (Map<String, Object>) hitMap.get("ruleInfo");
            }
            Map<String, Object> map = new HashMap<>();
            for (String set : searchMap.keySet()) {
                if (labelCodeList.size() < searchMap.keySet().size()) {
                    labelCodeList.add(set);
                }
                map.put(set, searchMap.get(set));
            }
            map.put("campaignId", operation.getCampaignId());
            map.put("campaignName", operation.getCampaignName());
            map.put("strategyId", operation.getStrategyId());
            map.put("strategyName", operation.getStrategyName());
            map.put("ruleId", ruleInfoMap.get("ruleId"));
            map.put("ruleName", ruleInfoMap.get("ruleName").toString());
            //todo 工单号
            map.put("orderId", "49736605");
            userList.add(map);

        }
//        for (String key : hitsList.keySet()) {
//            Map<String, Object> searchMap = (Map<String, Object>) ((Map<String, Object>) hitsList.get(key)).get("searchHitMap");
//            Map<String, Object> ruleInfoMap = new HashMap<>();
//            if (((Map<String, Object>) hitsList.get(key)).get("ruleInfo") != null) {
//                ruleInfoMap = (Map<String, Object>) ((Map<String, Object>) hitsList.get(key)).get("ruleInfo");
//            }
//            Map<String, Object> map = new HashMap<>();
//            for (String set : searchMap.keySet()) {
//                if (labelCodeList.size() < searchMap.keySet().size()) {
//                    labelCodeList.add(set);
//                }
//                map.put(set, searchMap.get(set));
//            }
//            map.put("campaignId", operation.getCampaignId());
//            map.put("campaignName", operation.getCampaignName());
//            map.put("strategyId", operation.getStrategyId());
//            map.put("strategyName", operation.getStrategyName());
//            map.put("ruleId", ruleInfoMap.get("ruleId"));
//            map.put("ruleName", ruleInfoMap.get("ruleName").toString());
//            //todo 工单号
//            map.put("orderId", "49736605");
//            userList.add(map);
//        }
        if (labelCodeList.size() > 0) {
            List<SimpleInfo> titleList = labelMapper.listLabelByCodeList(labelCodeList);
            vo.setTitleList(titleList);
        }
        vo.setHitsList(userList);
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", vo);
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
        TrialOperation operation = trialOperationMapper.selectByPrimaryKey(trialOperation.getId());
        if (operation==null){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "试运算记录不存在");
            return result;
        }
        BeanUtil.copy(operation,trialOperation);

        // 通过活动id获取关联的标签字段数组
        MktCampaignDO campaignDO = campaignMapper.selectByPrimaryKey(trialOperation.getCampaignId());
        if (campaignDO==null){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "活动不存在");
            return result;
        }
        // 通过活动id获取关联的标签字段数组
        DisplayColumn req = new DisplayColumn();
        req.setDisplayColumnId(campaignDO.getCalcDisplay());
        Map<String,Object> labelMap = messageLabelService.queryLabelListByDisplayId(req);
        List<LabelDTO> labelDTOList = (List<LabelDTO>)labelMap.get("labels");
        String[] fieldList = new String[labelDTOList.size()];
        for (int i = 0 ; i< labelDTOList.size();i++){
            fieldList[i] = labelDTOList.get(i).getLabelCode();
        }

        TrialOperationVO request = BeanUtil.create(trialOperation,new TrialOperationVO());
        request.setFieldList(fieldList);
        request.setCampaignType(campaignDO.getMktCampaignType());
        request.setLanId(campaignDO.getLanId());
        request.setCamLevel(campaignDO.getCamLevel());

        // 获取创建人员code
        SysStaff sysStaff = sysStaffMapper.selectByPrimaryKey(campaignDO.getCreateStaff());
        request.setStaffCode(sysStaff.getStaffCode());

        List<TrialOperationParam> paramList = new ArrayList<>();
        List<MktStrategyConfRuleRelDO> ruleRelList = ruleRelMapper.selectByMktStrategyConfId(trialOperation.getStrategyId());
        for (MktStrategyConfRuleRelDO ruleRelDO : ruleRelList) {
            TrialOperationParam param = new TrialOperationParam();
            // 获取规则Id
            Long ruleId = ruleRelDO.getMktStrategyConfRuleId();
            param.setRuleId(ruleId);
            MktStrategyConfRuleDO confRule = ruleMapper.selectByPrimaryKey(ruleId);
            if (confRule != null) {
                param.setRuleName(confRule.getMktStrategyConfRuleName());
                param.setTarGrpId(confRule.getTarGrpId());
            }
            // 获取规则信息
            Map<String, Object> mktStrategyConfRuleMap = mktStrategyConfRuleService.getMktStrategyConfRule(ruleId);
            MktStrategyConfRule mktStrategyConfRule = (MktStrategyConfRule) mktStrategyConfRuleMap.get("mktStrategyConfRule");

            // 获取销售品集合
            Map<String, Object> productRuleListMap = productService.getProductRuleList(UserUtil.loginId(), mktStrategyConfRule.getProductIdlist());
            List<MktProductRule> mktProductRuleList = (List<MktProductRule>) productRuleListMap.get("resultMsg");
            param.setMktProductRuleList(mktProductRuleList);

            // 获取推送渠道
            List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
            List<MktCamChlConfDetail> mktCamChlConfList = mktStrategyConfRule.getMktCamChlConfDetailList();
            if(mktCamChlConfList!=null){
                for (MktCamChlConfDetail mktCamChlConf : mktCamChlConfList) {
                    Map<String, Object> mktCamChlConfDetailMap = mktCamChlConfService.getMktCamChlConf(mktCamChlConf.getEvtContactConfId());
                    MktCamChlConfDetail mktCamChlConfDetail = (MktCamChlConfDetail) mktCamChlConfDetailMap.get("mktCamChlConfDetail");
                    mktCamChlConfDetail.setIsSecondCoop("1");   // 设置为首次协同
                    mktCamChlConfDetailList.add(mktCamChlConfDetail);
                }
            }

            List<MktCamChlResult> mktCamChlResultList = mktStrategyConfRule.getMktCamChlResultList();
            if(mktCamChlResultList!=null){
                for (MktCamChlResult mktCamChlResult : mktCamChlResultList) {
                    List<MktCamChlConfDetail> confDetailResultList = mktCamChlResult.getMktCamChlConfDetailList();
                    for (MktCamChlConfDetail mktCamChlConfDetail : confDetailResultList) {
                        mktCamChlConfDetail.setIsSecondCoop("0");  // 设置为二次协同
                        mktCamChlConfDetailList.add(mktCamChlConfDetail);
                    }
                }
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
        request.setParamList(paramList);
        try {
//            com.zjtelcom.es.model.TrialResponse response = esService.strategyIssure(BeanUtil.create(request,new com.zjtelcom.es.model.TrialOperationVO()));
            //todo 待验证
            restTemplate.postForObject(machFile, request, TrialResponse.class);
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
