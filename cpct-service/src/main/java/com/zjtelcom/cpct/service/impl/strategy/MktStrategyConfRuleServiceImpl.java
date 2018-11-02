/**
 * @(#)MktStrategyConfServiceImpl.java, 2018/6/25.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.strategy;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.CamScript;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConf;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.campaign.MktCamChlResult;
import com.zjtelcom.cpct.dto.channel.CamScriptAddVO;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRule;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.MktCamChlConfService;
import com.zjtelcom.cpct.service.campaign.MktCamChlResultService;
import com.zjtelcom.cpct.service.channel.CamScriptService;
import com.zjtelcom.cpct.service.channel.ProductService;
import com.zjtelcom.cpct.service.grouping.TarGrpService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfRuleService;

import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

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

    @Autowired
    private MktCamChlResultService mktCamChlResultService;

    @Autowired
    private MktCamChlConfService mktCamChlConfService;

    @Autowired
    private TarGrpService tarGrpService;

    @Autowired
    private ProductService productService;

    @Autowired
    private MktCamResultRelMapper mktCamResultRelMapper;

    @Autowired
    private CamScriptService camScriptService;

    @Autowired
    private MktCamRecomCalcRelMapper mktCamRecomCalcRelMapper;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 添加策略规则
     *
     * @param mktStrategyConfRule
     * @return
     */
    @Override
    public Map<String, Object> saveMktStrategyConfRule(MktStrategyConfRule mktStrategyConfRule) {
        Map<String, Object> mktStrategyConfRuleMap = new HashMap<>();
        MktStrategyConfRuleDO mktStrategyConfRuleDO = new MktStrategyConfRuleDO();
        try {
            CopyPropertiesUtil.copyBean2Bean(mktStrategyConfRuleDO, mktStrategyConfRule);
            String productIds = "";
            String evtContactConfIds = "";
            if (mktStrategyConfRule.getProductIdlist() != null) {
                for (int i = 0; i < mktStrategyConfRule.getProductIdlist().size(); i++) {
                    if (i == 0) {
                        productIds += mktStrategyConfRule.getProductIdlist().get(i);
                    } else {
                        productIds += "/" + mktStrategyConfRule.getProductIdlist().get(i);
                    }
                }
                mktStrategyConfRuleDO.setProductId(productIds);
            }
            if (mktStrategyConfRule.getMktCamChlConfDetailList() != null) {
                for (int i = 0; i < mktStrategyConfRule.getMktCamChlConfDetailList().size(); i++) {
                    if (i == 0) {
                        evtContactConfIds += mktStrategyConfRule.getMktCamChlConfDetailList().get(i).getEvtContactConfId();
                    } else {
                        evtContactConfIds += "/" + mktStrategyConfRule.getMktCamChlConfDetailList().get(i).getEvtContactConfId();
                    }
                    // 保存话术
                    CamScriptAddVO camScriptAddVO = new CamScriptAddVO();
                    camScriptAddVO.setEvtContactConfId(mktStrategyConfRule.getMktCamChlConfDetailList().get(i).getEvtContactConfId());
                    camScriptAddVO.setMktCampaignId(mktStrategyConfRule.getMktCampaignId());
                    camScriptAddVO.setScriptDesc(mktStrategyConfRule.getMktCamChlConfDetailList().get(i).getScriptDesc());
                    camScriptService.addCamScript(UserUtil.loginId(), camScriptAddVO);
                }
                mktStrategyConfRuleDO.setEvtContactConfId(evtContactConfIds);
            }

            // 新增二次协同结果 并且 将结果id的存到规则中
            if (mktStrategyConfRule.getMktCamChlResultList() != null) {
                String mktCamChlResultIds = "";
                for (int i = 0; i < mktStrategyConfRule.getMktCamChlResultList().size(); i++) {
                    Map<String, Object> mktCamChlResultMap = mktCamChlResultService.saveMktCamChlResult(mktStrategyConfRule.getMktCamChlResultList().get(i));
                    MktCamChlResultDO mktCamChlResultDO = (MktCamChlResultDO) mktCamChlResultMap.get("mktCamChlResultDO");
                    Long mktCamChlResultId = mktCamChlResultDO.getMktCamChlResultId();
                    if (i == 0) {
                        mktCamChlResultIds += mktCamChlResultId;
                    } else {
                        mktCamChlResultIds += "/" + mktCamChlResultId;
                    }
                    // 判断类型是否为工单类型 , 保存二次营销结果和活动的关联
                    if ("1".equals(mktCamChlResultDO.getResultType())) {
                        MktCamResultRelDO mktCamResultRelDO = new MktCamResultRelDO();
                        mktCamResultRelDO.setMktCampaignId(mktStrategyConfRule.getMktCampaignId());
                        mktCamResultRelDO.setMktResultId(mktCamChlResultDO.getMktCamChlResultId());
                        mktCamResultRelDO.setStatus(StatusCode.STATUS_CODE_NOTACTIVE.getStatusCode()); // 未生效
                        mktCamResultRelDO.setCreateDate(new Date());
                        mktCamResultRelDO.setCreateStaff(UserUtil.loginId());
                        mktCamResultRelDO.setUpdateDate(new Date());
                        mktCamResultRelDO.setUpdateStaff(UserUtil.loginId());
                        mktCamResultRelMapper.insert(mktCamResultRelDO);
                    }
                }
                mktStrategyConfRuleDO.setMktCamChlResultId(mktCamChlResultIds);
            }
            mktStrategyConfRuleDO.setCreateStaff(UserUtil.loginId());
            mktStrategyConfRuleDO.setCreateDate(new Date());
            mktStrategyConfRuleDO.setUpdateStaff(UserUtil.loginId());
            mktStrategyConfRuleDO.setUpdateDate(new Date());
            mktStrategyConfRuleMapper.insert(mktStrategyConfRuleDO);

            //添加cpc算法规则
            MktCpcAlgorithmsRulDO mktCpcAlgorithmsRulDO = new MktCpcAlgorithmsRulDO();
            mktCpcAlgorithmsRulDO.setAlgorithmsRulName(mktStrategyConfRule.getMktStrategyConfRuleName());
            mktCpcAlgorithmsRulDO.setRuleDesc("");
            mktCpcAlgorithmsRulDO.setRuleExpression(mktStrategyConfRule.getTarGrpId() + "");
            mktCpcAlgorithmsRulMapper.insert(mktCpcAlgorithmsRulDO);
            //cpc算法规则关联表
            MktCamRecomCalcRelDO mktCamRecomCalcRelDO = new MktCamRecomCalcRelDO();
            mktCamRecomCalcRelDO.setMktCampaignId(mktStrategyConfRule.getMktCampaignId());
            mktCamRecomCalcRelDO.setAlgorithmsRulId(mktCpcAlgorithmsRulDO.getAlgorithmsRulId());
            mktCamRecomCalcRelMapper.insert(mktCamRecomCalcRelDO);
            //cpc算法表


            mktStrategyConfRuleMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfRuleMap.put("resultMsg", ErrorCode.SAVE_MKT_RULE_STR_CONF_RULE_SUCCESS.getErrorMsg());
            mktStrategyConfRuleMap.put("mktStrategyConfRuleDO", mktStrategyConfRuleDO);
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfRuleServiceImpl] failed to save MktStrategyConfRule = {}", JSON.toJSON(mktStrategyConfRule));
            mktStrategyConfRuleMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStrategyConfRuleMap.put("resultMsg", ErrorCode.SAVE_MKT_RULE_STR_CONF_RULE_FAILURE.getErrorMsg());
            mktStrategyConfRuleMap.put("mktStrategyConfRuleDO", mktStrategyConfRuleDO);
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
        String mktCamChlResultIds = "";
        String productIds = "";
        String evtContactConfIds = "";
        try {
            MktStrategyConfRuleDO mktStrategyConfRuleDO = new MktStrategyConfRuleDO();
            CopyPropertiesUtil.copyBean2Bean(mktStrategyConfRuleDO, mktStrategyConfRule);
            if (mktStrategyConfRule.getProductIdlist() != null) {
                for (int i = 0; i < mktStrategyConfRule.getProductIdlist().size(); i++) {
                    if (i == 0) {
                        productIds += mktStrategyConfRule.getProductIdlist().get(i);
                    } else {
                        productIds += "/" + mktStrategyConfRule.getProductIdlist().get(i);
                    }
                }
                mktStrategyConfRuleDO.setProductId(productIds);
            }
            if (mktStrategyConfRule.getMktCamChlConfDetailList() != null) {
                for (int i = 0; i < mktStrategyConfRule.getMktCamChlConfDetailList().size(); i++) {
                    Long evtContactConfId = mktStrategyConfRule.getMktCamChlConfDetailList().get(i).getEvtContactConfId();

                    CamScriptAddVO camScriptAddVO = new CamScriptAddVO();
                    camScriptAddVO.setEvtContactConfId(evtContactConfId);
                    camScriptAddVO.setMktCampaignId(mktStrategyConfRule.getMktCampaignId());
                    camScriptAddVO.setScriptDesc(mktStrategyConfRule.getMktCamChlConfDetailList().get(i).getScriptDesc());
                    camScriptService.addCamScript(UserUtil.loginId(), camScriptAddVO);

                    if (i == 0) {
                        evtContactConfIds += evtContactConfId;
                    } else {
                        evtContactConfIds += "/" + evtContactConfId;
                    }


                }
                mktStrategyConfRuleDO.setEvtContactConfId(evtContactConfIds);
            }
            if (mktStrategyConfRule.getMktCamChlResultList() != null) {
                for (int i = 0; i < mktStrategyConfRule.getMktCamChlResultList().size(); i++) {
                    Long mktCamChlResultId = 0L;
                    MktCamChlResult mktCamChlResult = mktStrategyConfRule.getMktCamChlResultList().get(i);
                    if (mktCamChlResult.getMktCamChlResultId() != null && mktCamChlResult.getMktCamChlResultId() != 0) {
                        // 修改已有的结果信息
                        mktCamChlResult.setMktCampaignId(mktStrategyConfRule.getMktCampaignId());
                        mktCamChlResultService.updateMktCamChlResult(mktCamChlResult);
                        mktCamChlResultId = mktCamChlResult.getMktCamChlResultId();
                    } else {
                        // 新增结果信息
                        Map<String, Object> mktCamChlResultMap = mktCamChlResultService.saveMktCamChlResult(mktCamChlResult);
                        MktCamChlResultDO mktCamChlResultDO = (MktCamChlResultDO) mktCamChlResultMap.get("mktCamChlResultDO");
                        mktCamChlResultId = mktCamChlResultDO.getMktCamChlResultId();
                    }
                    if (i == 0) {
                        mktCamChlResultIds += mktCamChlResultId;
                    } else {
                        mktCamChlResultIds += "/" + mktCamChlResultId;
                    }
                }
                mktStrategyConfRuleDO.setMktCamChlResultId(mktCamChlResultIds);
            }

            mktStrategyConfRuleDO.setUpdateStaff(UserUtil.loginId());
            mktStrategyConfRuleDO.setUpdateDate(new Date());
            mktStrategyConfRuleMapper.updateByPrimaryKey(mktStrategyConfRuleDO);

            mktStrategyConfRuleMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfRuleMap.put("resultMsg", ErrorCode.UPDATE_MKT_RULE_STR_CONF_RULE_SUCCESS.getErrorMsg());
            mktStrategyConfRuleMap.put("mktCamChlResultIds", mktCamChlResultIds);
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfRuleServiceImpl] failed to update MktStrategyConfRule = {}, mktCamChlResultIds = {}", JSON.toJSON(mktStrategyConfRule), mktCamChlResultIds, e);
            mktStrategyConfRuleMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStrategyConfRuleMap.put("resultMsg", ErrorCode.UPDATE_MKT_RULE_STR_CONF_RULE_FAILURE.getErrorMsg());
            mktStrategyConfRuleMap.put("mktCamChlResultIds", mktCamChlResultIds);
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
            if (mktStrategyConfRuleDO.getProductId() != null) {
                String[] productIds = mktStrategyConfRuleDO.getProductId().split("/");
                List<Long> productIdList = new ArrayList<>();
                for (int i = 0; i < productIds.length; i++) {
                    if (productIds[i] != "" && !"".equals(productIds[i]) && !productIds[i].equals(null)) {
                        productIdList.add(Long.valueOf(productIds[i]));
                    }
                }
                mktStrategyConfRule.setProductIdlist(productIdList);
            }

            if (mktStrategyConfRuleDO.getEvtContactConfId() != null) {
                String[] evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId().split("/");
                List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
                for (int i = 0; i < evtContactConfIds.length; i++) {
                    if (evtContactConfIds[i] != "" && !"".equals(evtContactConfIds[i]) && !evtContactConfIds[i].equals(null)) {
                        MktCamChlConfDetail mktCamChlConfDetail = new MktCamChlConfDetail();
                        mktCamChlConfDetail.setEvtContactConfId(Long.valueOf(evtContactConfIds[i]));
                        String evtContactConfName = mktCamChlConfMapper.selectforName(Long.valueOf(evtContactConfIds[i]));
                        mktCamChlConfDetail.setEvtContactConfName(evtContactConfName);
                        mktCamChlConfDetailList.add(mktCamChlConfDetail);
                    }
                }
                mktStrategyConfRule.setMktCamChlConfDetailList(mktCamChlConfDetailList);
            }

            if (mktStrategyConfRuleDO.getMktCamChlResultId() != null) {
                String[] mktCamChlResultIds = mktStrategyConfRuleDO.getMktCamChlResultId().split("/");
                List<MktCamChlResult> mktCamChlResultList = new ArrayList<>();
                if(mktCamChlResultIds!=null && !"".equals(mktCamChlResultIds[0])){
                    for (String mktCamChlResultId : mktCamChlResultIds) {
                        Map<String, Object> mktCamChlResultMap = mktCamChlResultService.getMktCamChlResult(Long.valueOf(mktCamChlResultId));
                        mktCamChlResultList.add((MktCamChlResult) mktCamChlResultMap.get("mktCamChlResult"));
                    }
                }
                mktStrategyConfRule.setMktCamChlResultList(mktCamChlResultList);
            }


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


    /**
     * 通过父规则Id复制策略规则
     *
     * @param parentMktStrategyConfRuleId
     * @param isPublish                   是否为发布操作
     * @return
     */
    @Override
    public Map<String, Object> copyMktStrategyConfRule(Long parentMktStrategyConfRuleId, Boolean isPublish) throws Exception {
        Map<String, Object> mktStrategyConfRuleMap = new HashMap<>();
        MktStrategyConfRuleDO mktStrategyConfRuleDO = mktStrategyConfRuleMapper.selectByPrimaryKey(parentMktStrategyConfRuleId);
        MktStrategyConfRuleDO chiledMktStrategyConfRuleDO = new MktStrategyConfRuleDO();
        /**
         * 客户分群配置
         */
        //判断是否为发布操作
        Map<String, Object> tarGrpMap = new HashMap<>();
        if (isPublish) {
            tarGrpMap = tarGrpService.copyTarGrp(mktStrategyConfRuleDO.getTarGrpId(), true);
        } else {
            tarGrpMap = tarGrpService.copyTarGrp(mktStrategyConfRuleDO.getTarGrpId(), false);
        }

        TarGrp tarGrp = (TarGrp) tarGrpMap.get("tarGrp");
        /**
         * 销售品配置
         */
        List<Long> productIdList = new ArrayList<>();
        if (mktStrategyConfRuleDO.getProductId() != null) {
            String[] productIds = mktStrategyConfRuleDO.getProductId().split("/");
            for (int i = 0; i < productIds.length; i++) {
                if (productIds[i] != "" && !"".equals(productIds[i])) {
                    productIdList.add(Long.valueOf(productIds[i]));
                }
            }
        }
        Map<String, Object> productRuleMap = productService.copyProductRule(UserUtil.loginId(), productIdList);
        List<Long> ruleIdList = (List<Long>) productRuleMap.get("ruleIdList");
        String childProductIds = "";
        for (int i = 0; i < ruleIdList.size(); i++) {
            if (i == 0) {
                childProductIds += ruleIdList.get(i);
            } else {
                childProductIds += "/" + ruleIdList.get(i);
            }
        }
        /**
         * 协同渠道配置
         */
        String childEvtContactConfIds = "";
        if (mktStrategyConfRuleDO.getEvtContactConfId() != null) {
            String[] evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId().split("/");
            if (evtContactConfIds != null && !"".equals(evtContactConfIds[0])) {
                for (int i = 0; i < evtContactConfIds.length; i++) {
                    if (evtContactConfIds[i] != "" && !"".equals(evtContactConfIds[i])) {
                        Map<String, Object> mktCamChlConfDOMap = mktCamChlConfService.copyMktCamChlConf(Long.valueOf(evtContactConfIds[i]));
                        MktCamChlConfDO mktCamChlConfDO = (MktCamChlConfDO) mktCamChlConfDOMap.get("mktCamChlConfDO");
                        if (i == 0) {
                            childEvtContactConfIds += mktCamChlConfDO.getEvtContactConfId();
                        } else {
                            childEvtContactConfIds += "/" + mktCamChlConfDO.getEvtContactConfId();
                        }
                    }
                }
            }
        }

        /**
         * 二次协同结果
         */
        String[] mktCamChlResultIds = mktStrategyConfRuleDO.getMktCamChlResultId().split("/");
        String childMktCamChlResultIds = "";
        if (mktCamChlResultIds != null && !"".equals(mktCamChlResultIds[0])) {
            for (int i = 0; i < mktCamChlResultIds.length; i++) {
                Map<String, Object> mktCamChlResultDOMap = mktCamChlResultService.copyMktCamChlResult(Long.valueOf(mktCamChlResultIds[i]));
                MktCamChlResultDO mktCamChlResultDO = (MktCamChlResultDO) mktCamChlResultDOMap.get("mktCamChlResultDO");
                if (i == 0) {
                    childMktCamChlResultIds += mktCamChlResultDO.getMktCamChlResultId();
                } else {
                    childMktCamChlResultIds += "/" + mktCamChlResultDO.getMktCamChlResultId();
                }
            }
        }
        chiledMktStrategyConfRuleDO.setMktStrategyConfRuleName(mktStrategyConfRuleDO.getMktStrategyConfRuleName());
        if (tarGrp != null) {
            chiledMktStrategyConfRuleDO.setTarGrpId(tarGrp.getTarGrpId());
        }
        chiledMktStrategyConfRuleDO.setProductId(childProductIds);
        chiledMktStrategyConfRuleDO.setEvtContactConfId(childEvtContactConfIds);
        chiledMktStrategyConfRuleDO.setMktCamChlResultId(childMktCamChlResultIds);
        chiledMktStrategyConfRuleDO.setCreateDate(new Date());
        chiledMktStrategyConfRuleDO.setCreateStaff(UserUtil.loginId());
        chiledMktStrategyConfRuleDO.setUpdateDate(new Date());
        chiledMktStrategyConfRuleDO.setUpdateStaff(UserUtil.loginId());
        mktStrategyConfRuleMapper.insert(chiledMktStrategyConfRuleDO);
        mktStrategyConfRuleMap.put("mktStrategyConfRuleId", chiledMktStrategyConfRuleDO.getMktStrategyConfRuleId());
        return mktStrategyConfRuleMap;
    }


    /**
     * 通过规则内容复制策略规则
     *
     * @param
     * @return
     */
    @Override
    public Map<String, Object> copyMktStrategyConfRule(List<MktStrategyConfRule> mktStrategyConfRuleList) throws Exception {
        Map<String, Object> ruleListMap = new HashMap<>();
        //初始化结果集
        Future<Map<String, Object>> ruleFuture = null;
        List<Future<Map<String, Object>>> threadList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        logger.info("MktStrategyConfRule-->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>开始：" + simpleDateFormat.format(new Date()));

        for (MktStrategyConfRule mktStrategyConfRule : mktStrategyConfRuleList) {
            //初始化线程池
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ExecutorService executorService = Executors.newCachedThreadPool();
            ruleFuture = executorService.submit(new CopyMktStrategyConfRuleTask(mktStrategyConfRule));
            threadList.add(ruleFuture);
        }

        List<MktStrategyConfRule> ruleNewList = new ArrayList<>();
        for (Future<Map<String, Object>> ruleFutureNew : threadList) {
            if (ruleFutureNew != null && ruleFutureNew.get() != null) {
                MktStrategyConfRule ruleNew = (MktStrategyConfRule) ruleFutureNew.get().get("mktStrategyConfRule");
                ruleNewList.add(ruleNew);
            }
            ruleListMap.put("mktStrategyConfRuleList", ruleNewList);
        }

        return ruleListMap;
    }


    class CopyTarGrpTask implements Callable<Map<String, Object>> {
        private Long tarGrpId;

        public CopyTarGrpTask(Long tarGrpId) {
            this.tarGrpId = tarGrpId;
        }

        @Override
        public Map<String, Object> call() throws Exception {
            Map<String, Object> tarGrpMap = tarGrpService.copyTarGrp(tarGrpId, false);
            TarGrp tarGrp = (TarGrp) tarGrpMap.get("tarGrp");
            if (tarGrp != null) {
                tarGrpId = tarGrp.getTarGrpId();
            }
            return tarGrpMap;
        }
    }

    class CopyProductRuleTask implements Callable<Map<String, Object>> {
        private List<Long> productIdlist;

        public CopyProductRuleTask(List<Long> productIdlist) {
            this.productIdlist = productIdlist;
        }

        @Override
        public Map<String, Object> call() throws Exception {
            Map<String, Object> productRuleMap = productService.copyProductRule(UserUtil.loginId(), productIdlist);
            return productRuleMap;
        }
    }

    class CopyMktCamChlConfTask implements Callable<Map<String, Object>> {
        private List<MktCamChlConfDetail> mktCamChlConfDetailList;

        public CopyMktCamChlConfTask(List<MktCamChlConfDetail> mktCamChlConfDetailList) {
            this.mktCamChlConfDetailList = mktCamChlConfDetailList;
        }

        @Override
        public Map<String, Object> call() throws Exception {
            Map<String, Object> mktCamChlConfDetailMap = new HashMap<>();
            List<MktCamChlConfDetail> mktCamChlConfDetailNewList = new ArrayList<>();
            if (mktCamChlConfDetailList != null && mktCamChlConfDetailList.size() > 0) {
                for (MktCamChlConfDetail mktCamChlConf : mktCamChlConfDetailList) {
                    Map<String, Object> mktCamChlConfMap = mktCamChlConfService.copyMktCamChlConfFormRedis(mktCamChlConf.getEvtContactConfId(), mktCamChlConf.getScriptDesc());
                    MktCamChlConfDetail mktCamChlConfDetail = (MktCamChlConfDetail) mktCamChlConfMap.get("mktCamChlConfDetail");
                    CamScript camScript = new CamScript();
                    camScript.setScriptDesc(mktCamChlConf.getScriptDesc());
                    mktCamChlConfDetail.setScriptDesc(mktCamChlConf.getScriptDesc());
                    //   redisUtils.set("MktCamChlConfDetail_" + mktCamChlConfDetail.getEvtContactConfId(), mktCamChlConfDetail);
                    //   MktCamChlConf camChlConf = BeanUtil.create(mktCamChlConfDO, new MktCamChlConf());
                    mktCamChlConfDetailNewList.add(mktCamChlConfDetail);
                }
                mktCamChlConfDetailMap.put("mktCamChlConfDetailList", mktCamChlConfDetailNewList);
            }
            return mktCamChlConfDetailMap;
        }
    }


    class CopyMktCamChlResultTask implements Callable<Map<String, Object>> {
        private List<MktCamChlResult> mktCamChlResultList;

        public CopyMktCamChlResultTask(List<MktCamChlResult> mktCamChlResultList) {
            this.mktCamChlResultList = mktCamChlResultList;
        }

        @Override
        public Map<String, Object> call() throws Exception {
            Map<String, Object> mktCamChlConfDetailMap = new HashMap<>();
            List<MktCamChlResult> mktCamChlResultNewList = new ArrayList<>();
            if (mktCamChlResultList != null && mktCamChlResultList.size() > 0) {
                for (MktCamChlResult mktCamChlResult : mktCamChlResultList) {
                    Map<String, Object> mktCamChlResultMap = mktCamChlResultService.copyMktCamChlResultFromRedis(mktCamChlResult);
                    MktCamChlResult childMktCamChlResult = (MktCamChlResult) mktCamChlResultMap.get("mktCamChlResult");
                    childMktCamChlResult.setMktCamChlResultId(null);
                    mktCamChlResultNewList.add(childMktCamChlResult);
                }
                mktCamChlConfDetailMap.put("mktCamChlResultList", mktCamChlResultNewList);
            }
            return mktCamChlConfDetailMap;
        }
    }

    /**
     * 更新规则下销售品
     *
     * @param productIdList
     * @param ruleId
     * @return
     */
    @Override
    public Map<String, Object> updateProductIds(List<Long> productIdList, Long ruleId) {
        Map<String, Object> mktStrategyConfRuleMap = new HashMap<>();
        MktStrategyConfRuleDO mktStrategyConfRuleDO = new MktStrategyConfRuleDO();
        String productIds = "";
        for (int i = 0; i < productIdList.size(); i++) {
            if (i == 0) {
                productIds += productIdList.get(i);
            } else {
                productIds += "/" + productIdList.get(i);
            }
        }
        mktStrategyConfRuleDO.setMktStrategyConfRuleId(ruleId);
        mktStrategyConfRuleDO.setProductId(productIds);
        mktStrategyConfRuleDO.setUpdateDate(new Date());
        mktStrategyConfRuleDO.setUpdateStaff(UserUtil.loginId());
        mktStrategyConfRuleMapper.updateByPrimaryKey(mktStrategyConfRuleDO);
        mktStrategyConfRuleMap.put("ruleId", ruleId);
        return mktStrategyConfRuleMap;
    }


    class CopyMktStrategyConfRuleTask implements Callable<Map<String, Object>> {
        private MktStrategyConfRule parentMktStrategyConfRule;

        public CopyMktStrategyConfRuleTask(MktStrategyConfRule mktStrategyConfRule) {
            this.parentMktStrategyConfRule = mktStrategyConfRule;
        }

        @Override
        public Map<String, Object> call() throws Exception {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            logger.info("MktStrategyConfRule-->>>开始：" + simpleDateFormat.format(new Date()));
            Map<String, Object> mktStrategyConfRuleMap = new HashMap<>();
            MktStrategyConfRule mktStrategyConfRule = new MktStrategyConfRule();
            /**
             * 客户分群配置
             */
            logger.info("-------------->>>>>>>>>>>>>>>>>>>>>>>>>复制规则开始：" + simpleDateFormat.format(new Date()));

            //初始化结果集
            List<Future<Map<String, Object>>> threadList = new ArrayList<>();
            //初始化线程池
            ExecutorService executorService = Executors.newCachedThreadPool();
            Future<Map<String, Object>> tarGrpFuture = null;
            if (parentMktStrategyConfRule.getTarGrpId() != null) {
                tarGrpFuture = executorService.submit(new CopyTarGrpTask(parentMktStrategyConfRule.getTarGrpId()));
                threadList.add(tarGrpFuture);
            }

            /**
             * 销售品配置
             */
            Future<Map<String, Object>> productFuture = null;
            if (parentMktStrategyConfRule.getProductIdlist() != null && parentMktStrategyConfRule.getProductIdlist().size() > 0) {
                productFuture = executorService.submit(new CopyProductRuleTask(parentMktStrategyConfRule.getProductIdlist()));
                threadList.add(productFuture);
            }
            /**
             * 协同渠道配置
             */
            Future<Map<String, Object>> mktCamChlConfFuture = null;
            if (parentMktStrategyConfRule.getMktCamChlConfDetailList() != null && parentMktStrategyConfRule.getMktCamChlConfDetailList().size() > 0) {
                mktCamChlConfFuture = executorService.submit(new CopyMktCamChlConfTask(parentMktStrategyConfRule.getMktCamChlConfDetailList()));
                threadList.add(mktCamChlConfFuture);
            }

            /**
             * 二次协同结果
             */
            Future<Map<String, Object>> mktCamChlResultFuture = null;
            if (parentMktStrategyConfRule.getMktCamChlResultList() != null && parentMktStrategyConfRule.getMktCamChlResultList().size() > 0) {
                mktCamChlResultFuture = executorService.submit(new CopyMktCamChlResultTask(parentMktStrategyConfRule.getMktCamChlResultList()));
                threadList.add(mktCamChlResultFuture);
            }
            mktStrategyConfRule.setMktStrategyConfRuleName(parentMktStrategyConfRule.getMktStrategyConfRuleName());

            // 从结果集中获取对应结果数据
            if (tarGrpFuture != null) {
                TarGrp tarGrp = (TarGrp) tarGrpFuture.get().get("tarGrp");
                mktStrategyConfRule.setTarGrpId(tarGrp.getTarGrpId());
            }

            if (productFuture != null) {
                List<Long> ruleIdList = (List<Long>) productFuture.get().get("ruleIdList");
                mktStrategyConfRule.setProductIdlist(ruleIdList);
            }

            if (mktCamChlConfFuture != null) {
                List<MktCamChlConfDetail> mktCamChlConfDetailList = (List<MktCamChlConfDetail>) mktCamChlConfFuture.get().get("mktCamChlConfDetailList");
                mktStrategyConfRule.setMktCamChlConfDetailList(mktCamChlConfDetailList);
            }

            if (mktCamChlResultFuture != null) {
                List<MktCamChlResult> mktCamChlResultList = (List<MktCamChlResult>) mktCamChlResultFuture.get().get("mktCamChlResultList");
                mktStrategyConfRule.setMktCamChlResultList(mktCamChlResultList);
            }
            executorService.shutdown();
            mktStrategyConfRuleMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfRuleMap.put("mktStrategyConfRule", mktStrategyConfRule);
            logger.info("-------------->>>>>>>>>>>>>>>>>>>>>>>>>复制规则结束：" + simpleDateFormat.format(new Date()));
            return mktStrategyConfRuleMap;
        }
    }

}