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
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamChlConfMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamDisplayColumnRelMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignRelMapper;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TrialOperationMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamChlConfDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignRelDO;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.dto.channel.LabelDTO;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.enums.AreaCodeEnum;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.synchronize.campaign.SyncActivityService;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

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
    private MktCampaignRelMapper mktCampaignRelMapper;

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

    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper;

    @Autowired
    private ContactChannelMapper contactChannelMapper;

    @Autowired
    private MktCamDisplayColumnRelMapper mktCamDisplayColumnRelMapper;

    @Override
    public ResponseHeaderModel syncActivity(Long mktCampaignId) {
        // 获取活动基本信息
        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
        ActivityModel activityModel = new ActivityModel();
        activityModel.setActivityId(mktCampaignDO.getInitId().toString());
        activityModel.setActivityCode(mktCampaignDO.getMktActivityNbr());
        activityModel.setActivityName(mktCampaignDO.getMktCampaignName());
        activityModel.setStartDate(mktCampaignDO.getPlanBeginTime());
        activityModel.setEndDate(mktCampaignDO.getPlanEndTime());
        if ("1000".equals(mktCampaignDO.getTiggerType())) {
            activityModel.setHandoutType("1");
        } else if ("2000".equals(mktCampaignDO.getTiggerType())) {
            activityModel.setHandoutType("0");
        } else if ("3000".equals(mktCampaignDO.getTiggerType())) {
            activityModel.setHandoutType("2");
        }
        if(mktCampaignDO.getMktCampaignType().equals(StatusCode.MARKETING_CAMPAIGN.getStatusCode())) {
            activityModel.setMarketingType("1");
        } else if (mktCampaignDO.getMktCampaignType().equals(StatusCode.SERVICE_CAMPAIGN.getStatusCode())) {
            activityModel.setMarketingType("2");
        } else {
            activityModel.setMarketingType("3");
        }
        try {
            // 同步子活动的initId
            List<String> childCampaignIdList = new ArrayList<>();
            List<MktCampaignRelDO> mktCampaignRelDOS = mktCampaignRelMapper.selectByAmktCampaignId(mktCampaignId, StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            for (MktCampaignRelDO mktCampaignRelDO : mktCampaignRelDOS) {
                if (mktCampaignRelDO != null && mktCampaignRelDO.getzMktCampaignId()!=null) {
                    childCampaignIdList.add(mktCampaignRelDO.getzMktCampaignId().toString());
                }
            }
            activityModel.setChildCampaignList(childCampaignIdList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //省份标识
        activityModel.setPrvnceId(AreaCodeEnum.ZHEJIAGN.getRegionId().toString());
        //账期
        SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
        activityModel.setMonthId(Integer.parseInt(df.format(DateUtil.getCurrentTime())));
        //展示列标签集合
        List<LabelDTO> labelDTOS = mktCamDisplayColumnRelMapper.selectLabelDisplayListByCamId(mktCampaignId);
        List<String> displayLabelList = new ArrayList<>();
        for (LabelDTO labelDTO : labelDTOS) {
            displayLabelList.add(labelDTO.getLabelCode());
        }
        activityModel.setDisplayLabelList(displayLabelList);

        //话术标签集合
        List<CamScript> camScriptList = mktCamScriptMapper.selectByCampaignId(mktCampaignId);
        List<String> scriptLabelList = new ArrayList<>();
        for (CamScript camScript : camScriptList) {
            String desc = camScript.getScriptDesc();
            if(desc!=null){
                while (desc.indexOf("${")>0){
                    while(desc.indexOf("}$")>0){
                        int start = desc.indexOf("${");
                        int end = desc.indexOf("}$");
                        String label = desc.substring(start+2, end);
                        if(!scriptLabelList.contains(label)){
                            scriptLabelList.add(label);
                        }
                        desc = desc.substring(end+2, desc.length());
                        break;
                    }
                }
            }
        }
        activityModel.setScriptLabelList(scriptLabelList);

        List<PolicyModel> policyList = new ArrayList<>();
        //获取活动下策略信息
        List<MktStrategyConfDO> strategyConfList = mktStrategyConfMapper.selectByCampaignId(mktCampaignId);
        for (MktStrategyConfDO mktStrategyConfDO : strategyConfList) {
            PolicyModel policyModel = new PolicyModel();
            policyModel.setPolicyId(mktStrategyConfDO.getInitId().toString());
            policyModel.setPolicyName(mktStrategyConfDO.getMktStrategyConfName());
            policyModel.setStartDate(mktStrategyConfDO.getBeginTime());
            policyModel.setEndDate(mktStrategyConfDO.getEndTime());
            policyModel.setHandoutType(activityModel.getHandoutType());
            policyModel.setBatchId(DateUtil.date2St4Trial(new Date()) + ChannelUtil.getRandomStr(4));

            List<RuleModel> ruleList = new ArrayList<>();
            // 获取策略下规则信息
            List<MktStrategyConfRuleDO> mktStrategyConfRuleDOList = mktStrategyConfRuleMapper.selectByMktStrategyConfId(mktStrategyConfDO.getMktStrategyConfId());
            for (MktStrategyConfRuleDO mktStrategyConfRuleDO : mktStrategyConfRuleDOList) {
                RuleModel ruleModel = new RuleModel();
                ruleModel.setRuleId(mktStrategyConfRuleDO.getInitId().toString());
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

                //接触渠道
                List<String> channelList = new ArrayList<>();
                String[] evtContactConfIdList = mktStrategyConfRuleDO.getEvtContactConfId().split("/");
                for(int i=0; i<evtContactConfIdList.length; i++) {
                    MktCamChlConfDO mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(Long.valueOf(evtContactConfIdList[i]));
                    if(mktCamChlConfDO != null) {
                        Channel channel = contactChannelMapper.selectByPrimaryKey(mktCamChlConfDO.getContactChlId());
                        Channel contactChannel = contactChannelMapper.selectByPrimaryKey(channel.getParentId());
                        if(contactChannel.getContactChlCode().equals("QD40001")) {
                            channelList.add("1");
                        } else if(contactChannel.getContactChlCode().equals("QD40002")) {
                            channelList.add("3");
                        } else if(contactChannel.getContactChlCode().equals("QD40003")) {
                            channelList.add("4");
                        } else if(contactChannel.getContactChlCode().equals("QD40004")) {
                            channelList.add("2");
                        } else if(contactChannel.getContactChlCode().equals("QD40005")) {
                            channelList.add("6");
                        } else if(contactChannel.getContactChlCode().equals("QD40006")) {
                            channelList.add("5");
                        } else if(contactChannel.getContactChlCode().equals("QD40007")) {
                            channelList.add("7");
                        }
                    }
                }
                StringBuilder salexChannel = new StringBuilder(channelList.get(0));
                for(int i=1; i<channelList.size(); i++) {
                    salexChannel.append(",").append(channelList.get(i));
                }
                ruleModel.setSalexChannel(salexChannel.toString());
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


    @Override
    public Map<String,Object> syncTotalActivity() {
        Map<String,Object> result = new HashMap<>();
        List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.selectAll();
        List<Long> mktCampaignIdList = new ArrayList<>();
        for(MktCampaignDO mktCampaignDO : mktCampaignDOList) {
            if(mktCampaignDO.getStatusCd().equals(StatusCode.STATUS_CODE_PUBLISHED.getStatusCode())) {
                String mktCampaignName = mktCampaignDO.getMktCampaignName();
                if(!mktCampaignName.contains("测试") && !mktCampaignName.contains("模板") && !mktCampaignName.contains("test") && !mktCampaignName.contains("演示") && !mktCampaignName.equals("")) {
                    try {
                        syncActivity(mktCampaignDO.getMktCampaignId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mktCampaignIdList.add(mktCampaignDO.getMktCampaignId());
                }
            }
        }
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg","同步成功");
        result.put("campaignTotalNumber", mktCampaignIdList.size());
        return result;
    }

}