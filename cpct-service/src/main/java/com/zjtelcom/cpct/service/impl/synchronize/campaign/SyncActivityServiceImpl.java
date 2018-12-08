/**
 * @(#)SyncActivityServiceImpl.java, 2018/9/25.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.synchronize.campaign;

import com.zjhcsoft.eagle.main.dubbo.model.policy.*;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.MktVerbalMapper;
import com.zjtelcom.cpct.dao.channel.OfferMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.MktVerbal;
import com.zjtelcom.cpct.domain.channel.Offer;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.service.synchronize.campaign.SyncActivityService;
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
        if (mktCampaignDO.getTiggerType() == "1000") {
            activityModel.setHandoutType("1");
        } else if (mktCampaignDO.getTiggerType() == "2000") {
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
                        productModel.setProductId(offer.getOfferId().toString());
                        productModel.setProductCode(offer.getOfferNbr());
                        productModel.setProductName(offer.getOfferName());
                        productModel.setProductDesc(offer.getOfferDesc());
                        productModelList.add(productModel);
                    }
                }
                ProductDmsModel productDmsModel = new ProductDmsModel();
                productDmsModel.setProductGroups(productModelList);
                ruleModel.setProductDms(productDmsModel);
                // 话术
                List<VerbalDmsModel> verbalDmsModelList = new ArrayList<>();
                String[] evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId().split("/");
                for (String evtContactConfId : evtContactConfIds) {
                    List<MktVerbal> mktVerbalList = mktVerbalMapper.findVerbalListByConfId(Long.valueOf(evtContactConfId));
                    for (MktVerbal mktVerbal : mktVerbalList) {
                        VerbalDmsModel verbalDmsModel = new VerbalDmsModel();
                        verbalDmsModel.setVerbalId(mktVerbal.getVerbalId().toString());
                        verbalDmsModel.setVerbalContext(mktVerbal.getScriptDesc());
                        verbalDmsModelList.add(verbalDmsModel);
                    }
                }
                ruleModel.setVerbalDms(verbalDmsModelList);
                ruleList.add(ruleModel);
            }
            policyModel.setRuleList(ruleList);
            policyList.add(policyModel);
        }
        activityModel.setPolicyList(policyList);


        ResponseHeaderModel responseHeaderModel = new ResponseHeaderModel();
        responseHeaderModel.setResultCode("0");
        responseHeaderModel.setResultMessage("同步成功！");
        return responseHeaderModel;
    }


}