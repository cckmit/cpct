/**
 * @(#)SyncActivityServiceImpl.java, 2018/9/25.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.synchronize.campaign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zjhcsoft.eagle.main.dubbo.model.policy.*;
import com.zjhcsoft.eagle.main.dubbo.service.ActivitySyncService;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.MktCamScriptMapper;
import com.zjtelcom.cpct.dao.channel.MktVerbalMapper;
import com.zjtelcom.cpct.dao.channel.OfferMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TrialOperationMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.CamScript;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.MktVerbal;
import com.zjtelcom.cpct.domain.channel.Offer;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.enums.TrialCreateType;
import com.zjtelcom.cpct.service.synchronize.campaign.SyncActivityService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @author: linchao
 * @date: 2018/09/25 15:48
 * @version: V1.0
 */
@Service
@Transactional
public class SyncActivityServiceImpl implements SyncActivityService {

    @Autowired
    private MktCampaignMapper mktCampaignMapper;

    @Autowired
    private MktStrategyConfMapper mktStrategyConfMapper;

    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;

    @Autowired
    private OfferMapper offerMapper;

    @Autowired
    private MktVerbalMapper mktVerbalMapper;

    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;

    @Autowired
    private InjectionLabelMapper injectionLabelMapper;

    @Autowired(required = false)
    private ActivitySyncService activitySyncService;

    @Autowired
    private TrialOperationMapper trialOperationMapper;

    @Autowired
    private MktCamScriptMapper mktCamScriptMapper;

    @Override
    public ResponseHeaderModel syncActivity(Long mktCampaignId) {
        // 获取活动基本信息
        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
        ActivityModel activityModel = new ActivityModel();
        activityModel.setActivityId(mktCampaignDO.getMktCampaignId().toString());
        activityModel.setActivityCode(mktCampaignDO.getMktActivityNbr());
        activityModel.setActivityName(mktCampaignDO.getMktCampaignName());
        activityModel.setStartDate(mktCampaignDO.getPlanBeginTime());
        activityModel.setEndDate(mktCampaignDO.getPlanEndTime());
        if ("1000".equals(mktCampaignDO.getTiggerType())) {
            activityModel.setHandoutType("1");
        } else if ("2000".equals(mktCampaignDO.getTiggerType())) {
            activityModel.setHandoutType("0");
        }
        List<PolicyModel> policyList = new ArrayList<>();
        //获取活动下策略信息
        List<MktStrategyConfDO> strategyConfList = mktStrategyConfMapper.selectByCampaignId(mktCampaignId);
        for (MktStrategyConfDO mktStrategyConfDO : strategyConfList) {
            PolicyModel policyModel = new PolicyModel();
            policyModel.setPolicyId(mktStrategyConfDO.getMktStrategyConfId().toString());
            policyModel.setPolicyName(mktStrategyConfDO.getMktStrategyConfName());
            policyModel.setStartDate(mktStrategyConfDO.getBeginTime());
            policyModel.setEndDate(mktStrategyConfDO.getEndTime());
            policyModel.setHandoutType(activityModel.getHandoutType());
            //通过策略id  得到对应的批次id 按降序取第一个批次id
            List<TrialOperation> operationListByStrategyId = trialOperationMapper.findOperationListByStrategyId(mktStrategyConfDO.getMktStrategyConfId(),TrialCreateType.TRIAL_OPERATION.getValue());
            if(!operationListByStrategyId.isEmpty()){
                policyModel.setBatchId(operationListByStrategyId.get(0).getBatchNum().toString());
            }


            List<RuleModel> ruleList = new ArrayList<>();
            // 获取策略下规则信息
            List<MktStrategyConfRuleDO> mktStrategyConfRuleDOList = mktStrategyConfRuleMapper.selectByMktStrategyConfId(mktStrategyConfDO.getMktStrategyConfId());
            for (MktStrategyConfRuleDO mktStrategyConfRuleDO : mktStrategyConfRuleDOList) {
                RuleModel ruleModel = new RuleModel();
                ruleModel.setRuleId(mktStrategyConfRuleDO.getMktStrategyConfRuleId().toString());
                ruleModel.setRuleName(mktStrategyConfRuleDO.getMktStrategyConfRuleName());
                // 销售品
                String[] productIds = mktStrategyConfRuleDO.getProductId().split("/");
                List<ProductModel> productModelList = new ArrayList<>();
                if (productIds != null && !"".equals(productIds[0])) {
                    for (String productId : productIds) {
                        ProductModel productModel = new ProductModel();
                        Offer offer = offerMapper.selectByCamItemId(Long.valueOf(productId));
                        if(offer!=null) {
                            productModel.setProductId(offer.getOfferId().toString());
                            productModel.setProductCode(offer.getOfferNbr());
                            productModel.setProductName(offer.getOfferName());
                            productModel.setProductDesc(offer.getOfferDesc());
                            productModelList.add(productModel);
                        }
                    }
                }
                ProductDmsModel productDmsModel = new ProductDmsModel();
                productDmsModel.setProductGroups(productModelList);
                //得到销售品对应的推荐指引和id  只取第一条推荐指引话术
                String[] split = mktStrategyConfRuleDO.getEvtContactConfId().split("/");
                if (split != null && !"".equals(split[0])) {
                    CamScript script = mktCamScriptMapper.selectByConfId(Long.valueOf(split[0]));
                    if(null!=script){
                        productDmsModel.setRecommend(script.getScriptDesc());
                        productDmsModel.setRecommendProductId(script.getMktCampaignScptId().toString());
                    }
                }

                ruleModel.setProductDms(productDmsModel);
                // 话术
                List<VerbalDmsModel> verbalDmsModelList = new ArrayList<>();
                String[] evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId().split("/");
                for (String evtContactConfId : evtContactConfIds) {
                    if(!StringUtils.isBlank(evtContactConfId)){
                        List<MktVerbal> mktVerbalList = mktVerbalMapper.findVerbalListByConfId(Long.valueOf(evtContactConfId));
                        for (MktVerbal mktVerbal : mktVerbalList) {
                            VerbalDmsModel verbalDmsModel = new VerbalDmsModel();
                            verbalDmsModel.setVerbalId(mktVerbal.getVerbalId().toString());
                            verbalDmsModel.setVerbalContext(mktVerbal.getScriptDesc());
                            verbalDmsModelList.add(verbalDmsModel);
                        }
                    }

                }
                ruleModel.setVerbalDms(verbalDmsModelList);

                // 分群条件 + 标签
                List<TarGrpConditionModel> tarGrpConditionModelList = new ArrayList<>();
                List<LabelModel> labelModelList = new ArrayList<>();
                if (mktStrategyConfRuleDO.getTarGrpId() != null && !"".equals(mktStrategyConfRuleDO.getTarGrpId())) {
                    List<TarGrpCondition> conditionList = tarGrpConditionMapper.listTarGrpCondition(mktStrategyConfRuleDO.getTarGrpId());
                    if (conditionList != null && conditionList.size() > 0) {
                        for (TarGrpCondition tarGrpCondition : conditionList) {
                            TarGrpConditionModel tarGrpConditionModel = new TarGrpConditionModel();
                            LabelModel labelModel = new LabelModel();
                            // 通过左参获取标签
                            Label label = injectionLabelMapper.selectByPrimaryKey(Long.valueOf(tarGrpCondition.getLeftParam()));
                            // 获取分群条件
                            tarGrpConditionModel.setLeftParam(label.getInjectionLabelCode());
                            tarGrpConditionModel.setOperType(tarGrpCondition.getOperType());
                            tarGrpConditionModel.setRightParam(tarGrpCondition.getRightParam());
                            tarGrpConditionModelList.add(tarGrpConditionModel);
                            // 获取标签
                            labelModel.setInjectionLabelCode(label.getInjectionLabelCode());
                            labelModel.setInjectionLabelName(label.getInjectionLabelName());
                            labelModelList.add(labelModel);
                        }
                    }
                }
                ruleModel.setLabelList(labelModelList);
                ruleModel.setTarGrpConditionList(tarGrpConditionModelList);
                ruleList.add(ruleModel);
            }
            policyModel.setRuleList(ruleList);
            policyList.add(policyModel);
        }
        activityModel.setPolicyList(policyList);

        System.out.println("同步大数据请求信息："+JSONObject.parseObject(JSON.toJSONString(activityModel)));
        //调用同步大数据
        ResponseHeaderModel responseHeaderModel1 = activitySyncService.syncActivity(activityModel);
        System.out.println("大数据接口返回信息："+responseHeaderModel1.getResultCode()+"  "+responseHeaderModel1.getResultMessage());

        ResponseHeaderModel responseHeaderModel = new ResponseHeaderModel();
        responseHeaderModel.setResultCode("0");
        responseHeaderModel.setResultMessage("同步成功！");
        return responseHeaderModel;
    }


}