package com.zjtelcom.cpct.service.impl.grouping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ctzj.smt.bss.sysmgr.model.common.SysmgrResultObject;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.ctzj.smt.bss.sysmgr.privilege.service.dubbo.api.ISystemUserDtoDubboService;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamChlConfMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamItemMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TrialOperationMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.Rule;
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
import com.zjtelcom.cpct.enums.TrialCreateType;
import com.zjtelcom.cpct.enums.TrialStatus;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.MktCamChlConfService;
import com.zjtelcom.cpct.service.channel.MessageLabelService;
import com.zjtelcom.cpct.service.channel.ProductService;
import com.zjtelcom.cpct.service.grouping.TrialOperationService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfRuleService;
import com.zjtelcom.cpct.util.*;
import com.zjtelcom.cpct_prod.dao.offer.MktResourceProdMapper;
import com.zjtelcom.cpct_prod.dao.offer.OfferProdMapper;
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
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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
    private OfferProdMapper offerMapper;
    @Autowired
    private MktCamChlConfMapper chlConfMapper;
    @Autowired
    private MktCamScriptMapper scriptMapper;
    @Autowired
    private SysParamsMapper sysParamsMapper;
    @Autowired(required = false)
    private EsService esService;
    @Autowired
    private MktResourceProdMapper resourceMapper;
    @Autowired
    private MktStrategyConfMapper strategyConfMapper;
    @Autowired
    private MktCamCustMapper camCustMapper;

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
    @Autowired(required = false)
    private ISystemUserDtoDubboService iSystemUserDtoDubboService;




    private String getCreater(Long createStaff){
        String codeNumber = null;
        try {
            // 获取创建人信息
            SysmgrResultObject<SystemUserDto> systemUserDtoSysmgrResultObject = iSystemUserDtoDubboService.qrySystemUserDto(createStaff, new ArrayList<Long>());
            if (systemUserDtoSysmgrResultObject != null) {
                if (systemUserDtoSysmgrResultObject.getResultObject() != null) {
                    codeNumber = systemUserDtoSysmgrResultObject.getResultObject().getSysUserCode();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            codeNumber = null;
        }
        return codeNumber;
    }


    /**
     * 查询试算记录日志
     * @param batchId
     * @return
     */
    @Override
    public Map<String, Object> trialLog(Long batchId) {
        Map<String, Object> result = new HashMap<>();
        TrialOperation operation = trialOperationMapper.selectByPrimaryKey(batchId);
        if (operation == null) {
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "试运算记录不存在");
            return result;
        }
        TrialOperationVOES request = new TrialOperationVOES();
        request.setBatchNum(operation.getBatchNum());
        TrialResponseES responseES = new TrialResponseES();
        try {
//            responseES = restTemplate.postForObject("http://localhost:8080/es/trialLog", request, TrialResponseES.class);
            responseES = esService.trialLog(request);
        }catch (Exception e){
            logger.error("日志查询失败");
            e.printStackTrace();
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","日志查询失败");
            return result;
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",responseES.getHitsList());
        return result;
    }

    /**
     * 下发文件到生产sftp
     * @param batchId
     * @return
     */
    @Override
    public Map<String, Object> uploadFile(Long batchId) {
        Map<String, Object> result = new HashMap<>();
        TrialOperation operation = trialOperationMapper.selectByPrimaryKey(batchId);
        if (operation == null) {
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "试运算记录不存在");
            return result;
        }
        if (!TrialStatus.ALL_SAMPEL_SUCCESS.getValue().equals(operation.getStatusCd()) && !TrialStatus.IMPORT_SUCCESS.getValue().equals(operation.getStatusCd())){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "不满足下发条件，无法操作");
            return result;
        }
        TrialOperationVOES request = new TrialOperationVOES();
        request.setCampaignId(operation.getCampaignId());
        request.setStrategyId(operation.getStrategyId());
        request.setBatchNum(operation.getBatchNum());
        new Thread(){
            public void  run(){
                try {
                    //todo 新的dubbo接口
                    TrialResponseES responseES = esService.uploadFile2Prod(request);
                }catch (Exception e){
                    e.printStackTrace();
                    logger.info("下发文件失败");
                }
            }
        }.start();
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", "文件已下发，请稍后查看下发结果");
        return result;
    }

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
        //生成批次号
        String batchNumSt = DateUtil.date2St4Trial(new Date()) + ChannelUtil.getRandomStr(4);
        //添加策略适用地市
        redisUtils.set("STRATEGY_CONF_AREA_"+operationVO.getStrategyId(),strategy.getAreaId());
        // 通过活动id获取关联的标签字段数组
        DisplayColumn req = new DisplayColumn();
        req.setDisplayColumnId(campaign.getCalcDisplay());
        Map<String,Object> labelMap = messageLabelService.queryLabelListByDisplayId(req);
        List<LabelDTO> labelDTOList = (List<LabelDTO>)labelMap.get("labels");
        String[] displays = new String[labelDTOList.size()];
        List<Map<String,Object>> labelList = new ArrayList<>();
        for (int i = 0 ; i< labelDTOList.size();i++){
            displays[i] = labelDTOList.get(i).getLabelCode();
            Map<String,Object> label = new HashMap<>();
            label.put("code",labelDTOList.get(i).getLabelCode());
            label.put("name",labelDTOList.get(i).getInjectionLabelName());
            label.put("labelType",labelDTOList.get(i).getLabelType());
            labelList.add(label);
        }
        redisUtils.set("LABEL_DETAIL_"+batchNumSt,labelList);

        String[] fieldList = getStrings(campaign,strategy);

        TrialOperationVO request = BeanUtil.create(operationVO,new TrialOperationVO());
        //抽样业务校验
        request.setSample(false);
        TrialOperationVOES requests = BeanUtil.create(request,new TrialOperationVOES());

        ArrayList<TrialOperationParamES> paramList = new ArrayList<>();
        List<MktStrategyConfRuleRelDO> ruleRelList = ruleRelMapper.selectByMktStrategyConfId(operationVO.getStrategyId());
        for (MktStrategyConfRuleRelDO ruleRelDO : ruleRelList) {
            TrialOperationParamES param = getTrialOperationParamES(operationVO,Long.valueOf(batchNumSt), ruleRelDO.getMktStrategyConfRuleId(),true);
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
        requests.setBatchNum(Long.valueOf(batchNumSt));
        TrialResponseES response = new TrialResponseES();

        try {
            //todo
            System.out.println(JSON.toJSONString(requests));
             response = esService.searchBatchInfo(requests);
//            response = restTemplate.postForObject("http://localhost:8080/es/searchBatchInfo", requests, TrialResponseES.class);

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
     * ppm-文件下发
     * @param batchId
     * @return
     */
    @Override
    public Map<String, Object> importFromCust4Ppm(Long batchId) {
        Map<String, Object> result = new HashMap<>();
        TrialOperation trialOperation = trialOperationMapper.selectByPrimaryKey(batchId);
        if (trialOperation == null) {
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "试运算记录不存在");
            return result;
        }
        if (!trialOperation.getStatusCd().equals(TrialStatus.PPM_IMPORT_GOING.getValue())){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "无法导入");
            return result;
        }
        TrialOperationVO operation = BeanUtil.create(trialOperation,new TrialOperationVO());
        MktStrategyConfRuleDO confRule = ruleMapper.selectByPrimaryKey(operation.getStrategyId());
        if (confRule==null) {
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "未找到有效的活动策略或规则");
            return result;
        }
        Long tarGrpTempId = null;
        Long ruleId = operation.getStrategyId();
        String batchNumSt = operation.getBatchNum().toString();
        List<Map<String,Object>> customerList = new ArrayList<>();
        List<Map<String,Object>> labelList = new ArrayList<>();
        List<Map<String, String>> labelMapList = tarGrpConditionMapper.selectAllLabelByTarId(confRule.getTarGrpId());
        if (!labelMapList.isEmpty()){
            tarGrpTempId = Long.valueOf(labelMapList.get(0).get("rightParam"));
            List<MktCamCust> camCustList = camCustMapper.selectByTarGrpTempId(tarGrpTempId);
            if (camCustList!=null && !camCustList.isEmpty()){
               JSONArray list = (JSONArray) JSON.parse(camCustList.get(0).getRemark());
               if (list!=null){
                   for (Object map : list){
                       labelList.add((Map<String, Object>) map);
                   }
               }
                for (MktCamCust camCust : camCustList){
                    Map<String,Object> jsonObject = (Map<String,Object>)JSON.parse(camCust.getAttrValue());
                    customerList.add(jsonObject);
                }
            }
        }
        return importUserList(result, operation, ruleId, batchNumSt, customerList, labelList);
    }

    //下发文件
    private Map<String, Object> importUserList(Map<String, Object> result, TrialOperationVO operation, Long ruleId, String batchNumSt, List<Map<String, Object>> customerList, List<Map<String, Object>> labelList) {
        redisUtils.set("LABEL_DETAIL_"+batchNumSt,labelList);
        int num = (customerList.size() / 100) + 1;
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
        MktStrategyConfDO strategyConfDO = strategyConfMapper.selectByPrimaryKey(operation.getStrategyId());
        if (campaignDO==null){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "策略不存在");
            return result;
        }
        final TrialOperationVOES request = BeanUtil.create(operation,new TrialOperationVOES());
        request.setBatchNum(Long.valueOf(batchNumSt));
        request.setCampaignType(campaignDO.getMktCampaignType());
        request.setLanId(campaignDO.getLanId());
        request.setCampaignName(campaignDO.getMktCampaignName());
        request.setCamLevel(campaignDO.getCamLevel());
        request.setStrategyName(strategyConfDO.getMktStrategyConfName());
        // 获取创建人员code
        request.setStaffCode(getCreater(campaignDO.getCreateStaff())==null ? "null" : getCreater(campaignDO.getCreateStaff()));

        //获取销售品及规则列表
        TrialOperationParamES param = getTrialOperationParamES(operation, Long.valueOf(batchNumSt), ruleId,false);
        ArrayList<TrialOperationParamES> paramESList = new ArrayList<>();
        paramESList.add(param);
        request.setParamList(paramESList);
        System.out.println(JSON.toJSONString(request));
        new Thread(){
            public void run(){
                try {
                    TrialResponseES responseES = esService.issueByFile(request);
                }catch (Exception e){
                    e.printStackTrace();
                    logger.info("导入清单下发失败");
                }
            }
        }.start();
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg", "导入成功,请稍后查看结果");
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

        MktCampaignDO campaign = campaignMapper.selectByPrimaryKey(operation.getCampaignId());
        MktStrategyConfDO strategy = strategyMapper.selectByPrimaryKey(operation.getStrategyId());
        MktStrategyConfRuleDO confRule = ruleMapper.selectByPrimaryKey(ruleId);
        if (campaign == null || strategy == null || confRule==null) {
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "未找到有效的活动策略或规则");
            return result;
        }
        TrialOperation trialOp = BeanUtil.create(operation, new TrialOperation());
        trialOp.setCampaignName(campaign.getMktCampaignName());
        //当清单导入时 strategyId name 存储规则信息
        trialOp.setStrategyId(confRule.getMktStrategyConfRuleId());
        trialOp.setStrategyName(confRule.getMktStrategyConfRuleName());
        trialOp.setBatchNum(Long.valueOf(batchNumSt));
        trialOp.setStatusCd(TrialStatus.IMPORT_GOING.getValue());
        trialOp.setStatusDate(new Date());
        trialOp.setCreateStaff(TrialCreateType.IMPORT_USER_LIST.getValue());
        trialOperationMapper.insert(trialOp);

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
        for (int i = 3; i < rowNums ; i++) {
            Map<String, Object> customers = new HashMap<>();
            Row rowCode = sheet.getRow(1);
            Row row = sheet.getRow(i);
            System.out.println("处理--------："+i);
            if (row==null){
                System.out.println("这一行是空的："+i);
                continue;
            }
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cellTitle = rowCode.getCell(j);
                Cell cell = row.getCell(j);
                if (cellTitle.getStringCellValue().equals("CCUST_ID") && ChannelUtil.getCellValue(cell).equals("null")){
                    continue;
                }
                if (cellTitle.getStringCellValue().equals("ASSET_INTEG_ID") && ChannelUtil.getCellValue(cell).equals("null")){
                    continue;
                }
                if (cellTitle.getStringCellValue().equals("ASSET_NUMBER") && ChannelUtil.getCellValue(cell).equals("null")){
                    continue;
                }
                customers.put(cellTitle.getStringCellValue(), ChannelUtil.getCellValue(cell));
            }
            if (!customers.isEmpty()){
                customerList.add(customers);
            }
        }
        for (int i = 0 ; i< labelDTOList.size();i++){
            Map<String,Object> label = new HashMap<>();
            label.put("code",labelDTOList.get(i).getLabelCode());
            label.put("name",labelDTOList.get(i).getInjectionLabelName());
            labelList.add(label);
        }
        new Thread(){
            public void run(){
                try {
                    importUserList(result, operation, ruleId, batchNumSt, customerList, labelList);
                }catch (Exception e){
                    e.printStackTrace();
                    logger.error("导入失败");
                }
            }
        }.start();
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg", "导入成功,请稍后查看结果");
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

        String[] statusCd  = new String[1];
        statusCd[0] = TrialStatus.SAMPEL_GOING.getValue();
        Date createTime = new Date(new Date().getTime() - 600000);
        List<TrialOperation> operationCheck = trialOperationMapper.listOperationByCreateTime(null,createTime,statusCd);
        if (operationCheck!=null && !operationCheck.isEmpty()){
            for (TrialOperation operation : operationCheck){
                operation.setStatusCd(TrialStatus.SAMPEL_FAIL.getValue());
                trialOperationMapper.updateByPrimaryKey(operation);
            }
        }
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
        List<MktStrategyConfRuleDO> ruleList = ruleMapper.selectByMktStrategyConfId(operationVO.getStrategyId());
        if (ruleList==null || ruleList.isEmpty()){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "未找到有效的规则信息");
            return result;
        }
        if (strategy.getAreaId()==null || "".equals(strategy.getAreaId())){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "请配置策略适用地市");
            return result;
        }
        String[] strings  = new String[1];
        strings[0] = TrialStatus.SAMPEL_GOING.getValue();
        List<TrialOperation> listOperationCheck = trialOperationMapper.listOperationCheck(strings);
        if (listOperationCheck.size()>5){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "同时抽样人数过多，请稍后再试");
            return result;
        }

        String[] status  = new String[3];
        status[0] = TrialStatus.SAMPEL_FAIL.getValue();
        status[1] = TrialStatus.SAMPEL_SUCCESS.getValue();
        status[2] = TrialStatus.SAMPEL_GOING.getValue();
        int timeLimit = 180000;
        List<SysParams> sysParams = sysParamsMapper.listParamsByKeyForCampaign("TRIAL_BATCH_TIME");
        if (sysParams.get(0)!=null){
            timeLimit = Integer.valueOf(sysParams.get(0).getParamValue());
        }
        Date upTime = new Date(new Date().getTime() - timeLimit);
        List<TrialOperation> operations = trialOperationMapper.listOperationByUpdateTime(operationVO.getCampaignId(),upTime,status);
        if (!operations.isEmpty()){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "相同活动3分钟只能抽样一次，请稍后再试");
            return result;
        }

        for (MktStrategyConfRuleDO rule : ruleList){
            List<String> labelTypeList = injectionLabelMapper.listLabelByRuleId(rule.getMktStrategyConfRuleId());
            if (labelTypeList == null || labelTypeList.isEmpty()){
                result.put("resultCode", CODE_FAIL);
                result.put("resultMsg", "请检查规则："+rule.getMktStrategyConfRuleName()+"条件配置");
                return result;
            }
            if (!labelTypeList.contains("2000")){
                result.put("resultCode", CODE_FAIL);
                result.put("resultMsg", "规则："+rule.getMktStrategyConfRuleName()+"请至少配置一条用户级条件");
                return result;
            }
        }

        TrialOperation trialOp = BeanUtil.create(operationVO, new TrialOperation());
        trialOp.setCampaignName(campaign.getMktCampaignName());
        trialOp.setStrategyName(strategy.getMktStrategyConfName());
        trialOp.setBatchNum(Long.valueOf(batchNumSt));
        trialOp.setStatusCd(TrialStatus.SAMPEL_GOING.getValue());
        trialOp.setStatusDate(new Date());
        trialOp.setUpdateDate(new Date());
        trialOp.setCreateStaff(TrialCreateType.TRIAL_OPERATION.getValue());
        trialOperationMapper.insert(trialOp);

        operationVO.setTrialId(trialOp.getId());
        operationVO.setCampaignName(campaign.getMktCampaignName());
        operationVO.setStrategyName(strategy.getMktStrategyConfName());
        List<TrialOperation> operationList = trialOperationMapper.findOperationListByStrategyId(operationVO.getStrategyId(),TrialCreateType.TRIAL_OPERATION.getValue());
        // 调用es的抽样接口
        final TrialOperationVO vo = operationVO;
        new Thread() {
            public void run() {
                Map<String,Object> resultMap = sampleFromES(vo);
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
        //添加策略适用地市
        redisUtils.set("STRATEGY_CONF_AREA_"+operationVO.getStrategyId(),strategy.getAreaId());

        // 通过活动id获取关联的标签字段数组
        DisplayColumn req = new DisplayColumn();
        req.setDisplayColumnId(campaign.getCalcDisplay());
        Map<String,Object> labelMap = messageLabelService.queryLabelListByDisplayId(req);
        List<LabelDTO> labelDTOList = (List<LabelDTO>)labelMap.get("labels");
        String[] displays = new String[labelDTOList.size()];
        List<Map<String,Object>> labelList = new ArrayList<>();
        for (int i = 0 ; i< labelDTOList.size();i++){
            displays[i] = labelDTOList.get(i).getLabelCode();
            Map<String,Object> label = new HashMap<>();
            label.put("code",labelDTOList.get(i).getLabelCode());
            label.put("name",labelDTOList.get(i).getInjectionLabelName());
            label.put("labelType",labelDTOList.get(i).getLabelType());
            labelList.add(label);
        }
        redisUtils.set("LABEL_DETAIL_"+trialOperation.getBatchNum(),labelList);

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
            paramList.add(param);
        }
        requests.setParamList(paramList);
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
        } catch (Exception e) {
            e.printStackTrace();
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
        // 通过活动id获取关联的标签字段数组
        MktCampaignDO campaignDO = campaignMapper.selectByPrimaryKey(trialOperation.getCampaignId());
        if (campaignDO==null){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "活动不存在");
            return result;
        }
        if(!StatusCode.STATUS_CODE_PUBLISHED.getStatusCode().equals(campaignDO.getStatusCd())){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "发布活动后才能全量试算");
            return result;
        }
        MktStrategyConfDO strategyConfDO = strategyConfMapper.selectByPrimaryKey(operation.getStrategyId());
        if (strategyConfDO==null){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "策略不存在");
            return result;
        }
        if (!operation.getStatusCd().equals(TrialStatus.SAMPEL_SUCCESS.getValue())){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "抽样试算失败，无法全量试算");
            return result;
        }
//        List<TrialOperation> operationList = trialOperationMapper.listOperationByStatusCd(TrialStatus.ALL_SAMPEL_GOING.getValue());
//        if (!operationList.isEmpty()){
//            result.put("resultCode", CODE_FAIL);
//            result.put("resultMsg", "系统正在全量试算 请稍后再试。");
//            return result;
//        }

        int timeLimit = 1800000;
        List<SysParams> sysParams = sysParamsMapper.listParamsByKeyForCampaign("TRIAL_TIME");
        if (sysParams.get(0)!=null){
            timeLimit = Integer.valueOf(sysParams.get(0).getParamValue());
        }
        Date upTime = new Date(new Date().getTime() - timeLimit);
        String[] status  = new String[2];
        status[0] = TrialStatus.ALL_SAMPEL_SUCCESS.getValue();
        status[1] = TrialStatus.ALL_SAMPEL_FAIL.getValue();

        List<TrialOperation> operations = trialOperationMapper.listOperationByUpdateTime(trialOperation.getCampaignId(),upTime,status);
        if (!operations.isEmpty()){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "相同活动30分钟只能全量试算一次，请稍后再试");
            return result;
        }
        BeanUtil.copy(operation,trialOperation);



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
        List<Map<String, Object>> iSaleDisplay = new ArrayList<>();
        iSaleDisplay = (List<Map<String, Object>>) redisUtils.get("EVT_ISALE_LABEL_" + campaignDO.getIsaleDisplay());
        if (iSaleDisplay == null) {
            iSaleDisplay = injectionLabelMapper.listLabelByDisplayId(campaignDO.getIsaleDisplay());
            redisUtils.set("EVT_ISALE_LABEL_" + campaignDO.getIsaleDisplay(), iSaleDisplay);
        }
        redisUtils.set("ISALE_LABEL_"+trialOperation.getBatchNum(),iSaleDisplay);

        TrialOperationVO request = BeanUtil.create(trialOperation,new TrialOperationVO());
        request.setFieldList(fieldList);
        request.setCampaignType(campaignDO.getMktCampaignType());
        request.setLanId(campaignDO.getLanId());
        request.setCamLevel(campaignDO.getCamLevel());
        // 获取创建人员code
        request.setStaffCode(getCreater(campaignDO.getCreateStaff())==null ? "null" : getCreater(campaignDO.getCreateStaff()));

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
        operation.setStatusCd(TrialStatus.ALL_SAMPEL_GOING.getValue());
        trialOperationMapper.updateByPrimaryKey(operation);
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
            result.put("resultMsg", "全量试算中，请稍后刷新页面查看结果");
            return result;
        }
        //更新试算记录状态和时间
        trialOperation.setStatusDate(new Date());
        trialOperation.setStatusCd(TrialStatus.ALL_SAMPEL_GOING.getValue());
        trialOperationMapper.updateByPrimaryKey(trialOperation);
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", "全量试算中，请稍后刷新页面查看结果");
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
     * 刷新列表(策略试运算)
     * @param strategyId
     * @return
     */
    @Override
    public Map<String, Object> getTrialListByStrategyId(Long strategyId) {
        Map<String, Object> result = new HashMap<>();
        List<TrialOperation> trialOperations = trialOperationMapper.findOperationListByStrategyId(strategyId,TrialCreateType.TRIAL_OPERATION.getValue());
        List<TrialOperationDetail> operationDetailList = supplementOperation(trialOperations);
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", operationDetailList);
        return result;
    }

    /**
     * 刷新列表(清单导入)
     * @param ruleId
     * @return
     */
    @Override
    public Map<String, Object> getTrialListByRuleId(Long ruleId) {
        Map<String, Object> result = new HashMap<>();
        List<TrialOperation> trialOperations = trialOperationMapper.findOperationListByStrategyId(ruleId,TrialCreateType.IMPORT_USER_LIST.getValue());
        List<TrialOperationDetail> operationDetailList = supplementOperation(trialOperations);
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", operationDetailList);
        return result;
    }

    //试算记录补充信息
    private List<TrialOperationDetail> supplementOperation(List<TrialOperation> trialOperations) {
        List<TrialOperationDetail> operationDetailList = new ArrayList<>();
        for (TrialOperation trialOperation : trialOperations) {
            TrialOperationDetail detail = BeanUtil.create(trialOperation, new TrialOperationDetail());
            if (trialOperation.getUpdateDate() != null && !trialOperation.getStatusCd().equals(TrialStatus.SAMPEL_GOING.getValue())) {
                Long cost = (trialOperation.getUpdateDate().getTime() - trialOperation.getCreateDate().getTime());
                cost = cost<0L ? 0L : cost;
                if (cost>1000){
                    detail.setCost(cost/1000 + "s");
                }else {
                    detail.setCost(cost+"ms");
                }

            }
            operationDetailList.add(detail);
        }
        return operationDetailList;
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
                        if (label==null){
                            continue;
                        }
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
