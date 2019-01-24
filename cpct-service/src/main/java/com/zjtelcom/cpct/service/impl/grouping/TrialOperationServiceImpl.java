package com.zjtelcom.cpct.service.impl.grouping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamChlConfMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamItemMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.MktCamScriptMapper;
import com.zjtelcom.cpct.dao.channel.MktResourceMapper;
import com.zjtelcom.cpct.dao.channel.OfferMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TrialOperationMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamChlConfDO;
import com.zjtelcom.cpct.domain.campaign.MktCamItem;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.grouping.GroupingVO;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.campaign.MktCamChlResult;
import com.zjtelcom.cpct.dto.channel.LabelDTO;
import com.zjtelcom.cpct.dto.channel.VerbalVO;
import com.zjtelcom.cpct.dto.grouping.*;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRule;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.MktCamChlConfService;
import com.zjtelcom.cpct.service.channel.MessageLabelService;
import com.zjtelcom.cpct.service.channel.ProductService;
import com.zjtelcom.cpct.service.grouping.TrialOperationService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfRuleService;
import com.zjtelcom.cpct.util.*;
import com.zjtelcom.es.es.entity.*;
import com.zjtelcom.es.es.entity.model.LabelResultES;
import com.zjtelcom.es.es.entity.model.TrialOperationParamES;
import com.zjtelcom.es.es.entity.model.TrialResponseES;
import com.zjtelcom.es.es.service.EsService;
import org.apache.poi.ss.usermodel.Cell;
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
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class TrialOperationServiceImpl extends BaseService implements TrialOperationService {



    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;

    @Autowired
    private InjectionLabelMapper injectionLabelMapper;

    @Autowired
    private TrialOperationMapper trialOperationMapper;
    @Autowired
    private MktCampaignMapper campaignMapper;
    @Autowired
    private MktStrategyConfMapper strategyMapper;
    @Autowired
    private MktStrategyConfRuleRelMapper strategyConfRuleRelMapper;
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
    private SysParamsMapper sysParamsMapper;
    @Autowired(required = false)
    private EsService esService;
    @Autowired
    private MktResourceMapper resourceMapper;
    @Autowired
    private MktStrategyConfMapper strategyConfMapper;

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

    @Autowired
    private MktCamItemMapper itemMapper;


    /**
     * 标签查询非空总数
     * @param labelCodes
     * @return
     */
    @Override
    public Map<String, Object> searchCountByLabelList(String labelCodes) {
        Map<String, Object> result = new HashMap<>();
        String[] codeList = labelCodes.split(",");
        TrialResponseES response = new TrialResponseES();
        TrialOperationVOES request = new TrialOperationVOES();
        request.setFieldList(codeList);
        try {
            response = restTemplate.postForObject("localhost:8090/es/searchCountByLabelList", request, TrialResponseES.class);
            if (!response.getResultCode().equals("200")){
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","查询失败");
                return result;
            }
        }catch (Exception e){
            e.printStackTrace();
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","查询失败");
            return result;
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","查询成功");
        result.put("total",response.getHitsList());
        return result;
    }

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
            result.put("resultMsg", "活动信息有误");
            return result;
        }
        MktStrategyConfDO strategy = strategyMapper.selectByPrimaryKey(operationVO.getStrategyId());
        if (strategy==null){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "策略信息有误");
            return result;
        }
        //添加策略适用地市
        redisUtils.set("STRATEGY_CONF_AREA_"+operationVO.getStrategyId(),strategy.getAreaId());

        // 通过活动id获取关联的标签字段数组
        String[] fieldList = getStrings(campaign,strategy);

        TrialOperationVO request = BeanUtil.create(operationVO,new TrialOperationVO());
        //抽样业务校验
        request.setSample(false);
        TrialOperationVOES requests = BeanUtil.create(request,new TrialOperationVOES());

        ArrayList<TrialOperationParamES> paramList = new ArrayList<>();
        List<MktStrategyConfRuleRelDO> ruleRelList = ruleRelMapper.selectByMktStrategyConfId(operationVO.getStrategyId());
        for (MktStrategyConfRuleRelDO ruleRelDO : ruleRelList) {
            TrialOperationParamES param = getTrialOperationParamES(operationVO,null, ruleRelDO.getMktStrategyConfRuleId(),true);
            List<LabelResultES> labelResultList = param.getLabelResultList();
            List<String> labelTypeList = new ArrayList<>();
            for (LabelResultES la : labelResultList){
                labelTypeList.add(la.getRightOperand());
            }
            if (!labelTypeList.contains("2000")){
                result.put("resultCode", CODE_FAIL);
                result.put("resultMsg", "规则："+param.getRuleName()+"不满足查询条件，请至少配置一条用户级标签查询条件！");
                return result;
            }
            paramList.add(param);
          Map<String,Object> stringObjectMap =  getProductAndChannelByRuleId(ruleRelDO.getMktStrategyConfRuleId());
          List<String> stringList = (List<String>) stringObjectMap.get("scriptLabel");
          fieldList = ChannelUtil.arrayInput(fieldList,stringList);
        }
        requests.setFieldList(fieldList);
        requests.setParamList(paramList);
//        TrialResponse response = new TrialResponse();
        TrialResponseES response = new TrialResponseES();

        try {
            //todo
             response = esService.searchBatchInfo(requests);
//            response = restTemplate.postForObject(batchInfo, request, TrialResponse.class);

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
            Map<String,Object> map = getProductAndChannelByRuleId(ruleId);
            for (Map<String,Object> customer : customerList) {
                String channelScript = map.get("channelTemp")==null? "" : map.get("channelTemp").toString();
                List<String> camList = subScript(channelScript);
                if (!camList.isEmpty()){
                    for (String code : camList){
                        channelScript = channelScript.replace("${" + code + "}$", customer.get(code)==null ? "" : customer.get(code).toString());
                    }
                    map.put("channel", channelScript);
                }
                //todo 目前只查杭州数据 后续加映射关系
                if(customer.get("LATN_NAME")==null){
                    customer.put("LATN_NAME","杭州");
                }
                customer.putAll(map);
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
        List<String> scriptList = new ArrayList<>();
        //添加规则下的销售品
        MktStrategyConfRuleDO rule = ruleMapper.selectByPrimaryKey(ruleId);
        if (rule.getProductId()!=null && !rule.getProductId().equals("")){
            List<Long> itemIdList = ChannelUtil.StringToIdList(rule.getProductId());
            List<Long> productList = new ArrayList<>();
            List<String> resourceList = new ArrayList<>();
            for (Long itemId : itemIdList){
                MktCamItem item = itemMapper.selectByPrimaryKey(itemId);
                if (item==null){
                    continue;
                }
                if (item.getItemType().equals("1000")){
                    productList.add(itemId);
                }else if (item.getItemType().equals("3000")){
                    MktResource resource = resourceMapper.selectByPrimaryKey(item.getItemId());
                    if (resource!=null){
                        resourceList.add(resource.getMktResName());
                    }
                }
            }
            List<String> itemList = offerMapper.listByOfferIdList(itemIdList);
            result.put("product",ChannelUtil.StringList2String(itemList));
            result.put("resource",ChannelUtil.StringList2String(resourceList));
        }
        StringBuffer st = new StringBuffer();
        if (rule.getEvtContactConfId()!=null && !rule.getEvtContactConfId().equals("")){
            List<Long> channelIdList = ChannelUtil.StringToIdList(rule.getEvtContactConfId());
            List<MktCamChlConfDO> chlConfList = chlConfMapper.listByIdList(channelIdList);
            for (MktCamChlConfDO chlConf : chlConfList){
                CamScript script = scriptMapper.selectByConfId(chlConf.getEvtContactConfId());
                if (script.getScriptDesc()!=null){
                    List<String> camList = subScript(script.getScriptDesc());
                    scriptList.addAll(camList);
                }
                st.append(chlConf.getEvtContactConfName()).append("(")
                        .append(script.getScriptDesc()==null? "" : script.getScriptDesc())
                        .append(")；");
            }
            result.put("channel",st.toString());
            result.put("channelTemp",st.toString());
        }
        result.put("scriptLabel",scriptList);
        //todo 渠道及推荐指引
        return result;
    }

    //截取脚本内的标签
    private List<String> subScript(String str) {
        List<String> result = new ArrayList<>();
//        Pattern p = Pattern.compile("\\$");
        Pattern p = Pattern.compile("(?<=\\$\\{)([^$]+)(?=\\}\\$)");
        Matcher m = p.matcher(str);
//        List<Integer> list = new ArrayList<>();

        while (m.find()) {
//            list.add(m.start());
            result.add(m.group(1));
        }

//        for (int i = 0; i < list.size(); ) {
//            result.add(str.substring(list.get(i) + 1, list.get(++i)));
//            i++;
//        }
        return result;
    }



    /**
     * 导入试运算清单
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> importUserList(MultipartFile multipartFile, TrialOperationVO operation, Long ruleId) throws IOException {
        Map<String, Object> result = new HashMap<>();
        String batchNumSt = DateUtil.date2St4Trial(new Date()) + ChannelUtil.getRandomStr(4);

        InputStream inputStream = multipartFile.getInputStream();
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        Sheet sheet = wb.getSheetAt(0);
        Integer rowNums = sheet.getLastRowNum() + 1;
        List<Map<String,Object>> customerList = new ArrayList<>();

        List<Map<String,Object>> labelList = new ArrayList<>();

        List<LabelDTO>  labelDTOList = new ArrayList<>();
        Row labelRowFirst = sheet.getRow(0);
        Row labelRow = sheet.getRow(1);
        for (int j = 0; j < labelRow.getLastCellNum(); j++) {
            Cell cellTitle = labelRowFirst.getCell(j);
            Cell cell = labelRow.getCell(j);
            LabelDTO  labelDTO = new LabelDTO();
            labelDTO.setLabelCode((String) ChannelUtil.getCellValue(cell));
            labelDTO.setInjectionLabelName(cellTitle.getStringCellValue());
            labelDTOList.add(labelDTO);
        }
        for (int i = 2; i < rowNums ; i++) {
            Map<String, Object> customers = new HashMap<>();
            Row rowCode = sheet.getRow(1);
            Row row = sheet.getRow(i);
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cellTitle = rowCode.getCell(j);
                Cell cell = row.getCell(j);
                customers.put(cellTitle.getStringCellValue(), ChannelUtil.getCellValue(cell));
            }
            customerList.add(customers);
        }
        for (int i = 0 ; i< labelDTOList.size();i++){
            Map<String,Object> label = new HashMap<>();
            label.put("code",labelDTOList.get(i).getLabelCode());
            label.put("name",labelDTOList.get(i).getInjectionLabelName());
            labelList.add(label);
        }
        redisUtils.set("LABEL_DETAIL_"+batchNumSt,labelList);
        int num = (customerList.size() / 500) + 1;
        List<List<Map<String,Object>>> smallCustomers = ChannelUtil.averageAssign(customerList,num);
        //按规则存储客户信息
        for (int i = 0; i < num; i++) {
            redisUtils.hset("ISSURE_" + batchNumSt + "_" + ruleId, i + "",smallCustomers.get(i));
        }
//        redisUtils.set("ISSURE_" + batchNumSt + "_" + ruleId,customerList);
        MktCampaignDO campaignDO = campaignMapper.selectByPrimaryKey(operation.getCampaignId());
        if (campaignDO==null){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "活动不存在");
            return result;
        }
        final TrialOperationVOES request = BeanUtil.create(operation,new TrialOperationVOES());
        request.setBatchNum(Long.valueOf(batchNumSt));
        request.setCampaignType(campaignDO.getMktCampaignType());
        request.setLanId(campaignDO.getLanId());
        request.setCamLevel(campaignDO.getCamLevel());
        // 获取创建人员code
        request.setStaffCode("SYS987329864");


        //获取销售品及规则列表
        TrialOperationParamES param = getTrialOperationParamES(operation, Long.valueOf(batchNumSt), ruleId,false);
        ArrayList<TrialOperationParamES> paramESList = new ArrayList<>();
        paramESList.add(param);
        request.setParamList(paramESList);
        new Thread(){
            public void run(){
                try {
                    TrialResponseES responseES = esService.issueByFile(request);
//                   TrialResponseES responseES =  restTemplate.postForObject("http://localhost:8080/es/issueByFile",request,TrialResponseES.class);
                }catch (Exception e){
                    e.printStackTrace();
                    logger.info("导入清单下发失败");
                }
            }
        }.run();
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg", "导入成功");
        return result;
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
        String batchNumSt = DateUtil.date2St4Trial(new Date()) + ChannelUtil.getRandomStr(4);
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
        MktStrategyConfDO strategy = strategyMapper.selectByPrimaryKey(operationVO.getStrategyId());
        if (strategy==null){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "策略信息有误");
            return result;
        }
        String[] fieldList = getStrings(campaign,strategy);


        TrialOperationVO request = BeanUtil.create(operationVO,new TrialOperationVO());
        request.setBatchNum(trialOperation.getBatchNum());
        request.setFieldList(fieldList);
        //策略试运算
        request.setSample(true);
        TrialOperationVOES requests = BeanUtil.create(request,new TrialOperationVOES());
        //todo 待测试
        ArrayList<TrialOperationParamES> paramList = new ArrayList<>();
        List<MktStrategyConfRuleRelDO> ruleRelList = ruleRelMapper.selectByMktStrategyConfId(operationVO.getStrategyId());
        for (MktStrategyConfRuleRelDO ruleRelDO : ruleRelList) {
            TrialOperationParamES param = getTrialOperationParamES(operationVO, trialOperation.getBatchNum(), ruleRelDO.getMktStrategyConfRuleId(),true);
            List<LabelResultES> labelResultList = param.getLabelResultList();
            List<String> labelTypeList = new ArrayList<>();
            for (LabelResultES la : labelResultList){
                labelTypeList.add(la.getRightOperand());
            }
            if (!labelTypeList.contains("2000")){
                result.put("resultCode", CODE_FAIL);
                result.put("resultMsg", "规则："+param.getRuleName()+"不满足查询条件，请至少配置一条用户级标签查询条件！");
                return result;
            }
            paramList.add(param);
        }
        requests.setParamList(paramList);
//        TrialResponse response = new TrialResponse();
//        TrialResponse countResponse = new TrialResponse();
        TrialResponseES response = new TrialResponseES();
        TrialResponseES countResponse = new TrialResponseES();

        try {
            //todo
            System.out.println(JSON.toJSONString(requests));
            response = esService.searchBatchInfo(requests);

//            response = restTemplate.postForObject("http://localhost:8080/es/searchBatchInfo", requests, TrialResponseES.class);
            //同时调用统计查询的功能

//             countResponse = esService.searchCountInfo(requests);
//            countResponse = restTemplate.postForObject(countInfo,request,TrialResponse.class);

//            if (countResponse.getResultCode().equals(CODE_SUCCESS)){
//                redisUtils.set("HITS_COUNT_INFO_"+request.getBatchNum(),countResponse.getHitsList());
//            }
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

    private String[] getStrings(MktCampaignDO campaign,MktStrategyConfDO strategy) {
        // 通过活动id获取关联的标签字段数组
        DisplayColumn req = new DisplayColumn();
        req.setDisplayColumnId(campaign.getCalcDisplay());
        Map<String, Object> labelMap = messageLabelService.queryLabelListByDisplayId(req);
        List<LabelDTO> labelDTOList = (List<LabelDTO>) labelMap.get("labels");
        List<String> codeList = new ArrayList<>();
        for (LabelDTO labelDTO : labelDTOList) {
            codeList.add(labelDTO.getLabelCode());
        }
        List<String> ruleCodeList = (List<String>) redisUtils.hgetAllRedisList("LABEL_CODE_"+strategy.getMktStrategyConfId());
        logger.info("*********** 试算获取全部标签条件编码 ："+JSON.toJSONString(ruleCodeList));
        //添加固定查询标签
        if (!codeList.contains("ACCS_NBR")){
            codeList.add("ACCS_NBR");
        }if (!codeList.contains("LATN_NAME")){
            codeList.add("LATN_NAME");
        }if (!codeList.contains("CCUST_NAME")){
            codeList.add("CCUST_NAME");
        } if (!codeList.contains("CCUST_ID")){
            codeList.add("CCUST_ID");
        } if (!codeList.contains("CCUST_TEL")){
            codeList.add("CCUST_TEL");
        } if (!codeList.contains("LATN_ID")){
            codeList.add("LATN_ID");
        }if (!codeList.contains("CCUST_ROW_ID")){
            codeList.add("CCUST_ROW_ID");
        }if (!codeList.contains("ASSET_NUMBER")){
            codeList.add("ASSET_NUMBER");
        }
        //策略下所有分群条件加入
        if (ruleCodeList!=null){
            for (String labelCode : ruleCodeList){
                if (codeList.contains(labelCode)){
                    continue;
                }
                codeList.add(labelCode);
            }
        }
        String[] fieldList = new String[codeList.size()];
        for (int i = 0; i < codeList.size(); i++) {
            fieldList[i] = codeList.get(i);
        }
        return fieldList;
    }


//    private TrialOperationParam getTrialOperationParam(TrialOperationVO operationVO, Long batchNum, Long ruleId, boolean isSample) {
//        TrialOperationParam param = new TrialOperationParam();
//        param.setRuleId(ruleId);
//        MktStrategyConfRuleDO confRule = ruleMapper.selectByPrimaryKey(ruleId);
//        if (confRule != null) {
//            param.setRuleName(confRule.getMktStrategyConfRuleName());
//            param.setTarGrpId(confRule.getTarGrpId());
//        }
//        if (!isSample){
//            // 获取规则信息
//            Map<String, Object> mktStrategyConfRuleMap = mktStrategyConfRuleService.getMktStrategyConfRule(ruleId);
//            MktStrategyConfRule mktStrategyConfRule = (MktStrategyConfRule) mktStrategyConfRuleMap.get("mktStrategyConfRule");
//
//            // 获取销售品集合
//            Map<String, Object> productRuleListMap = productService.getProductRuleList(UserUtil.loginId(), mktStrategyConfRule.getProductIdlist());
//            List<MktProductRule> mktProductRuleList = (List<MktProductRule>) productRuleListMap.get("resultMsg");
//            param.setMktProductRuleList(mktProductRuleList);
//
//            // 获取推送渠道
//            List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
//            List<MktCamChlConfDetail> mktCamChlConfList = mktStrategyConfRule.getMktCamChlConfDetailList();
//            if (mktCamChlConfList != null) {
//                for (MktCamChlConfDetail mktCamChlConf : mktCamChlConfList) {
//                    Map<String, Object> mktCamChlConfDetailMap = mktCamChlConfService.getMktCamChlConf(mktCamChlConf.getEvtContactConfId());
//                    MktCamChlConfDetail mktCamChlConfDetail = (MktCamChlConfDetail) mktCamChlConfDetailMap.get("mktCamChlConfDetail");
//                    mktCamChlConfDetailList.add(mktCamChlConfDetail);
//                }
//            }
//            param.setMktCamChlConfDetailList(mktCamChlConfDetailList);
//        }
//        // 设置批次号
//        param.setBatchNum(batchNum);
//        //redis取规则
//        Object ruleOb = redisUtils.get("EVENT_RULE_" + operationVO.getCampaignId() + "_" + operationVO.getStrategyId() + "_" + ruleId);
//        String rule = "";
//        if (ruleOb!=null){
//            rule = ruleOb.toString();
//            System.out.println("*************************" + rule);
//        }
//        param.setRule(rule);
//        List<LabelResult> labelResultList = new ArrayList<>();
//        if (redisUtils.get("EVENT_RULE_" + operationVO.getCampaignId() + "_" + operationVO.getStrategyId() + "_" + ruleId+"_LABEL")!=null){
//            JSONArray objects = JSONArray.parseArray((String) redisUtils.get("EVENT_RULE_" + operationVO.getCampaignId() + "_" + operationVO.getStrategyId() + "_" + ruleId+"_LABEL"));
//            labelResultList = objects.toJavaList(LabelResult.class);
//        }
//        param.setLabelResultList(labelResultList);
//        return param;
//    }


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
        TrialResponseES response = new TrialResponseES();
//        TrialResponse response = new TrialResponse();
        try {
            Map<String, Long> param = new HashMap<>();
            param.put("batchId", operation.getBatchNum());
            response = esService.findBatchHitsList(operation.getBatchNum().toString());
//            response = restTemplate.postForObject("http://localhost:8080/es/findBatchHitsList",param, TrialResponseES.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("试算清单记录查询失败{}",operation.getBatchNum());
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
            Map<String, Object> map = new HashMap<>();
            TrialOperationParamES ruleInfoMap = new TrialOperationParamES();
            if (hitMap.get("ruleInfo") != null) {
                ruleInfoMap = (TrialOperationParamES) hitMap.get("ruleInfo");
            }

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
            map.put("ruleId", ruleInfoMap.getRuleId());
            map.put("ruleName", ruleInfoMap.getRuleName());
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
        int timeLimit = 1200000;
        List<SysParams> sysParams = sysParamsMapper.listParamsByKeyForCampaign("TRIAL_TIME");
        if (sysParams.get(0)!=null){
            timeLimit = Integer.valueOf(sysParams.get(0).getParamValue());
        }
        Date upTime = new Date(new Date().getTime() - timeLimit);
        List<TrialOperation> operationList = trialOperationMapper.listOperationByUpdateTime(upTime);
        if (!operationList.isEmpty()){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "正在下发文件 请稍后再试。");
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
        if(!StatusCode.STATUS_CODE_PUBLISHED.getStatusCode().equals(campaignDO.getStatusCd())){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "发布活动后才能下发");
            return result;
        }
        MktStrategyConfDO strategyConfDO = strategyConfMapper.selectByPrimaryKey(operation.getStrategyId());
        if (strategyConfDO==null){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "策略不存在");
            return result;
        }
        //添加策略适用地市
        redisUtils.set("STRATEGY_CONF_AREA_"+operation.getStrategyId(),strategyConfDO.getAreaId());
        // 通过活动id获取关联的标签字段数组
        DisplayColumn req = new DisplayColumn();
        req.setDisplayColumnId(campaignDO.getCalcDisplay());
        Map<String,Object> labelMap = messageLabelService.queryLabelListByDisplayId(req);
        List<LabelDTO> labelDTOList = (List<LabelDTO>)labelMap.get("labels");
        String[] fieldList = new String[labelDTOList.size()];
        List<Map<String,Object>> labelList = new ArrayList<>();
        for (int i = 0 ; i< labelDTOList.size();i++){
            fieldList[i] = labelDTOList.get(i).getLabelCode();
            Map<String,Object> label = new HashMap<>();
            label.put("code",labelDTOList.get(i).getLabelCode());
            label.put("name",labelDTOList.get(i).getInjectionLabelName());
            label.put("labelType",labelDTOList.get(i).getLabelType());
            labelList.add(label);
        }
        redisUtils.set("LABEL_DETAIL_"+trialOperation.getBatchNum(),labelList);

        TrialOperationVO request = BeanUtil.create(trialOperation,new TrialOperationVO());
        request.setFieldList(fieldList);
        request.setCampaignType(campaignDO.getMktCampaignType());
        request.setLanId(campaignDO.getLanId());
        request.setCamLevel(campaignDO.getCamLevel());
        // 获取创建人员code
        request.setStaffCode("SYS827364823");

         TrialOperationVOES requests = BeanUtil.create(request,new TrialOperationVOES());
        //todo 待测试
        ArrayList<TrialOperationParamES> paramList = new ArrayList<>();
        List<MktStrategyConfRuleRelDO> ruleRelList = ruleRelMapper.selectByMktStrategyConfId(request.getStrategyId());
        for (MktStrategyConfRuleRelDO ruleRelDO : ruleRelList) {
            TrialOperationParamES param = getTrialOperationParamES(request, trialOperation.getBatchNum(), ruleRelDO.getMktStrategyConfRuleId(),false);
            List<LabelResultES> labelResultList = param.getLabelResultList();
            List<String> labelTypeList = new ArrayList<>();
            for (LabelResultES la : labelResultList){
                labelTypeList.add(la.getRightOperand());
            }
            if (!labelTypeList.contains("2000")){
                result.put("resultCode", CODE_FAIL);
                result.put("resultMsg", "规则："+param.getRuleName()+"不满足查询条件，请至少配置一条用户级标签查询条件！");
                return result;
            }
            paramList.add(param);
        }
        requests.setParamList(paramList);
        final  TrialOperationVOES  issureRequest = requests;
        System.out.println(JSON.toJSONString(requests));
        try {
            new Thread(){
                public void run(){
                   esService.strategyIssure(issureRequest);
//                    TrialResponseES res  =  restTemplate.postForObject("http://localhost:8080/es/cpcMatchFileToFtp", issureRequest, TrialResponseES.class);
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "文件下发成功，稍后请联系相关业务人员校验结果");
            return result;
        }
        //更新试算记录状态和时间
        trialOperation.setUpdateDate(new Date());
        trialOperation.setStatusCd(StatusCode.STATUS_CODE_ARCHIVED.getStatusCode());
        trialOperationMapper.updateByPrimaryKey(trialOperation);
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", "文件下发成功，稍后请联系相关业务人员校验结果");
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
//                Double cost =  ((double)(trialOperation.getUpdateDate().getTime() - trialOperation.getCreateDate().getTime()) / 1000);
//                DecimalFormat df = new DecimalFormat("#.00");
                Long cost = (trialOperation.getUpdateDate().getTime() - trialOperation.getCreateDate().getTime());
                detail.setCost(cost + "ms");
            }
            operationDetailList.add(detail);
        }
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", operationDetailList);
        return result;
    }


    /**
     * 下发参数
     * @param operationVO
     * @param batchNum
     * @param ruleId
     * @param isSample
     * @return
     */
    private TrialOperationParamES getTrialOperationParamES(TrialOperationVO operationVO, Long batchNum, Long ruleId, boolean isSample) {
        TrialOperationParamES param = new TrialOperationParamES();
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
            ArrayList<MktProductRuleES> mktProductRuleEsList = new ArrayList<>();
            for (MktProductRule rule : mktProductRuleList){
                MktProductRuleES es = BeanUtil.create(rule,new MktProductRuleES());
                es.setPriority(es.getPriority()==null ? 0L : es.getPriority());
                mktProductRuleEsList.add(es);
            }
            param.setMktProductRuleList(mktProductRuleEsList);

            // 获取推送渠道
            List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
            ArrayList<MktCamChlConfDetailES> mktCamChlConfDetaiEslList = new ArrayList<>();
            List<MktCamChlConfDetail> mktCamChlConfList = mktStrategyConfRule.getMktCamChlConfDetailList();
            if(mktStrategyConfRule.getMktCamChlResultList()!=null){
                for (MktCamChlResult mktCamChlResult:mktStrategyConfRule.getMktCamChlResultList()) {
                    if(mktCamChlResult.getMktCamChlConfDetailList()!=null){
                        for (MktCamChlConfDetail mktCamChlConfDetail : mktCamChlResult.getMktCamChlConfDetailList()) {
                            mktCamChlConfList.add(mktCamChlConfDetail);
                        }
                    }
                }
            }
            if (mktCamChlConfList != null) {
                for (MktCamChlConfDetail mktCamChlConf : mktCamChlConfList) {
                    Map<String, Object> mktCamChlConfDetailMap = mktCamChlConfService.getMktCamChlConf(mktCamChlConf.getEvtContactConfId());
                    MktCamChlConfDetail mktCamChlConfDetail = (MktCamChlConfDetail) mktCamChlConfDetailMap.get("mktCamChlConfDetail");
                    MktCamChlConfDetailES es = BeanUtil.create(mktCamChlConfDetail,new MktCamChlConfDetailES());
                    CamScriptES camScriptES = BeanUtil.create(mktCamChlConfDetail.getCamScript(),new CamScriptES());
                    es.setCamScript(camScriptES);
                    ArrayList<MktCamChlConfAttrES> attrs = new ArrayList<>();
                    ArrayList<VerbalVOES> verbalES = new ArrayList<>();
                    if (mktCamChlConfDetail.getMktCamChlConfAttrList()!=null){
                        for (MktCamChlConfAttr attr : mktCamChlConfDetail.getMktCamChlConfAttrList()){
                            MktCamChlConfAttrES attrES = BeanUtil.create(attr,new MktCamChlConfAttrES());
                            attrs.add(attrES);
                        }
                    }
                    if (mktCamChlConfDetail.getVerbalVOList()!=null){
                        for (VerbalVO verbalVO : mktCamChlConfDetail.getVerbalVOList()){
                            VerbalVOES verbalVOES = BeanUtil.create(verbalVO,new VerbalVOES());
                            verbalES.add(verbalVOES);
                        }
                    }
                    es.setVerbalVOList(verbalES);
                    es.setMktCamChlConfAttrList(attrs);
                    mktCamChlConfDetaiEslList.add(es);
                }
            }
            param.setMktCamChlConfDetailList(mktCamChlConfDetaiEslList);
        }
        // 设置批次号
        param.setBatchNum(batchNum);
        //redis取规则
        String rule = "";
        List<LabelResult> labelResultList = new ArrayList<>();
        ArrayList<LabelResultES> labelResultES = new ArrayList<>();

        //获取规则
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            MktStrategyConfRuleDO ruleDO = ruleMapper.selectByPrimaryKey(ruleId);
            if (ruleDO!=null){
                Future<Map<String, Object>> future = executorService.submit(new TarGrpRuleTask(operationVO.getCampaignId(),operationVO.getStrategyId(), ruleDO, redisUtils, tarGrpConditionMapper, injectionLabelMapper));
                rule = future.get().get("express").toString();
                labelResultList = ( List<LabelResult>)future.get().get("labelResultList");
            }
            // 关闭线程池
            if (!executorService.isShutdown()) {
                executorService.shutdown();
            }
        }catch (Exception e){
            // 关闭线程池
            if (!executorService.isShutdown()) {
                executorService.shutdown();
            }
        }
        System.out.println("*************************" + rule);
        param.setRule(rule);
        for (LabelResult labelResult : labelResultList){
            LabelResultES labelEs = BeanUtil.create(labelResult,new LabelResultES());
            labelResultES.add(labelEs);
        }
        param.setLabelResultList(labelResultES);
        return param;
    }

    class TarGrpRuleTask implements Callable<Map<String,Object>>{
        private Long mktCampaignId;

        private Long mktStrategyConfId;

        private MktStrategyConfRuleDO mktStrategyConfRuleDO;

        private RedisUtils redisUtils;

        private TarGrpConditionMapper tarGrpConditionMapper;

        private InjectionLabelMapper injectionLabelMapper;

        public TarGrpRuleTask(Long mktCampaignId, Long mktStrategyConfId, MktStrategyConfRuleDO mktStrategyConfRuleDO, RedisUtils redisUtils, TarGrpConditionMapper tarGrpConditionMapper, InjectionLabelMapper injectionLabelMapper) {
            this.mktCampaignId = mktCampaignId;
            this.mktStrategyConfId = mktStrategyConfId;
            this.mktStrategyConfRuleDO = mktStrategyConfRuleDO;
            this.redisUtils = redisUtils;
            this.tarGrpConditionMapper = tarGrpConditionMapper;
            this.injectionLabelMapper = injectionLabelMapper;
        }

        @Override
        public Map<String, Object> call() {
            Map<String, Object> result = new HashMap<>();
            // 策略配置规则Id
            Long mktStrategyConfRuleId = mktStrategyConfRuleDO.getMktStrategyConfRuleId();
            //  2.判断活动的客户分群规则---------------------------
            //查询分群规则list
            Long tarGrpId = mktStrategyConfRuleDO.getTarGrpId();
            List<TarGrpCondition> tarGrpConditionDOs = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
            List<LabelResult> labelResultList = new ArrayList<>();
            List<String> codeList = new ArrayList<>();

            StringBuilder express = new StringBuilder();
            if (tarGrpId != null && tarGrpId != 0) {
                //将规则拼装为表达式
                if (tarGrpConditionDOs != null && tarGrpConditionDOs.size() > 0) {
                    express.append("if(");
                    //遍历所有规则
                    for (int i = 0; i < tarGrpConditionDOs.size(); i++) {
                        LabelResult labelResult = new LabelResult();
                        String type = tarGrpConditionDOs.get(i).getOperType();
                        Label label = injectionLabelMapper.selectByPrimaryKey(Long.parseLong(tarGrpConditionDOs.get(i).getLeftParam()));

                        labelResult.setLabelCode(label.getInjectionLabelCode());
                        labelResult.setLabelName(label.getInjectionLabelName());
                        labelResult.setRightOperand(label.getLabelType());
                        labelResult.setRightParam(tarGrpConditionDOs.get(i).getRightParam());
                        labelResult.setClassName(label.getClassName());
                        labelResult.setOperType(type);
                        labelResult.setLabelDataType(label.getLabelDataType()==null ? "1100" : label.getLabelDataType());
                        labelResultList.add(labelResult);
                        codeList.add(label.getInjectionLabelCode());
                        if ("7100".equals(type)) {
                            express.append("!");
                        }
                        express.append("(");
                        express.append(label.getInjectionLabelCode());
                        if ("1000".equals(type)) {
                            express.append(">");
                        } else if ("2000".equals(type)) {
                            express.append("<");
                        } else if ("3000".equals(type)) {
                            express.append("==");
                        } else if ("4000".equals(type)) {
                            express.append("!=");
                        } else if ("5000".equals(type)) {
                            express.append(">=");
                        } else if ("6000".equals(type)) {
                            express.append("<=");
                        } else if ("7000".equals(type)) {
                            express.append("in");
                        }else if ("7200".equals(type)) {
                            express.append("@@@@");//区间于
                        } else if ("7100".equals(type)) {
                            express.append("notIn");
                        }
                        express.append(tarGrpConditionDOs.get(i).getRightParam());
                        express.append(")");
                        if (i + 1 != tarGrpConditionDOs.size()) {
                            express.append("&&");
                        }
                    }
                    express.append(") {return true} else {return false}");
                }else {
                    express.append("");
                }
                // 将表达式存入Redis
                String key = "EVENT_RULE_" + mktCampaignId + "_" + mktStrategyConfId + "_" + mktStrategyConfRuleId;
                System.out.println("key>>>>>>>>>>" + key + ">>>>>>>>express->>>>:" + JSON.toJSONString(express));
                redisUtils.set(key, express);

                //标签条件编码集合 试算展示用
                redisUtils.hset("LABEL_CODE_"+mktStrategyConfId,tarGrpId+"",codeList);
                System.out.println("TAR_GRP_ID>>>>>>>>>>" + tarGrpId + ">>>>>>>>codeList->>>>:" + JSON.toJSONString(codeList));

                // 将所有的标签集合存入redis
                redisUtils.set(key + "_LABEL", JSON.toJSONString(labelResultList));
            }
            result.put("express",express.toString());
            result.put("labelResultList",labelResultList);
            return result;
        }

    }
}
