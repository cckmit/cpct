/**
 * @(#)MktStrategyConfServiceImpl.java, 2018/6/25.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.strategy;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamChlConfMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamStrategyConfRelMapper;
import com.zjtelcom.cpct.dao.campaign.MktCpcAlgorithmsRulMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamStrategyConfRelDO;
import com.zjtelcom.cpct.domain.campaign.MktCpcAlgorithmsRulDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConf;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRule;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfRuleService;

import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/06/25 17:23
 * version: V1.0
 */
@Transactional
@Service
public class MktStrategyConfRuleServiceImpl extends BaseService implements MktStrategyConfRuleService {

    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;

    @Autowired
    private MktStrategyConfRuleRelMapper mktStrategyConfRuleRelMapper;

    @Autowired
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper; //cpc算法与活动关联表

    @Autowired
    private MktCpcAlgorithmsRulMapper mktCpcAlgorithmsRulMapper; //cpc算法规则表

    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper;

    /**
     * 添加策略规则
     *
     * @param mktStrategyConfRule
     * @return
     */
    @Override
    public Map<String, Object> saveMktStrategyConfRule(MktStrategyConfRule mktStrategyConfRule) {
        Map<String, Object> mktStrategyConfRuleMap = new HashMap<>();
        try {
            MktStrategyConfRuleDO ktStrategyConfRuleDO = new MktStrategyConfRuleDO();
            CopyPropertiesUtil.copyBean2Bean(ktStrategyConfRuleDO, mktStrategyConfRule);
            mktStrategyConfRuleMapper.insert(ktStrategyConfRuleDO);

            //添加cpc算法规则
            MktCpcAlgorithmsRulDO mktCpcAlgorithmsRulDO = new MktCpcAlgorithmsRulDO();
            mktCpcAlgorithmsRulDO.setAlgorithmsRulName(mktStrategyConfRule.getMktStrategyConfRuleName());
            mktCpcAlgorithmsRulDO.setRuleDesc("");
            mktCpcAlgorithmsRulDO.setRuleExpression(mktStrategyConfRule.getTarGrpId() + "");
            mktCpcAlgorithmsRulMapper.insert(mktCpcAlgorithmsRulDO);
            //关联表
            MktCamStrategyConfRelDO mktCamStrategyConfRelDO = new MktCamStrategyConfRelDO();
            mktCamStrategyConfRelDO.setMktCampaignId(1L);
            mktCamStrategyConfRelDO.setStrategyConfId(mktCpcAlgorithmsRulDO.getAlgorithmsRulId());
            mktCamStrategyConfRelMapper.insert(mktCamStrategyConfRelDO);

            mktStrategyConfRuleMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfRuleMap.put("resultMsg", ErrorCode.SAVE_MKT_RULE_STR_CONF_RULE_SUCCESS.getErrorMsg());
            mktStrategyConfRuleMap.put("mktStrategyConfId", mktStrategyConfRule.getMktStrategyConfRuleId());
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfRuleServiceImpl] failed to save MktStrategyConfRule = {}", JSON.toJSON(mktStrategyConfRule));
            mktStrategyConfRuleMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStrategyConfRuleMap.put("resultMsg", ErrorCode.SAVE_MKT_RULE_STR_CONF_RULE_FAILURE.getErrorMsg());
            mktStrategyConfRuleMap.put("mktStrategyConfId", mktStrategyConfRule.getMktStrategyConfRuleId());
        }
        return mktStrategyConfRuleMap;
    }

    /**
     * 修改策略规则
     *
     * @param mktStrategyConfRule
     * @return
     */
    @Override
    public Map<String, Object> updateMktStrategyConfRule(MktStrategyConfRule mktStrategyConfRule) {
        Map<String, Object> mktStrategyConfRuleMap = new HashMap<>();
        try {
            MktStrategyConfRuleDO ktStrategyConfRuleDO = new MktStrategyConfRuleDO();
            CopyPropertiesUtil.copyBean2Bean(ktStrategyConfRuleDO, mktStrategyConfRule);
            mktStrategyConfRuleMapper.updateByPrimaryKey(ktStrategyConfRuleDO);
            mktStrategyConfRuleMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfRuleMap.put("resultMsg", ErrorCode.UPDATE_MKT_RULE_STR_CONF_RULE_SUCCESS.getErrorMsg());
            mktStrategyConfRuleMap.put("mktStrategyConfId", ktStrategyConfRuleDO.getMktStrategyConfRuleId());
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfRuleServiceImpl] failed to save MktStrategyConfRule = {}", JSON.toJSON(mktStrategyConfRule));
            mktStrategyConfRuleMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStrategyConfRuleMap.put("resultMsg", ErrorCode.UPDATE_MKT_RULE_STR_CONF_RULE_FAILURE.getErrorMsg());
            mktStrategyConfRuleMap.put("mktStrategyConfId", mktStrategyConfRule.getMktStrategyConfRuleId());
        }
        return mktStrategyConfRuleMap;
    }

    /**
     * 查询策略规则
     *
     * @param mktStrategyConfRuleId
     * @return
     */
    @Override
    public Map<String, Object> getMktStrategyConfRule(Long mktStrategyConfRuleId) {
        Map<String, Object> mktStrategyConfRuleMap = null;
        MktStrategyConfRuleDO mktStrategyConfRuleDO = new MktStrategyConfRuleDO();
        MktStrategyConfRule mktStrategyConfRule = new MktStrategyConfRule();
        try {
            mktStrategyConfRuleMap = new HashMap<>();
            mktStrategyConfRuleDO = mktStrategyConfRuleMapper.selectByPrimaryKey(mktStrategyConfRuleId);
            CopyPropertiesUtil.copyBean2Bean(mktStrategyConfRule, mktStrategyConfRuleDO);
            String[] productIds = mktStrategyConfRuleDO.getProductId().split("/");
            List<Long> productIdList = new ArrayList<>();
            for (int i = 0; i < productIds.length; i++) {
                if (productIds[i] != "" && !"".equals(productIds[i])) {
                    productIdList.add(Long.valueOf(productIds[i]));
                }
            }
            String[] evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId().split("/");
            List<MktCamChlConf> mktCamChlConfList = new ArrayList<>();
            for (int i = 0; i < evtContactConfIds.length; i++) {
                if (evtContactConfIds[i] != "" && !"".equals(evtContactConfIds[i])) {
                    MktCamChlConf mktCamChlConf = new MktCamChlConf();
                    mktCamChlConf.setEvtContactConfId(Long.valueOf(evtContactConfIds[i]));
                    String evtContactConfName = mktCamChlConfMapper.selectforName(Long.valueOf(evtContactConfIds[i]));
                    mktCamChlConf.setEvtContactConfName(evtContactConfName);
                    mktCamChlConfList.add(mktCamChlConf);
                }
            }
            mktStrategyConfRule.setMktCamChlConfList(mktCamChlConfList);
            mktStrategyConfRule.setProductIdlist(productIdList);
            mktStrategyConfRuleMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfRuleMap.put("resultMsg", ErrorCode.GET_MKT_RULE_STR_CONF_RULE_SUCCESS.getErrorMsg());
            mktStrategyConfRuleMap.put("mktStrategyConfRule", mktStrategyConfRule);
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfRuleServiceImpl] failed to get MktStrategyConfRule = {}", JSON.toJSON(mktStrategyConfRuleDO), e);
            mktStrategyConfRuleMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStrategyConfRuleMap.put("resultMsg", ErrorCode.GET_MKT_RULE_STR_CONF_RULE_FAILURE.getErrorMsg());
            mktStrategyConfRuleMap.put("mktStrategyConfRule", mktStrategyConfRule);
        }
        return mktStrategyConfRuleMap;
    }

    /**
     * 查询策略规则列表
     *
     * @return
     */
    @Override
    public Map<String, Object> listAllMktStrategyConfRule() {
        Map<String, Object> mktStrategyConfRuleMap = new HashMap<>();
        List<MktStrategyConfRuleDO> mktStrategyConfRuleDOList = new ArrayList<>();
        try {
            mktStrategyConfRuleDOList = mktStrategyConfRuleMapper.selectAll();
            mktStrategyConfRuleMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfRuleMap.put("resultMsg", ErrorCode.GET_MKT_RULE_STR_CONF_RULE_SUCCESS.getErrorMsg());
            mktStrategyConfRuleMap.put("mktStrategyConfRuleDOList", mktStrategyConfRuleDOList);
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfRuleServiceImpl] failed to get the List of mktStrategyConfRuleDOList = {}", JSON.toJSON(mktStrategyConfRuleDOList));
            mktStrategyConfRuleMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStrategyConfRuleMap.put("resultMsg", ErrorCode.GET_MKT_RULE_STR_CONF_RULE_FAILURE.getErrorMsg());
            mktStrategyConfRuleMap.put("mktStrategyConfRuleDOList", mktStrategyConfRuleDOList);
        }
        return mktStrategyConfRuleMap;
    }

    /**
     * 通过策略Id 查询对应的规则列表（id+名称）
     *
     * @return
     */
    @Override
    public Map<String, Object> listAllMktStrategyConfRuleForName(Long mktStrategyConfId) {
        Map<String, Object> mktStrategyConfRuleMap = new HashMap<>();
        List<MktStrategyConfRule> mktStrategyConfRuleList = new ArrayList<>();
        try {
            // 通过关系表查询策略下对应的规则Id
            List<MktStrategyConfRuleRelDO> mktStrategyConfRuleRelDOList = mktStrategyConfRuleRelMapper.selectByMktStrategyConfId(mktStrategyConfId);
            for (MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO : mktStrategyConfRuleRelDOList) {
                MktStrategyConfRule mktStrategyConfRule = new MktStrategyConfRule();
                String mktStrategyConfRuleName = mktStrategyConfRuleMapper.selectMktStrategyConfRuleName(mktStrategyConfRuleRelDO.getMktStrategyConfRuleId());
                mktStrategyConfRule.setMktStrategyConfRuleId(mktStrategyConfRuleRelDO.getMktStrategyConfRuleId());
                mktStrategyConfRule.setMktStrategyConfRuleName(mktStrategyConfRuleName);
                mktStrategyConfRuleList.add(mktStrategyConfRule);
            }
            mktStrategyConfRuleMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfRuleMap.put("resultMsg", ErrorCode.GET_MKT_RULE_STR_CONF_RULE_SUCCESS.getErrorMsg());
            mktStrategyConfRuleMap.put("mktStrategyConfRuleList", mktStrategyConfRuleList);
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfRuleServiceImpl] failed to get the List of mktStrategyConfRuleList = {}", JSON.toJSON(mktStrategyConfRuleList));
            mktStrategyConfRuleMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStrategyConfRuleMap.put("resultMsg", ErrorCode.GET_MKT_RULE_STR_CONF_RULE_FAILURE.getErrorMsg());
            mktStrategyConfRuleMap.put("mktStrategyConfRuleList", mktStrategyConfRuleList);
        }
        return mktStrategyConfRuleMap;
    }

    /**
     * 删除策略规则
     *
     * @param mktStrategyConfRuleId
     * @return
     */
    @Override
    public Map<String, Object> deleteMktStrategyConfRule(Long mktStrategyConfRuleId) {
        Map<String, Object> mktStrategyConfRuleMap = new HashMap<>();
        try {
            //删除规则跟策略的关联
            mktStrategyConfRuleRelMapper.deleteByMktStrategyConfRulId(mktStrategyConfRuleId);
            //删除规则
            mktStrategyConfRuleMapper.deleteByPrimaryKey(mktStrategyConfRuleId);
            mktStrategyConfRuleMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfRuleMap.put("resultMsg", ErrorCode.GET_MKT_RULE_STR_CONF_RULE_SUCCESS.getErrorMsg());
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfRuleServiceImpl] failed to delete the mktStrategyConfRuleDO by mktStrategyConfRuleId = {}", mktStrategyConfRuleId);
            mktStrategyConfRuleMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStrategyConfRuleMap.put("resultMsg", ErrorCode.DELETE_MKT_RULE_STR_CONF_RULE_FAILURE.getErrorMsg());
        }
        return mktStrategyConfRuleMap;
    }
}