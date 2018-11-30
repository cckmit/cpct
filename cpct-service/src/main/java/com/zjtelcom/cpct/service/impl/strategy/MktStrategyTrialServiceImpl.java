/**
 * @(#)MktStrategyTrialServiceImpl.java, 2018/8/10.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.strategy;

import com.zjtelcom.cpct.domain.channel.MktProductRule;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRule;
import com.zjtelcom.cpct.service.campaign.MktCamChlConfService;
import com.zjtelcom.cpct.service.channel.ProductService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfRuleService;
import com.zjtelcom.cpct.service.strategy.MktStrategyTrialService;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/08/10 17:20
 * version: V1.0
 */
@Service
public class MktStrategyTrialServiceImpl implements MktStrategyTrialService {

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 策略配置规则
     */
    @Autowired
    private MktStrategyConfRuleService mktStrategyConfRuleService;

    /**
     * 规则下的销售品
     */
    @Autowired
    private ProductService productService;

    /**
     * 首次协同渠道
     */
    @Autowired
    private MktCamChlConfService mktCamChlConfService;

    /**
     * 策略试运算下发
     *
     * @param batchNum              批次号
     * @param mktStrategyConfRuleId 规则Id
     */
    @Override
    public void StrategyTrial(Long batchNum, Long mktStrategyConfRuleId) {
        // 根据批次号获取同一批次的人员信息
        Map<String, Object> customerMap = (Map<String, Object>) redisUtils.hKeys(String.valueOf(batchNum));
        // 获取客户号集合
        List<String> customerList = new ArrayList<>(customerMap.keySet());
        // TODO 获取客户信息集合


        // 根据规则Id获取 客户分群信息、销售品信息、渠道信息、等
        Map<String, Object> mktStrategyConfRuleMap = mktStrategyConfRuleService.getMktStrategyConfRule(mktStrategyConfRuleId);
        MktStrategyConfRule mktStrategyConfRule = (MktStrategyConfRule) mktStrategyConfRuleMap.get("mktStrategyConfRule");

        // 获取客户分群规则信息
        mktStrategyConfRule.getTarGrpId();

        // 获取销售品信息
        Map<String, Object> MktProductMap = productService.getProductRuleList(UserUtil.loginId(), mktStrategyConfRule.getProductIdlist());
        List<MktProductRule> mktProductList = (List<MktProductRule>) MktProductMap.get("ruleList");

        // 获取协同渠道信息
        List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
        List<MktCamChlConfDetail> mktCamChlConfList = mktStrategyConfRule.getMktCamChlConfDetailList();
        for (MktCamChlConfDetail mktCamChlConf : mktCamChlConfList) {
            Map<String, Object> mktCamChlConfDetailMap = mktCamChlConfService.getMktCamChlConf(mktCamChlConf.getEvtContactConfId());
            MktCamChlConfDetail mktCamChlConfDetail = (MktCamChlConfDetail) mktCamChlConfDetailMap.get("mktCamChlConfDetail");
            mktCamChlConfDetailList.add(mktCamChlConfDetail);
        }

        int threadNum = 1000;
        int delNum = customerList.size() / threadNum;
        // 将前面的变量转换成final类型的，以便在new Thread中使用
        final List<String> customerListThread = customerList;
        final Long batchNumThread = batchNum;
        final List<MktCamChlConfDetail> mktCamChlConfDetailListThread = mktCamChlConfDetailList;
        final List<MktProductRule> mktProductListThread = mktProductList;
        for (int i = 0; i < threadNum; i++) {
            final int startNum = i * delNum;
            final int endNum = (i + 1) * delNum;
            new Thread() {
                public void run() {
                    // 遍历人员，为其匹配对应的销售品和推送渠道
                    for (int j = startNum; j < endNum; j++) {
                        //遍历推送渠道
                        for (MktCamChlConfDetail mktCamChlConfDetail : mktCamChlConfDetailListThread) {
                            // 遍历销售品
                            for (MktProductRule mktProductRule : mktProductListThread){

                                Map<String, Object> mktIssueDetailMap = new HashMap<>();
                                mktIssueDetailMap.put("mktCamChlConfDetail", mktCamChlConfDetail);
                                mktIssueDetailMap.put("mktProductRule", mktProductRule);
                                // TODO 客户信息
                                // mktIssueDetailMap.put("", );

                                // 将客户信息，销售品，推送渠道存入redis
                                redisUtils.hmSet("ISSUE"+batchNumThread, customerListThread, mktIssueDetailMap);
                            }
                        }
                    }
                }
            }.start();
        }
    }
}