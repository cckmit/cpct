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
import com.zjtelcom.cpct.dao.campaign.MktCamResultRelMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamStrategyConfRelMapper;
import com.zjtelcom.cpct.dao.campaign.MktCpcAlgorithmsRulMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConf;
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
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
            if (mktStrategyConfRule.getMktCamChlConfList() != null) {
                for (int i = 0; i < mktStrategyConfRule.getMktCamChlConfList().size(); i++) {
                    if (i == 0) {
                        evtContactConfIds += mktStrategyConfRule.getMktCamChlConfList().get(i).getEvtContactConfId();
                    } else {
                        evtContactConfIds += "/" + mktStrategyConfRule.getMktCamChlConfList().get(i).getEvtContactConfId();
                    }
                    // 保存话术
                    CamScriptAddVO camScriptAddVO = new CamScriptAddVO();
                    camScriptAddVO.setEvtContactConfId(mktStrategyConfRule.getMktCamChlConfList().get(i).getEvtContactConfId());
                    camScriptAddVO.setMktCampaignId(mktStrategyConfRule.getMktCampaignId());
                    camScriptAddVO.setScriptDesc(mktStrategyConfRule.getMktCamChlConfList().get(i).getScriptDesc());
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
            //关联表
            MktCamStrategyConfRelDO mktCamStrategyConfRelDO = new MktCamStrategyConfRelDO();
            mktCamStrategyConfRelDO.setMktCampaignId(1L);
            mktCamStrategyConfRelDO.setStrategyConfId(mktCpcAlgorithmsRulDO.getAlgorithmsRulId());
            mktCamStrategyConfRelMapper.insert(mktCamStrategyConfRelDO);

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
            if (mktStrategyConfRule.getMktCamChlConfList() != null) {
                for (int i = 0; i < mktStrategyConfRule.getMktCamChlConfList().size(); i++) {
                    Long evtContactConfId = mktStrategyConfRule.getMktCamChlConfList().get(i).getEvtContactConfId();

                    CamScriptAddVO camScriptAddVO = new CamScriptAddVO();
                    camScriptAddVO.setEvtContactConfId(evtContactConfId);
                    camScriptAddVO.setMktCampaignId(mktStrategyConfRule.getMktCamChlConfList().get(i).getMktCampaignId());
                    camScriptAddVO.setScriptDesc(mktStrategyConfRule.getMktCamChlConfList().get(i).getScriptDesc());
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
            logger.error("[op:MktStrategyConfRuleServiceImpl] failed to update MktStrategyConfRule = {}, mktCamChlResultIds = {}", JSON.toJSON(mktStrategyConfRule), mktCamChlResultIds);
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
                List<MktCamChlConf> mktCamChlConfList = new ArrayList<>();
                for (int i = 0; i < evtContactConfIds.length; i++) {
                    if (evtContactConfIds[i] != "" && !"".equals(evtContactConfIds[i]) && !evtContactConfIds[i].equals(null)) {
                        MktCamChlConf mktCamChlConf = new MktCamChlConf();
                        mktCamChlConf.setEvtContactConfId(Long.valueOf(evtContactConfIds[i]));
                        String evtContactConfName = mktCamChlConfMapper.selectforName(Long.valueOf(evtContactConfIds[i]));
                        mktCamChlConf.setEvtContactConfName(evtContactConfName);
                        mktCamChlConfList.add(mktCamChlConf);
                    }
                }
                mktStrategyConfRule.setMktCamChlConfList(mktCamChlConfList);
            }

            if (mktStrategyConfRuleDO.getMktCamChlResultId() != null) {
                String[] mktCamChlResultIds = mktStrategyConfRuleDO.getMktCamChlResultId().split("/");
                List<MktCamChlResult> mktCamChlResultList = new ArrayList<>();
                for (String mktCamChlResultId : mktCamChlResultIds) {
                    Map<String, Object> mktCamChlResultMap = mktCamChlResultService.getMktCamChlResult(Long.valueOf(mktCamChlResultId));
                    mktCamChlResultList.add((MktCamChlResult) mktCamChlResultMap.get("mktCamChlResult"));
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
        String[] evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId().split("/");
        String childEvtContactConfIds = "";
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
    public Map<String, Object> copyMktStrategyConfRule(MktStrategyConfRule parentMktStrategyConfRule) throws Exception {
        Map<String, Object> mktStrategyConfRuleMap = new HashMap<>();
        MktStrategyConfRule mktStrategyConfRule = new MktStrategyConfRule();
        Long tarGrpId = null;
        /**
         * 客户分群配置
         */
        Map<String, Object> tarGrpMap = tarGrpService.copyTarGrp(parentMktStrategyConfRule.getTarGrpId(), false);
        TarGrp tarGrp = (TarGrp) tarGrpMap.get("tarGrp");
        if (tarGrp != null) {
            tarGrpId = tarGrp.getTarGrpId();
        }
        /**
         * 销售品配置
         */

        Map<String, Object> productRuleMap = productService.copyProductRule(UserUtil.loginId(), parentMktStrategyConfRule.getProductIdlist());
        List<Long> ruleIdList = (List<Long>) productRuleMap.get("ruleIdList");

        /**
         * 协同渠道配置
         */
        List<MktCamChlConf> mktCamChlConfList = new ArrayList<>();
        for (MktCamChlConf mktCamChlConf : parentMktStrategyConfRule.getMktCamChlConfList()) {
            Map<String, Object> mktCamChlConfMap = mktCamChlConfService.copyMktCamChlConf(mktCamChlConf.getEvtContactConfId());
            MktCamChlConfDO mktCamChlConfDO = (MktCamChlConfDO) mktCamChlConfMap.get("mktCamChlConfDO");
            MktCamChlConf camChlConf = BeanUtil.create(mktCamChlConfDO, new MktCamChlConf());
            mktCamChlConfList.add(camChlConf);
        }

        /**
         * 二次协同结果
         */
        List<MktCamChlResult> mktCamChlResultList = new ArrayList<>();
        for (MktCamChlResult mktCamChlResult : parentMktStrategyConfRule.getMktCamChlResultList()) {
//            Map<String, Object> mktCamChlResultMap = mktCamChlResultService.copyMktCamChlResult(mktCamChlResult.getMktCamChlResultId());
//            MktCamChlResultDO childMktCamChlResultDO = (MktCamChlResultDO) mktCamChlResultMap.get("mktCamChlResultDO");
            MktCamChlResult childMktCamChlResult = BeanUtil.create(mktCamChlResult, new MktCamChlResult());
            childMktCamChlResult.setMktCamChlResultId(null);
            mktCamChlResultList.add(childMktCamChlResult);
        }

        mktStrategyConfRule.setMktStrategyConfRuleName(parentMktStrategyConfRule.getMktStrategyConfRuleName());
        mktStrategyConfRule.setTarGrpId(tarGrpId);
        mktStrategyConfRule.setProductIdlist(ruleIdList);
        mktStrategyConfRule.setMktCamChlConfList(mktCamChlConfList);
        mktStrategyConfRule.setMktCamChlResultList(mktCamChlResultList);
        mktStrategyConfRuleMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        mktStrategyConfRuleMap.put("mktStrategyConfRule", mktStrategyConfRule);
        return mktStrategyConfRuleMap;
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
}