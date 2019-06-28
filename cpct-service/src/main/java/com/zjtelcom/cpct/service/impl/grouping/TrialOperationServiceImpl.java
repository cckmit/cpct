package com.zjtelcom.cpct.service.impl.grouping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ctg.mq.api.bean.MQSendStatus;
import com.ctzj.smt.bss.sysmgr.model.common.SysmgrResultObject;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.ctzj.smt.bss.sysmgr.privilege.service.dubbo.api.ISystemUserDtoDubboService;
import com.mysql.jdbc.StringUtils;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.MktCamCustMapper;
import com.zjtelcom.cpct.dao.channel.MktCamScriptMapper;
import com.zjtelcom.cpct.dao.filter.CloseRuleMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.filter.MktStrategyCloseRuleRelMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TrialOperationMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamChlConfAttrDO;
import com.zjtelcom.cpct.domain.campaign.MktCamChlConfDO;
import com.zjtelcom.cpct.domain.campaign.MktCamItem;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.grouping.GroupingVO;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.domain.strategy.MktStrategyCloseRuleRelDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConf;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.campaign.MktCamChlResult;
import com.zjtelcom.cpct.dto.channel.LabelDTO;
import com.zjtelcom.cpct.dto.channel.TransDetailDataVO;
import com.zjtelcom.cpct.dto.channel.VerbalVO;
import com.zjtelcom.cpct.dto.filter.CloseRule;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.grouping.*;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRule;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.enums.TrialCreateType;
import com.zjtelcom.cpct.enums.TrialStatus;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.MqService;
import com.zjtelcom.cpct.service.campaign.MktCamChlConfService;
import com.zjtelcom.cpct.service.channel.ProductService;
import com.zjtelcom.cpct.service.grouping.TrialOperationService;
import com.zjtelcom.cpct.service.impl.MqServiceImpl;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfRuleService;
import com.zjtelcom.cpct.util.*;
import com.zjtelcom.cpct_prod.dao.offer.MktResourceProdMapper;
import com.zjtelcom.cpct_prod.dao.offer.OfferProdMapper;
import com.zjtelcom.es.es.entity.*;
import com.zjtelcom.es.es.entity.model.LabelResultES;
import com.zjtelcom.es.es.entity.model.TrialOperationParamES;
import com.zjtelcom.es.es.entity.model.TrialResponseES;
import com.zjtelcom.es.es.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.concurrent.locks.LockSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;
import static com.zjtelcom.cpct.enums.ConfAttrEnum.*;

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
    @Autowired(required = false)
    private RestTemplate restTemplate;
    @Autowired
    private MktStrategyConfRuleRelMapper ruleRelMapper;
    @Autowired
    private RedisUtils redisUtils;
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
    @Autowired
    private FilterRuleMapper filterRuleMapper;
    @Autowired
    private RedisUtils_es redisUtils_es;
    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper;
    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper;
    @Autowired
    private MqService mqService;
    @Autowired
    private MktCamDisplayColumnRelMapper mktCamDisplayColumnRelMapper;

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
    @Autowired
    private MktStrategyCloseRuleRelMapper strategyCloseRuleRelMapper;
    @Autowired
    private CloseRuleMapper closeRuleMapper;



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
        operation.setStatusCd(TrialStatus.UPLOAD_GOING.getValue());
        trialOperationMapper.updateByPrimaryKey(operation);
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
//            response = restTemplate.postForObject("localhost:8090/es/searchCountByLabelList", request, TrialResponseES.class);
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

    @Override
    public Map<String, Object> conditionCheck(Map<String, Object> params) {
        Map<String,Object> result = new HashMap<>();

        List<TarGrpCondition> conditions = new ArrayList<>();
        if (params.get("conditionList")==null){
            result.put("resultCode", CODE_SUCCESS);
            result.put("resultMsg",0);
            return result;
        }
        Long ruleId = MapUtil.getLongNum(params.get("ruleId"));
        String orgCheck = redisUtils.get("ORG_CHECK_"+ruleId.toString())==null ? null :redisUtils.get("ORG_CHECK_"+ruleId.toString()).toString();
        if (orgCheck!=null && orgCheck.equals("false")){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "营销组织树配置正在努力加载请稍后再试");
            return result;
        }
        List<Map<String,Object>> conditionMap = (List<Map<String,Object>>)params.get("conditionList");
        for (Map<String,Object> map : conditionMap){
            TarGrpCondition condition = ChannelUtil.mapToEntity(map,TarGrpCondition.class);
            condition.setOperType(map.get("operType").toString());
            condition.setLeftParam(map.get("leftParam").toString());
            condition.setRightParam(map.get("rightParam").toString());
            conditions.add(condition);
        }
        String strategyArea = MapUtil.getString(params.get("strategyArea"));
        if (conditions.isEmpty() ){
            result.put("resultCode", CODE_SUCCESS);
            result.put("resultMsg",0);
            return result;
        }
        TrialOperationVO request = new TrialOperationVO();
        //生成批次号
        String batchNumSt = DateUtil.date2St4Trial(new Date()) + ChannelUtil.getRandomStr(4);
        request.setBatchNum(Long.valueOf(batchNumSt));
        request.setFieldList(new String[new ArrayList<String>().size()]);
        request.setSample(true);
        if (!strategyArea.equals("")){
            //添加策略适用地市
            Long strategyId = Long.valueOf(new Date().getTime()+ChannelUtil.getRandomStr(2));
            redisUtils.set("STRATEGY_CONF_AREA_"+strategyId,strategyArea);
            request.setStrategyId(strategyId);
        }
        TrialOperationVOES requests = BeanUtil.create(request, new TrialOperationVOES());
        ArrayList<TrialOperationParamES> paramList = new ArrayList<>();
        TrialOperationParamES param = getTrialOperationParamES(request, Long.valueOf(batchNumSt),  Long.valueOf(new Date().getTime()+ChannelUtil.getRandomStr(2)) + 1, true, conditions);
        param.setRuleId(ruleId);
        paramList.add(param);
        requests.setParamList(paramList);
        TrialResponseES response = new TrialResponseES();
        try {
            //todo
            System.out.println(JSON.toJSONString(requests));
            response = esService.searchBatchInfo(requests);
            if (response.getResultCode().equals(CODE_FAIL)){
                result.put("resultCode", CODE_SUCCESS);
                result.put("resultMsg", 0);
                return result;
            }
            //todo 返回信息结果封装
        } catch (Exception e) {
            e.printStackTrace();
            // 抽样试算失败
            result.put("resultCode", CODE_SUCCESS);
            result.put("resultMsg", 0);
            return result;
        }
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", response.getTotal());
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
        List<Map<String,Object>> labelList =displayLabel(campaign);

        redisUtils.set("LABEL_DETAIL_"+batchNumSt,labelList);

        String[] fieldList = getStrings(campaign,strategy);

        TrialOperationVO request = BeanUtil.create(operationVO,new TrialOperationVO());
        //抽样业务校验
        request.setSample(false);
        TrialOperationVOES requests = BeanUtil.create(request,new TrialOperationVOES());

        ArrayList<TrialOperationParamES> paramList = new ArrayList<>();
        List<MktStrategyConfRuleRelDO> ruleRelList = ruleRelMapper.selectByMktStrategyConfId(operationVO.getStrategyId());
        for (MktStrategyConfRuleRelDO ruleRelDO : ruleRelList) {
            TrialOperationParamES param = getTrialOperationParamES(operationVO,Long.valueOf(batchNumSt), ruleRelDO.getMktStrategyConfRuleId(),true,null);
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


    private Map<String,Object> userList2Redis(int avg,int redisI,int fild,String batchNumSt,Long ruleId,List<Map<String,Object>> customerList){
        Map<String,Object> result = new HashMap<>();
        if (customerList.isEmpty()){
            result.put("redisI",redisI);
            result.put("listNum",fild);
            return result;
        }
        List<List<Map<String,Object>>> smallCustomers = ChannelUtil.averageAssign(customerList,avg);
        int listNum = fild;
        //按规则存储客户信息
        int number = listNum;
        for (int j = listNum,i = 0; j < avg + number && i < smallCustomers.size(); j++,i++) {
            redisUtils_es.hset("ISSURE_" + batchNumSt + "_" + ruleId + "_KEY_NUM_" + redisI, j + "", smallCustomers.get(i));
            System.out.println("*************插入得key："+"ISSURE_" + batchNumSt + "_" + ruleId + "_KEY_NUM_" + redisI);
            listNum++;
        }
        result.put("redisI",redisI+1);
        result.put("listNum",listNum);
        return result;
    }


    //下发文件
    private Map<String, Object> importUserList(Map<String, Object> result, TrialOperationVO operation, Long ruleId, String batchNumSt, List<Map<String, Object>> customerList, List<Map<String, Object>> labelList) {
        final TrialOperationVOES request = getTrialOperationVOES(operation, ruleId, batchNumSt, labelList);
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

    private TrialOperationVOES getTrialOperationVOES(TrialOperationVO operation, Long ruleId, String batchNumSt, List<Map<String, Object>> labelList) {
        redisUtils_es.set("LABEL_DETAIL_" + batchNumSt, labelList);
        MktCampaignDO campaignDO = campaignMapper.selectByPrimaryKey(operation.getCampaignId());
        MktStrategyConfDO strategyConfDO = strategyConfMapper.selectByPrimaryKey(operation.getStrategyId());

        final TrialOperationVOES request = BeanUtil.create(operation, new TrialOperationVOES());
        request.setBatchNum(Long.valueOf(batchNumSt));
        request.setCampaignType(campaignDO.getMktCampaignType());
        request.setLanId(campaignDO.getLanId());
        request.setCampaignName(campaignDO.getMktCampaignName());
        request.setCamLevel(campaignDO.getCamLevel());
        request.setStrategyName(strategyConfDO.getMktStrategyConfName());
        // 获取创建人员code
        request.setStaffCode(getCreater(campaignDO.getCreateStaff()) == null ? "null" : getCreater(campaignDO.getCreateStaff()));

        //获取销售品及规则列表
        TrialOperationParamES param = getTrialOperationParamES(operation, Long.valueOf(batchNumSt), ruleId, false, null);
        ArrayList<TrialOperationParamES> paramESList = new ArrayList<>();
        paramESList.add(param);
        request.setParamList(paramESList);
        return request;
    }

    private List<Map<String,Object>> displayLabel(MktCampaignDO campaign) {
        List<Map<String,Object>> labelList = new ArrayList<>();
        List<LabelDTO> labelDTOList = mktCamDisplayColumnRelMapper.selectLabelDisplayListByCamId(campaign.getMktCampaignId());
        if (labelDTOList==null){
            labelDTOList = new ArrayList<>();
        }
        String[] displays = new String[labelDTOList.size()];
        for (int i = 0 ; i< labelDTOList.size();i++){
            displays[i] = labelDTOList.get(i).getLabelCode();
            Map<String,Object> label = new HashMap<>();
            label.put("code",labelDTOList.get(i).getLabelCode());
            label.put("name",labelDTOList.get(i).getInjectionLabelName());
            label.put("labelType",labelDTOList.get(i).getLabelType());
            labelList.add(label);
        }
        return labelList;
    }


    /**
     * 导入试运算清单
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> importUserList(MultipartFile multipartFile, TrialOperationVO operation, Long ruleId) throws IOException {
        Map<String, Object> result = new HashMap<>();
        String batchNumSt = DateUtil.date2St4Trial(new Date()) + ChannelUtil.getRandomStr(4);
        XlsxProcessAbstract xlsxProcess = new XlsxProcessAbstract();
        InputStream inputStream = multipartFile.getInputStream();

        MktCampaignDO campaign = campaignMapper.selectByPrimaryKey(operation.getCampaignId());
        MktStrategyConfDO strategy = strategyMapper.selectByPrimaryKey(operation.getStrategyId());
        MktStrategyConfRuleDO confRule = ruleMapper.selectByPrimaryKey(ruleId);
        if (campaign == null || strategy == null || confRule==null) {
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "未找到有效的活动策略或规则");
            return result;
        }
        TrialOperation op = null;

        try {
            //添加红黑名单列表
            blackList2Redis(campaign);
            List<String> labelNameList = new ArrayList<>();
            List<String> labelEngNameList = new ArrayList<>();
            TransDetailDataVO dataVO;
            List<Map<String, Object>> labelList = new ArrayList<>();
            dataVO = xlsxProcess.processAllSheet(multipartFile);
            String[] nameList = dataVO.getContentList().get(0).split("\\|@\\|");
            String[] codeList = dataVO.getContentList().get(1).split("\\|@\\|");
            if (nameList.length != codeList.length) {
                result.put("resultCode", CODE_FAIL);
                result.put("resultMsg", "标签中文名个数与英文个数不匹配请重新检查文件");
                return result;
            }
            for (int i = 0; i < nameList.length; i++) {
                if (labelNameList.contains(nameList[i].trim())) {
                    result.put("resultCode", CODE_FAIL);
                    result.put("resultMsg", "标签中文名称不能重复:" + "\"" + nameList[i] + "\"");
                    return result;
                }
                if (labelEngNameList.contains(codeList[i].trim())) {
                    result.put("resultCode", CODE_FAIL);
                    result.put("resultMsg", "标签英文名称不能重复:" + "\"" + codeList[i] + "\"");
                    return result;
                }
                Map<String, Object> label = new HashMap<>();
                label.put("code", codeList[i]);
                label.put("name", nameList[i]);
                labelList.add(label);
                labelNameList.add(nameList[i]);
                labelEngNameList.add(codeList[i]);
            }

<<<<<<< HEAD
            List<Map<String,Object>> displayList = displayLabel(campaign);
            List<String> fields = new ArrayList<>();
            for (Map<String,Object> display : displayList){
                String code = display.get("code")==null ? null : display.get("code").toString();
                String name = display.get("name")==null ? null : display.get("name").toString();
                if (code!=null && !labelEngNameList.contains(code) && !labelNameList.contains(name)){
=======

            //查询活动下面所有渠道属性id是21和22的value
            List<String> attrValue = mktCamChlConfAttrMapper.selectAttrLabelValueByCampaignId(campaign.getMktCampaignId());
            List<String> fields = new ArrayList<>();
            for (String attr : attrValue){
                fields.add(attr);
            }
            List<Map<String, Object>> displayList = displayLabel(campaign);
            for (Map<String, Object> display : displayList) {
                String code = display.get("code") == null ? null : display.get("code").toString();
                String name = display.get("name") == null ? null : display.get("name").toString();
                if (code != null && !labelEngNameList.contains(code)) {
>>>>>>> origin/dev_cpct_1212_svn_config
                    Map<String, Object> label = new HashMap<>();
                    label.put("code", code);
                    label.put("name", name);
                    labelList.add(label);
                    fields.add(code);
                    labelEngNameList.add(code);
                    labelNameList.add(name);
                }
            }
            if (!fields.isEmpty()){
                redisUtils_es.set("DISPLAY_LABEL_"+campaign.getMktCampaignId(),fields);
            }
            List<Long> attrList = mktCamChlConfAttrMapper.selectByCampaignId(campaign.getMktCampaignId());
            if (attrList.contains(ISEE_CUSTOMER.getArrId()) || attrList.contains(ISEE_LABEL_CUSTOMER.getArrId()) ){
                Map<String,Object> label = new HashMap<>();
                label.put("code","SALE_EMP_NBR");
                label.put("name","接单人号码");
                label.put("labelType","1200");
                labelList.add(label);
            }
            if (attrList.contains(ISEE_AREA.getArrId()) || attrList.contains(ISEE_LABEL_AREA.getArrId()) ){
                Map<String,Object> label = new HashMap<>();
                label.put("code","AREA");
                label.put("name","派单区域");
                label.put("labelType","1200");
                labelList.add(label);
            }
            redisUtils.set("LABEL_DETAIL_"+batchNumSt,labelList);

            if (labelList.size() > 87) {
                result.put("resultCode", CODE_FAIL);
                result.put("resultMsg", "扩展字段不能超过87个");
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
            op = trialOp;
            int size = dataVO.contentList.size() - 3;
            new Thread() {
                public void run() {
                    List<Map<String, Object>> customerList = new ArrayList<>();
                    int k = 4000 / labelList.size();
                    int y = 80000 / labelList.size();
                    //num 分割后有多少个小list
                    int num = (size / k) + 1;
                    //多少个key
                    int totalKey = (size / y) + 1;
                    //多少个list存一个key
                    int avg = (num / totalKey) + 1;

                    int redisI = 0;
                    int redisListNum = 0;

                    for (int j = 3; j < dataVO.contentList.size(); j++) {
                        List<String> data = Arrays.asList(dataVO.contentList.get(j).split("\\|@\\|"));
                        Map<String, Object> customers = new HashMap<>();
                        boolean check = true;
                        for (int x = 0; x < codeList.length; x++) {
                            if (codeList[x] == null) {
                                break;
                            }
                            String value = "";
                            if (x >= data.size()) {
                                value = "null";
                            } else {
                                value = data.get(x);
                            }
                            if (value.contains("\r")){
                                value = value.replace("\r","");
                            }
                            if (value.contains("\n")){
                                value = value.replace("\n","");
                            }
                            if (codeList[x].equals("CCUST_NAME") && (value.contains("null") || value.equals(""))) {
                                check = false;
                                break;
                            }
                            if (codeList[x].equals("CCUST_ID") && (value.contains("null") || value.equals(""))) {
                                check = false;
                                break;
                            }
                            if (codeList[x].equals("ASSET_INTEG_ID") && (value.contains("null") || value.equals(""))) {
                                check = false;
                                break;
                            }
                            if (codeList[x].equals("ASSET_NUMBER") && (value.contains("null") || value.equals(""))) {
                                check = false;
                                break;
                            }
                            if (codeList[x].equals("LATN_ID") && (value.contains("null") || value.equals(""))) {
                                check = false;
                                break;
                            }
                            customers.put(codeList[x], value);
                        }
                        if (customers.isEmpty() || !check){
                           continue;
                        }
                        customerList.add(customers);
                        if (customerList.size() >= avg * k || j == dataVO.contentList.size() - 1) {
                            Map<String, Object> resultMap = userList2Redis(avg, redisI, redisListNum, batchNumSt, ruleId, customerList);
                            redisI = (int) resultMap.get("redisI");
                            redisListNum = (int) resultMap.get("listNum");
                            customerList = new ArrayList<>();
                        }
                    }
                    redisUtils_es.set("IMPORT_USER_LIST_" + batchNumSt, redisI);
                    redisUtils_es.set("IMPORT_USER_LIST_AVG" + batchNumSt, avg);
                    try {
                        inputStream.close();
                        importUserList(result, operation, ruleId, batchNumSt, customerList, labelList);
                    } catch (Exception e) {
                        trialOp.setStatusCd(TrialStatus.IMPORT_FAIL.getValue());
                        trialOperationMapper.updateByPrimaryKey(trialOp);
                        e.printStackTrace();
                        logger.error("导入失败");
                    }
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
            if (op!=null){
                op.setStatusCd(TrialStatus.IMPORT_FAIL.getValue());
                trialOperationMapper.updateByPrimaryKey(op);
            }
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "导入失败");
            return result;
        }
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg", "导入成功,请稍后查看结果");
        return result;
    }

//    /**
//     * 导入试运算清单
//     */
//    @Transactional(readOnly = false)
//    @Override
//    public Map<String, Object> importUserList(MultipartFile multipartFile, TrialOperationVO operation, Long ruleId) throws IOException {
//        Map<String, Object> result = new HashMap<>();
//        String batchNumSt = DateUtil.date2St4Trial(new Date()) + ChannelUtil.getRandomStr(4);
//        XlsxProcessAbstract xlsxProcess = new XlsxProcessAbstract();
//        InputStream inputStream = multipartFile.getInputStream();
//
//        MktCampaignDO campaign = campaignMapper.selectByPrimaryKey(operation.getCampaignId());
//        MktStrategyConfDO strategy = strategyMapper.selectByPrimaryKey(operation.getStrategyId());
//        MktStrategyConfRuleDO confRule = ruleMapper.selectByPrimaryKey(ruleId);
//        if (campaign == null || strategy == null || confRule == null) {
//            result.put("resultCode", CODE_FAIL);
//            result.put("resultMsg", "未找到有效的活动策略或规则");
//            return result;
//        }
//        TrialOperation op = null;
//        try {
//            //添加红黑名单列表
//            blackList2Redis(campaign);
//            List<String> labelNameList = new ArrayList<>();
//            List<String> labelEngNameList = new ArrayList<>();
//            TransDetailDataVO dataVO;
//            List<Map<String, Object>> labelList = new ArrayList<>();
//            dataVO = xlsxProcess.processAllSheet(multipartFile);
//            String[] nameList = dataVO.getContentList().get(0).split("\\|@\\|");
//            String[] codeList = dataVO.getContentList().get(1).split("\\|@\\|");
//            if (nameList.length != codeList.length) {
//                result.put("resultCode", CODE_FAIL);
//                result.put("resultMsg", "标签中文名个数与英文个数不匹配请重新检查文件");
//                return result;
//            }
//            for (int i = 0; i < nameList.length; i++) {
//                if (labelNameList.contains(nameList[i])) {
//                    result.put("resultCode", CODE_FAIL);
//                    result.put("resultMsg", "标签中文名称不能重复:" + "\"" + nameList[i] + "\"");
//                    return result;
//                }
//                if (labelEngNameList.contains(codeList[i])) {
//                    result.put("resultCode", CODE_FAIL);
//                    result.put("resultMsg", "标签英文名称不能重复:" + "\"" + codeList[i] + "\"");
//                    return result;
//                }
//                Map<String, Object> label = new HashMap<>();
//                label.put("code", codeList[i]);
//                label.put("name", nameList[i]);
//                labelList.add(label);
//                labelNameList.add(nameList[i]);
//                labelEngNameList.add(codeList[i]);
//            }
//
//            List<Map<String, Object>> displayList = displayLabel(campaign);
//            List<String> fields = new ArrayList<>();
//            for (Map<String, Object> display : displayList) {
//                String code = display.get("code") == null ? null : display.get("code").toString();
//                String name = display.get("name") == null ? null : display.get("name").toString();
//                if (code != null && !labelEngNameList.contains(code)) {
//                    Map<String, Object> label = new HashMap<>();
//                    label.put("code", code);
//                    label.put("name", name);
//                    labelList.add(label);
//                    fields.add(code);
//                    labelEngNameList.add(code);
//                }
//            }
//            if (!fields.isEmpty()) {
//                redisUtils_es.set("DISPLAY_LABEL_" + campaign.getMktCampaignId(), fields);
//            }
//
//            if (labelList.size() > 87) {
//                result.put("resultCode", CODE_FAIL);
//                result.put("resultMsg", "扩展字段不能超过87个");
//                return result;
//            }
//            List<MktStrategyCloseRuleRelDO> closeRuleRelDOS = strategyCloseRuleRelMapper.selectRuleByStrategyId(campaign.getMktCampaignId());
//            //todo 关单规则配置信息
//            if (closeRuleRelDOS!=null && !closeRuleRelDOS.isEmpty()){
//                List<Map<String,Object>> closeRule = new ArrayList<>();
//                for (MktStrategyCloseRuleRelDO ruleRelDO : closeRuleRelDOS){
//                    CloseRule closeR = closeRuleMapper.selectByPrimaryKey(ruleRelDO.getRuleId());
//                    if (closeR!=null){
//                        Map<String,Object> ruleMap = new HashMap<>();
//                        ruleMap.put("closeName",closeR.getCloseName());
//                        ruleMap.put("closeCode",closeR.getCloseCode());
//                        ruleMap.put("closeNbr",closeR.getExpression());
//                        closeRule.add(ruleMap);
//                    }
//                }
//                redisUtils_es.set("CLOSE_RULE_"+campaign.getMktCampaignId(),closeRule);
//            }
//            TrialOperation trialOp = BeanUtil.create(operation, new TrialOperation());
//            trialOp.setCampaignName(campaign.getMktCampaignName());
//            //当清单导入时 strategyId name 存储规则信息
//            trialOp.setStrategyId(confRule.getMktStrategyConfRuleId());
//            trialOp.setStrategyName(confRule.getMktStrategyConfRuleName());
//            trialOp.setBatchNum(Long.valueOf(batchNumSt));
//            trialOp.setStatusCd(TrialStatus.IMPORT_GOING.getValue());
//            trialOp.setStatusDate(new Date());
//            trialOp.setCreateStaff(TrialCreateType.IMPORT_USER_LIST.getValue());
//            trialOperationMapper.insert(trialOp);
//            op = trialOp;
//            int size = dataVO.contentList.size() - 3;
//            new Thread() {
//                public void run() {
//                    List<FilterRule> productFilter = new ArrayList<>();
//                    final TrialOperationVOES request = getTrialOperationVOES(operation, ruleId, batchNumSt, labelList);
//                    List<Map<String, Object>> customerList = new ArrayList<>();
//                    //红黑名单过滤
//                    List<String> typeList = new ArrayList<>();
//                    typeList.add("3000");
//                    List<FilterRule> filterRuleList = filterRuleMapper.selectFilterRuleListByStrategyId(campaign.getMktCampaignId(), typeList);
//                    if (filterRuleList != null && !filterRuleList.isEmpty()) {
//                        productFilter = filterRuleList;
//                    }
//                    for (int j = 3; j < dataVO.contentList.size(); j++) {
//                        List<String> data = Arrays.asList(dataVO.contentList.get(j).split("\\|@\\|"));
//                        Map<String, Object> customers = new HashMap<>();
//                        for (int x = 0; x < codeList.length; x++) {
//                            if (codeList[x] == null) {
//                                break;
//                            }
//                            String value = "";
//                            if (x >= data.size()) {
//                                value = "null";
//                            } else {
//                                value = data.get(x);
//                            }
//                            if (value.contains("\r") || value.equals("\n")) {
//                                // 过滤换行符
//                                value = value.replace("\r", "").replace("\n", "");
//                            }
//                            if (codeList[x].equals("CCUST_NAME") && (value.contains("null") || value.equals(""))) {
//                                break;
//                            }
//                            if (codeList[x].equals("CCUST_ID") && (value.contains("null") || value.equals(""))) {
//                                break;
//                            }
//                            if (codeList[x].equals("ASSET_INTEG_ID") && (value.contains("null") || value.equals(""))) {
//                                break;
//                            }
//                            if (codeList[x].equals("ASSET_NUMBER") && (value.contains("null") || value.equals(""))) {
//                                break;
//                            }
//                            if (codeList[x].equals("LATN_ID") && (value.contains("null") || value.equals(""))) {
//                                break;
//                            }
//                            customers.put(codeList[x], value);
//                        }
//                        if (customers.isEmpty()) {
//                            continue;
//                        }
//                        customerList.add(customers);
//                        if (customerList.size() >= 1000 || j == dataVO.contentList.size() - 1) {
//                            // 向MQ中扔入request和customersList
//                            HashMap msgBody = new HashMap();
//                            msgBody.put("request", request);
//                            msgBody.put("customerList", customerList);
//                            msgBody.put("productFilterList", productFilter);
//                            try {
//                                // 判断是否发送成功
//                                if (!mqService.msg2Producer(msgBody, batchNumSt, null).equals(MQSendStatus.SEND_OK)) {
//                                    // 发送失败自动重发2次，如果还是失败，记录
//                                    logger.error("CTGMQ消息生产失败,batchNumSt:" + batchNumSt, msgBody);
//                                }
//                                msgBody = null;
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            //把customerList拆分成多个小list存入redis
//                            //Map<String, Object> resultMap = userList2Redis(avg, redisI, redisListNum, batchNumSt, ruleId, customerList);
//                            //redisI = (int) resultMap.get("redisI");
//                            //redisListNum = (int) resultMap.get("listNum");
//                            //customerList = new ArrayList<>();
//                            customerList.clear();
//                        }
//                    }
//
//                }
//            }.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (op != null) {
//                op.setStatusCd(TrialStatus.IMPORT_FAIL.getValue());
//                trialOperationMapper.updateByPrimaryKey(op);
//            }
//            result.put("resultCode", CODE_FAIL);
//            result.put("resultMsg", "导入失败");
//            return result;
//        }
//        result.put("resultCode", CommonConstant.CODE_SUCCESS);
//        result.put("resultMsg", "导入成功,请稍后查看结果");
//        return result;
//    }

    private void blackList2Redis(MktCampaignDO campaign) {
        List<String> typeList = new ArrayList<>();
        typeList.add("1000");
        List<FilterRule> filterRuleList = filterRuleMapper.selectFilterRuleListByStrategyId(campaign.getMktCampaignId(),typeList);
        if (filterRuleList!=null && !filterRuleList.isEmpty()){
            List<String> userList = new ArrayList<>();
            for (FilterRule filterRule : filterRuleList){
                String[] users = filterRule.getUserList().split(",");
                userList.addAll(Arrays.asList(users));
            }
            int num = userList.size()/100 + 1;
            List<List<String>> list = ChannelUtil.averageAssign(userList,num);
            for (int i = 0; i < num; i++) {
                redisUtils.hset("AREA_RULE_BLACK_LIST_"+campaign.getMktCampaignId(),i+"",list.get(i));
            }
        }
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
            String orgCheck = redisUtils.get("ORG_CHECK_"+rule.getMktStrategyConfRuleId().toString())==null ? null :redisUtils.get("ORG_CHECK_"+rule.getMktStrategyConfRuleId().toString()).toString();
            if (orgCheck!=null && orgCheck.equals("false")){
                result.put("resultCode", CODE_FAIL);
                result.put("resultMsg", "规则："+rule.getMktStrategyConfRuleName()+"营销组织树配置正在努力加载请稍后再试");
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
        //添加红黑名单列表
        blackList2Redis(campaign);


        // 通过活动id获取关联的标签字段数组

        List<Map<String,Object>> labelList = displayLabel(campaign);

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
            TrialOperationParamES param = getTrialOperationParamES(operationVO, trialOperation.getBatchNum(), ruleRelDO.getMktStrategyConfRuleId(),true,null);
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
        List<LabelDTO> labelDTOList = mktCamDisplayColumnRelMapper.selectLabelDisplayListByCamId(campaign.getMktCampaignId());
        if (labelDTOList==null){
            labelDTOList = new ArrayList<>();
        }
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
                // 数据脱敏(客户证件号)，查询全局开关（0：关；1：开）
                String dataDesFilter = (String) redisUtils.get("DATA_DESENSITIZATION_FILTER");
                if (dataDesFilter == null) {
                    List<SysParams> sysParamsList = sysParamsMapper.listParamsByKeyForCampaign("DATA_DESENSITIZATION_FILTER");
                    if (sysParamsList != null && sysParamsList.size() > 0) {
                        dataDesFilter = sysParamsList.get(0).getParamValue();
                        redisUtils.set("DATA_DESENSITIZATION_FILTER", dataDesFilter);
                    }
                }
                // 数据脱敏(客户证件号)，脱敏操作
                if (set.equals("ID_NBR") && ( null == dataDesFilter || dataDesFilter.equals("1"))) {
                    String value = dataDesensitization((String) searchMap.get(set));
                    map.put(set, value);
                } else {
                    map.put(set, searchMap.get(set));
                }
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

    private String dataDesensitization(String sfz) {
        String sfzyc = "";
        if (!StringUtils.isNullOrEmpty(sfz)) {
            if (sfz.length() == 18 || sfz.length() == 15) {
                sfzyc = (sfz.substring(0, 6) + "********" + sfz.substring(sfz.length() - 4, sfz.length()));
            } else if (sfz.length() == 10) {
                sfzyc = (sfz.substring(0, 7) + "***");
            } else if (sfz.length() > 18) {
                sfzyc = (sfz.substring(0, 6) + "********" + sfz.substring(sfz.length() - 4, sfz.length()));
            } else if (sfz.length() - 2 >= 4) {
                sfzyc = (sfz.substring(0, 2) + "******" + sfz.substring(sfz.length() - 2, sfz.length()));
            } else if (sfz.length() - 2 >= 1) {
                sfzyc = (sfz.substring(0, 1) + "*****" + sfz.substring(sfz.length() - 1, sfz.length()));
            }
        }
        return sfzyc;
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
        List<MktStrategyCloseRuleRelDO> closeRuleRelDOS = strategyCloseRuleRelMapper.selectRuleByStrategyId(campaignDO.getMktCampaignId());
        //todo 关单规则配置信息
        if (closeRuleRelDOS!=null && !closeRuleRelDOS.isEmpty()){
            List<Map<String,Object>> closeRule = new ArrayList<>();
            for (MktStrategyCloseRuleRelDO ruleRelDO : closeRuleRelDOS){
                CloseRule closeR = closeRuleMapper.selectByPrimaryKey(ruleRelDO.getRuleId());
                if (closeR!=null){
                    Map<String,Object> ruleMap = new HashMap<>();
                    ruleMap.put("closeName",closeR.getCloseName());
                    ruleMap.put("closeCode",closeR.getCloseCode());
                    ruleMap.put("closeNbr",closeR.getExpression());
                    closeRule.add(ruleMap);
                }
            }
            redisUtils_es.set("CLOSE_RULE_"+campaignDO.getMktCampaignId(),closeRule);
        }
        //查询活动下面所有渠道属性id是21和22的value
        List<String> attrValue = mktCamChlConfAttrMapper.selectAttrLabelValueByCampaignId(trialOperation.getCampaignId());
        //添加策略适用地市
        redisUtils.set("STRATEGY_CONF_AREA_"+operation.getStrategyId(),strategyConfDO.getAreaId());
        // 通过活动id获取关联的标签字段数组
        List<LabelDTO> labelDTOList = mktCamDisplayColumnRelMapper.selectLabelDisplayListByCamId(campaignDO.getMktCampaignId());
        if (labelDTOList==null){
            labelDTOList = new ArrayList<>();
        }
        String[] fieldList = new String[labelDTOList.size()+attrValue.size()];
        List<Map<String,Object>> labelList = new ArrayList<>();
        for (int i = 0 ; i< labelDTOList.size();i++){
            fieldList[i] = labelDTOList.get(i).getLabelCode();
            Map<String,Object> label = new HashMap<>();
            label.put("code",labelDTOList.get(i).getLabelCode());
            label.put("name",labelDTOList.get(i).getInjectionLabelName());
            label.put("labelType",labelDTOList.get(i).getLabelType());
            labelList.add(label);
        }


        for (int i = labelDTOList.size(); i< labelDTOList.size()+attrValue.size();i++){
            fieldList[i] = attrValue.get(i-labelDTOList.size());
        }
        List<Long> attrList = mktCamChlConfAttrMapper.selectByCampaignId(trialOperation.getCampaignId());
        if (attrList.contains(ISEE_CUSTOMER.getArrId()) || attrList.contains(ISEE_LABEL_CUSTOMER.getArrId()) ){
            Map<String,Object> label = new HashMap<>();
            label.put("code","SALE_EMP_NBR");
            label.put("name","接单人号码");
            label.put("labelType","1200");
            labelList.add(label);
        }
        if (attrList.contains(ISEE_AREA.getArrId()) || attrList.contains(ISEE_LABEL_AREA.getArrId()) ){
            Map<String,Object> label = new HashMap<>();
            label.put("code","AREA");
            label.put("name","派单区域");
            label.put("labelType","1200");
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
            TrialOperationParamES param = getTrialOperationParamES(request, trialOperation.getBatchNum(), ruleRelDO.getMktStrategyConfRuleId(),false,null);
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
    private TrialOperationParamES getTrialOperationParamES(TrialOperationVO operationVO, Long batchNum, Long ruleId, boolean isSample,List<TarGrpCondition> conditions) {
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
                    if (mktCamChlConfDetail.getCamScript()!=null){
                        CamScriptES camScriptES = BeanUtil.create(mktCamChlConfDetail.getCamScript(),new CamScriptES());
                        if (camScriptES.getScriptDesc()!=null && !camScriptES.getScriptDesc().equals("")){
                            if (camScriptES.getScriptDesc().contains("\n")){
                                camScriptES.setScriptDesc(camScriptES.getScriptDesc().replace("\n",""));
                            }
                            if (camScriptES.getScriptDesc().contains("\r")){
                                camScriptES.setScriptDesc(camScriptES.getScriptDesc().replace("\r",""));
                            }
                        }
                        es.setCamScript(camScriptES);
                    }
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
            if (ruleDO!=null || isSample){
                Future<Map<String, Object>> future = executorService.submit(new TarGrpRuleTask(operationVO.getCampaignId(),operationVO.getStrategyId(), ruleDO, redisUtils,isSample,conditions));
                rule = future.get().get("express").toString();
                labelResultList = ( List<LabelResult>)future.get().get("labelResultList");
            }
            // 关闭线程池
            if (!executorService.isShutdown()) {
                executorService.shutdown();
            }
        }catch (Exception e){
            e.printStackTrace();
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
        private boolean isSample;
        private List<TarGrpCondition> conditions;



        public TarGrpRuleTask(Long mktCampaignId, Long mktStrategyConfId, MktStrategyConfRuleDO mktStrategyConfRuleDO, RedisUtils redisUtils
                ,boolean isSample,List<TarGrpCondition> conditions) {
            this.mktCampaignId = mktCampaignId;
            this.mktStrategyConfId = mktStrategyConfId;
            this.mktStrategyConfRuleDO = mktStrategyConfRuleDO;
            this.redisUtils = redisUtils;
            this.isSample = isSample;
            this.conditions = conditions;
        }

        @Override
        public Map<String, Object> call() {
            Map<String, Object> result = new HashMap<>();
            List<TarGrpCondition> tarGrpConditionDOs =  new ArrayList<>();
            //查询分群规则list
            Long tarGrpId = 0L;
            if (conditions==null){
                tarGrpId = mktStrategyConfRuleDO.getTarGrpId();
                tarGrpConditionDOs = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
            }else{
                tarGrpConditionDOs = conditions;
                tarGrpId = -1L;
            }
            System.out.println(JSON.toJSONString(tarGrpConditionDOs));
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
                System.out.println( ">>>>>>>>express->>>>:" + JSON.toJSONString(express));

                //标签条件编码集合 试算展示用
                redisUtils.hset("LABEL_CODE_"+mktStrategyConfId,tarGrpId+"",codeList);
                System.out.println("TAR_GRP_ID>>>>>>>>>>" + tarGrpId + ">>>>>>>>codeList->>>>:" + JSON.toJSONString(codeList));
            }
            result.put("express",express.toString());
            result.put("labelResultList",labelResultList);
            return result;
        }

    }
}
