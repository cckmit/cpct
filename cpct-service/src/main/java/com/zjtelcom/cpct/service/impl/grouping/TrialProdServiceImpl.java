package com.zjtelcom.cpct.service.impl.grouping;

import com.alibaba.fastjson.JSON;
import com.ctzj.smt.bss.sysmgr.model.common.SysmgrResultObject;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.ctzj.smt.bss.sysmgr.privilege.service.dubbo.api.ISystemUserDtoDubboService;
import com.zjtelcom.cpct.dao.campaign.MktCamChlConfAttrMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamChlConfMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamDisplayColumnRelMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.filter.CloseRuleMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.filter.MktStrategyCloseRuleRelMapper;
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
import com.zjtelcom.cpct.domain.strategy.MktStrategyCloseRuleRelDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.campaign.MktCamChlResult;
import com.zjtelcom.cpct.dto.channel.LabelDTO;
import com.zjtelcom.cpct.dto.channel.VerbalVO;
import com.zjtelcom.cpct.dto.filter.CloseRule;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.dto.grouping.TrialOperationVO;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRule;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.enums.TrialCreateType;
import com.zjtelcom.cpct.enums.TrialStatus;
import com.zjtelcom.cpct.service.MqService;
import com.zjtelcom.cpct.service.campaign.MktCamChlConfService;
import com.zjtelcom.cpct.service.campaign.MktDttsLogService;
import com.zjtelcom.cpct.service.channel.MessageLabelService;
import com.zjtelcom.cpct.service.channel.ProductService;
import com.zjtelcom.cpct.service.grouping.TrialOperationService;
import com.zjtelcom.cpct.service.grouping.TrialProdService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfRuleService;
import com.zjtelcom.cpct.util.*;
import com.zjtelcom.cpct_prd.dao.campaign.MktCamChlConfAttrPrdMapper;
import com.zjtelcom.cpct_prd.dao.campaign.MktCamChlConfPrdMapper;
import com.zjtelcom.cpct_prd.dao.campaign.MktCamDisplayColumnRelPrdMapper;
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
import com.zjtelcom.es.es.entity.model.TrialResponseES;
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
import static com.zjtelcom.cpct.enums.ConfAttrEnum.*;

@Service
public class TrialProdServiceImpl implements TrialProdService {

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
    private MktResourceMapper resourceMapper;
    @Autowired
    private MktStrategyConfMapper strategyConfMapper;
    @Autowired
    private FilterRuleMapper filterRuleMapper;
    @Autowired(required = false)
    private ISystemUserDtoDubboService iSystemUserDtoDubboService;
    @Autowired
    private MktStrategyConfMapper strategyConfPrdMapper;
    @Autowired
    private RedisUtils_es redisUtils_es;
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
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper;
    @Autowired
    private MktCamDisplayColumnRelMapper mktCamDisplayColumnRelMapper;
    @Autowired
    private MktStrategyCloseRuleRelMapper strategyCloseRuleRelMapper;
    @Autowired
    private CloseRuleMapper closeRuleMapper;
    @Autowired
    private MktDttsLogService mktDttsLogService;
    @Autowired
    private TrialOperationService trialOperationService;

    /**
     * 提供dtts定时任务清单存入es
     */
    @Override
    public Map<String,Object> campaignIndexTask(Map<String,Object> param) {
        Map<String, Object> result = new HashMap<>();
        List<MktCampaignDO> campaignList = new ArrayList<>();
        Long campaignId = MapUtil.getLongNum(param.get("id"));
        //周期性活动标记
        String perCampaign = MapUtil.getString(param.get("perCampaign"));
        //清单方案活动标记
        String userListCam =  MapUtil.getString(param.get("userListCam"));
        List<Integer> idList = ( List<Integer>)param.get("idList");

        List<String> mktCamCodeList = (List<String>) redisUtils.get("MKT_CAM_API_CODE_KEY");
        if (mktCamCodeList == null) {
            List<SysParams> sysParamsList = sysParamsMapper.listParamsByKeyForCampaign("MKT_CAM_API_CODE");
            mktCamCodeList = new ArrayList<String>();
            for (SysParams sysParams : sysParamsList) {
                mktCamCodeList.add(sysParams.getParamValue());
            }
            redisUtils.set("MKT_CAM_API_CODE_KEY", mktCamCodeList);
        }

        List<Map<String,Object>> resList = new ArrayList<>();
        mktDttsLogService.saveMktDttsLog("1000","活动",new Date(),new Date(),"成功",null);

        for (Integer id : idList){
            MktCampaignDO cam = campaignMapper.selectByPrimaryKey(Long.valueOf(id.toString()));
            if (cam!=null ){
                if (userListCam.equals("USER_LIST_CAM")&& mktCamCodeList.contains(cam.getInitId().toString())){
                    MktCampaignDO campaign = campaignMapper.selectByInitId(cam.getInitId());
                    if (campaign!=null){
                        System.out.println("清单方案活动开始："+cam.getMktCampaignName()+cam.getMktCampaignId());
                        campaignList.add(campaign);
                    }
                }else if (perCampaign.equals("PER_CAMPAIGN") || userListCam.equals("BIG_DATA_TEMP")){
                    MktCampaignDO campaign = campaignMapper.selectByInitId(cam.getInitId());
                    if (campaign!=null){
                        System.out.println("周期性活动-大数据开始："+userListCam+"  "+cam.getMktCampaignName()+cam.getMktCampaignId());
                        campaignList.add(campaign);
                    }
                }
            }
        }
        for (MktCampaignDO campaignDO : campaignList){
            if(!StatusCode.STATUS_CODE_PUBLISHED.getStatusCode().equals(campaignDO.getStatusCd()) && !StatusCode.STATUS_CODE_ADJUST.getStatusCode().equals(campaignDO.getStatusCd()) ){
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
                operation.setCreateStaff(TrialCreateType.TRIAL_OPERATION.getValue());
                trialOperationMapper.insert(operation);
                //周期性活动标记
                if (perCampaign.equals("PER_CAMPAIGN")){
                    redisUtils_es.set("PER_CAMPAIGN_"+batchNumSt,"true");
                }
                //清单方案
                if (userListCam.equals("USER_LIST_CAM")){
                    redisUtils_es.set("USER_LIST_CAM_"+batchNumSt,"USER_LIST_TEMP");
                }
                //大数据方案
                if (userListCam.equals("BIG_DATA_TEMP")){
                    redisUtils_es.set("USER_LIST_CAM_"+batchNumSt,"BIG_DATA_TEMP");
                }
                Map<String,Object> res = issue(operation,campaignDO,strategy,perCampaign);
                resList.add(res);
                System.out.println(JSON.toJSONString(res));
            }
        }
        result.put("resultCode", CODE_FAIL);
        result.put("resultMsg", "全量试算中");
        result.put("res",resList);
        return result;
    }

    private Map<String, Object> issue(TrialOperation trialOperation, MktCampaignDO campaignDO,MktStrategyConfDO strategyConfDO,String perCampaign){
        Map<String, Object> result = new HashMap<>();
        //添加策略适用地市
        redisUtils.set("STRATEGY_CONF_AREA_" + trialOperation.getStrategyId(), strategyConfDO.getAreaId());
        //查询活动下面所有渠道属性id是21和22的value
        List<String> attrValue = mktCamChlConfAttrMapper.selectAttrLabelValueByCampaignId(trialOperation.getCampaignId());
        // 通过活动id获取关联的标签字段数组
        List<LabelDTO> labelDTOList = mktCamDisplayColumnRelMapper.selectLabelDisplayListByCamId(campaignDO.getMktCampaignId());
        if (labelDTOList == null) {
            labelDTOList = new ArrayList<>();
        }
        String[] fieldList = new String[labelDTOList.size() + attrValue.size()];

        List<Map<String, Object>> labelList = new ArrayList<>();
        for (int i = 0; i < labelDTOList.size(); i++) {
            fieldList[i] = labelDTOList.get(i).getLabelCode();
            Map<String, Object> label = new HashMap<>();
            label.put("code", labelDTOList.get(i).getLabelCode());
            label.put("name", labelDTOList.get(i).getInjectionLabelName());
            label.put("labelType", labelDTOList.get(i).getLabelType());
            label.put("labelDataType",labelDTOList.get(i).getLabelDataType());
            labelList.add(label);
        }

        for (int i = labelDTOList.size(); i < labelDTOList.size() + attrValue.size(); i++) {
            fieldList[i] = attrValue.get(i - labelDTOList.size());
        }
        List<Long> attrList = mktCamChlConfAttrMapper.selectByCampaignId(trialOperation.getCampaignId());
        if (attrList.contains(ISEE_CUSTOMER.getArrId()) || attrList.contains(ISEE_LABEL_CUSTOMER.getArrId()) || attrList.contains(SERVICE_PACKAGE.getArrId())) {
            Map<String, Object> label = new HashMap<>();
            label.put("code", "SALE_EMP_NBR");
            label.put("name", "接单人号码");
            label.put("labelDataType", "1200");
            labelList.add(label);
        }
        if (attrList.contains(ISEE_AREA.getArrId()) || attrList.contains(ISEE_LABEL_AREA.getArrId())) {
            Map<String, Object> label = new HashMap<>();
            label.put("code", "AREA");
            label.put("name", "派单区域");
            label.put("labelDataType", "1200");
            labelList.add(label);
        }

        redisUtils.set("LABEL_DETAIL_" + trialOperation.getBatchNum(), labelList);
        List<Map<String, Object>> iSaleDisplay = new ArrayList<>();
        iSaleDisplay = (List<Map<String, Object>>) redisUtils.get("EVT_ISALE_LABEL_" + campaignDO.getIsaleDisplay());
        if (iSaleDisplay == null) {
            iSaleDisplay = injectionLabelMapper.listLabelByDisplayId(campaignDO.getIsaleDisplay());
            redisUtils.set("EVT_ISALE_LABEL_" + campaignDO.getIsaleDisplay(), iSaleDisplay);
        }
        redisUtils.set("ISALE_LABEL_" + trialOperation.getBatchNum(), iSaleDisplay);

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
                    ruleMap.put("closeType",closeR.getCloseType());
                    closeRule.add(ruleMap);
                }
            }
            redisUtils_es.set("CLOSE_RULE_"+campaignDO.getMktCampaignId(),closeRule);
        }

        TrialOperationVO request = BeanUtil.create(trialOperation, new TrialOperationVO());
        request.setFieldList(fieldList);
        request.setCampaignType(campaignDO.getMktCampaignType());
        request.setLanId(campaignDO.getLanId());
        request.setCamLevel(campaignDO.getCamLevel());
        // 获取创建人员code
        request.setStaffCode(getCreater(campaignDO.getCreateStaff()) == null ? "null" : getCreater(campaignDO.getCreateStaff()));

        TrialOperationVOES requests = BeanUtil.create(request, new TrialOperationVOES());
        ArrayList<TrialOperationParamES> paramList = new ArrayList<>();
        List<MktStrategyConfRuleRelDO> ruleRelList = ruleRelMapper.selectByMktStrategyConfId(request.getStrategyId());
        for (MktStrategyConfRuleRelDO ruleRelDO : ruleRelList) {
            TrialOperationParamES param = getTrialOperationParamES(request, trialOperation.getBatchNum(), ruleRelDO.getMktStrategyConfRuleId(), false);
            List<LabelResultES> labelResultList = param.getLabelResultList();
            paramList.add(param);
        }
        requests.setParamList(paramList);
        final TrialOperationVOES issureRequest = requests;
        System.out.println(JSON.toJSONString(requests));
        try {
            new Thread() {
                public void run() {
                    esService.strategyIssure(issureRequest);
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
        result.put("resultMsg", campaignDO.getMktCampaignName() + "&&&" + strategyConfDO.getMktStrategyConfName());
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
        TrialOperationParamES trialOperationParamES = trialOperationService.getTrialOperationParamES(operationVO, batchNum, ruleId, isSample, null);
        return trialOperationParamES;
    }
}
