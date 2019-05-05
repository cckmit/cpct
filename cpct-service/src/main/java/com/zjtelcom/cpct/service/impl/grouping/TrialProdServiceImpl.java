package com.zjtelcom.cpct.service.impl.grouping;

import com.alibaba.fastjson.JSON;
import com.ctzj.smt.bss.sysmgr.model.common.SysmgrResultObject;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.ctzj.smt.bss.sysmgr.privilege.service.dubbo.api.ISystemUserDtoDubboService;
import com.zjtelcom.cpct.dao.campaign.MktCamChlConfMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.MktCamCustMapper;
import com.zjtelcom.cpct.dao.channel.MktCamScriptMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TrialOperationMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.DisplayColumn;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.LabelResult;
import com.zjtelcom.cpct.domain.channel.MktProductRule;
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
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.dto.grouping.TrialOperationVO;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRule;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.enums.TrialStatus;
import com.zjtelcom.cpct.service.campaign.MktCamChlConfService;
import com.zjtelcom.cpct.service.channel.MessageLabelService;
import com.zjtelcom.cpct.service.channel.ProductService;
import com.zjtelcom.cpct.service.grouping.TrialProdService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfRuleService;
import com.zjtelcom.cpct.util.*;
import com.zjtelcom.cpct_prd.dao.campaign.MktCamChlConfPrdMapper;
import com.zjtelcom.cpct_prd.dao.campaign.MktCampaignPrdMapper;
import com.zjtelcom.cpct_prd.dao.channel.MktCamScriptPrdMapper;
import com.zjtelcom.cpct_prd.dao.filter.FilterRulePrdMapper;
import com.zjtelcom.cpct_prd.dao.grouping.TarGrpConditionPrdMapper;
import com.zjtelcom.cpct_prd.dao.grouping.TrialOperationPrdMapper;
import com.zjtelcom.cpct_prd.dao.label.InjectionLabelPrdMapper;
import com.zjtelcom.cpct_prd.dao.strategy.MktStrategyConfPrdMapper;
import com.zjtelcom.cpct_prd.dao.strategy.MktStrategyConfRulePrdMapper;
import com.zjtelcom.cpct_prd.dao.strategy.MktStrategyConfRuleRelPrdMapper;
import com.zjtelcom.cpct_prd.dao.sys.SysParamsPrdMapper;
import com.zjtelcom.cpct_prod.dao.offer.MktResourceProdMapper;
import com.zjtelcom.cpct_prod.dao.offer.OfferProdMapper;
import com.zjtelcom.es.es.entity.*;
import com.zjtelcom.es.es.entity.model.LabelResultES;
import com.zjtelcom.es.es.entity.model.TrialOperationParamES;
import com.zjtelcom.es.es.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class TrialProdServiceImpl implements TrialProdService {

    @Autowired
    private TarGrpConditionPrdMapper tarGrpConditionMapper;

    @Autowired
    private InjectionLabelPrdMapper injectionLabelMapper;

    @Autowired
    private TrialOperationPrdMapper trialOperationMapper;
    @Autowired
    private MktCampaignPrdMapper campaignMapper;
    @Autowired
    private MktStrategyConfPrdMapper strategyMapper;
    @Autowired
    private MktStrategyConfRuleRelPrdMapper strategyConfRuleRelMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MktStrategyConfRuleRelPrdMapper ruleRelMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private MessageLabelService messageLabelService;
    @Autowired
    private MktStrategyConfRulePrdMapper ruleMapper;
    @Autowired
    private InjectionLabelPrdMapper labelMapper;
    @Autowired
    private OfferProdMapper offerMapper;
    @Autowired
    private MktCamChlConfPrdMapper chlConfMapper;
    @Autowired
    private MktCamScriptPrdMapper scriptMapper;
    @Autowired
    private SysParamsPrdMapper sysParamsMapper;
    @Autowired(required = false)
    private EsService esService;
    @Autowired
    private MktResourceProdMapper resourceMapper;
    @Autowired
    private MktStrategyConfPrdMapper strategyConfMapper;
    @Autowired
    private FilterRulePrdMapper filterRuleMapper;
    @Autowired(required = false)
    private ISystemUserDtoDubboService iSystemUserDtoDubboService;
    @Autowired
    private MktStrategyConfPrdMapper strategyConfPrdMapper;
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
     * 提供dtts定时任务清单存入es
     */
    @Override
    public void campaignIndexTask(Map<String,Object> param) {
        List<MktCampaignDO> campaignList = new ArrayList<>();
        Long campaignId = MapUtil.getLongNum(param.get("id"));
        MktCampaignDO cam = campaignMapper.selectByPrimaryKey(campaignId);
        campaignList.add(cam);
        for (MktCampaignDO campaignDO : campaignList){
            if(!StatusCode.STATUS_CODE_PUBLISHED.getStatusCode().equals(campaignDO.getStatusCd())){
               continue;
            }
//            if (redisUtils.get("CAMPAIGN_ES_STOP")==null || "0".equals(redisUtils.get("CAMPAIGN_ES_STOP"))){
//                break;
//            }
            List<MktStrategyConfDO> strategyConfDOList = strategyConfPrdMapper.selectByCampaignId(campaignDO.getMktCampaignId());
            for (MktStrategyConfDO strategy : strategyConfDOList){
                //生成批次号
                String batchNumSt = DateUtil.date2St4Trial(new Date()) + ChannelUtil.getRandomStr(4);
                TrialOperation operation = new TrialOperation();
                operation.setCampaignId(campaignDO.getMktCampaignId());
                operation.setCampaignName(campaignDO.getMktCampaignName());
                operation.setStrategyId(strategy.getMktStrategyConfId());
                operation.setStrategyName(strategy.getMktStrategyConfName());
                operation.setBatchNum(Long.valueOf(batchNumSt));
                Map<String,Object> res = issue(operation,campaignDO,strategy);
                System.out.println(JSON.toJSONString(res));
            }
        }
    }

    private Map<String, Object> issue(TrialOperation trialOperation, MktCampaignDO campaignDO,MktStrategyConfDO strategyConfDO){
            Map<String, Object> result = new HashMap<>();
            //添加策略适用地市
            redisUtils.set("STRATEGY_CONF_AREA_"+trialOperation.getStrategyId(),strategyConfDO.getAreaId());
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
            final TrialOperationVOES issureRequest = requests;
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
                Future<Map<String, Object>> future = executorService.submit(new TarGrpRuleTask(operationVO.getCampaignId(),operationVO.getStrategyId(), ruleDO));
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

    class TarGrpRuleTask implements Callable<Map<String,Object>> {
        private Long mktCampaignId;

        private Long mktStrategyConfId;

        private MktStrategyConfRuleDO mktStrategyConfRuleDO;


        public TarGrpRuleTask(Long mktCampaignId, Long mktStrategyConfId, MktStrategyConfRuleDO mktStrategyConfRuleDO) {
            this.mktCampaignId = mktCampaignId;
            this.mktStrategyConfId = mktStrategyConfId;
            this.mktStrategyConfRuleDO = mktStrategyConfRuleDO;
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